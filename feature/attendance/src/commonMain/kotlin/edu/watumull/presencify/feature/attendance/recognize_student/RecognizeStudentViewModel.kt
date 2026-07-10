package edu.watumull.presencify.feature.attendance.recognize_student

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import co.touchlab.kermit.Logger
import edu.watumull.presencify.core.designsystem.components.dialog.DialogType
import edu.watumull.presencify.core.domain.NtpClock
import edu.watumull.presencify.core.domain.enums.BiometricVerificationStatus
import edu.watumull.presencify.core.domain.onError
import edu.watumull.presencify.core.domain.onSuccess
import edu.watumull.presencify.core.domain.repository.attendance.AttendanceRepository
import edu.watumull.presencify.core.domain.repository.auth.UserRepository
import edu.watumull.presencify.core.domain.repository.student.StudentRepository
import edu.watumull.presencify.core.presentation.UiText
import edu.watumull.presencify.core.presentation.components.dialog.DialogState
import edu.watumull.presencify.core.presentation.global_snackbar.SnackbarController
import edu.watumull.presencify.core.presentation.global_snackbar.SnackbarController.sendEvent
import edu.watumull.presencify.core.presentation.global_snackbar.SnackbarEvent
import edu.watumull.presencify.core.presentation.toUiText
import edu.watumull.presencify.core.presentation.utils.BaseViewModel
import edu.watumull.presencify.feature.attendance.navigation.AttendanceRoutes
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.sqrt

class RecognizeStudentViewModel(
    savedStateHandle: SavedStateHandle,
    private val userRepository: UserRepository,
    private val studentRepository: StudentRepository,
    private val attendanceRepository: AttendanceRepository,
    private val ntpClock: NtpClock
) : BaseViewModel<RecognizeStudentState, RecognizeStudentEvent, RecognizeStudentAction>(
    initialState = RecognizeStudentState()
) {
    private val TAG = "RecognizeStudentVM"
    private val attendanceId = savedStateHandle.toRoute<AttendanceRoutes.RecognizeStudent>().attendanceId

    private var startTimeMillis: Long = 0
    private var lastStepCompletedTime: Long = 0
    private var timeoutActive: Boolean = false
    private val FACE_MATCH_THRESHOLD = 0.363f

    // Global 90s timeout job; scoped to screen lifetime
    private var globalTimeoutJob: Job? = null

    init {
        ntpClock.startPeriodicSync(viewModelScope)
        Logger.d(TAG) { "VM init: loading student data for attendanceId=$attendanceId" }
        loadStudentData()
    }

    private fun loadStudentData() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            val studentId = userRepository.getUserId().firstOrNull()
            Logger.d(TAG) { "loadStudentData(): userId=$studentId" }
            if (studentId == null) {
                updateState {
                    it.copy(
                        isLoading = false,
                        viewState = RecognizeStudentState.ViewState.Error(
                            UiText.DynamicString("User not logged in")
                        )
                    )
                }
                return@launch
            }

            val result = studentRepository.getStudentById(studentId)
            result.onSuccess { student ->
                mutableStateFlow.update { it.copy(storedFaceDescriptor = student.faceDescriptor) }

                val biometricVerificationStatus = student.biometricVerificationStatus
                if (biometricVerificationStatus != BiometricVerificationStatus.APPROVED) {
                    Logger.d(TAG) { "Biometric verification is not approved yet!" }
                    updateState {
                        it.copy(
                            isLoading = false,
                            viewState = RecognizeStudentState.ViewState.Error(UiText.DynamicString("Biometric verification is not approved yet!")),
                        )
                    }
                } else {
                    Logger.d(TAG) {
                        "Successfully loaded DB Face Descriptor. Size: ${state.storedFaceDescriptor?.size}"
                    }
                    updateState {
                        it.copy(
                            isLoading = false,
                            viewState = RecognizeStudentState.ViewState.Content,
                        )
                    }
                    startLivenessCheck()
                }
            }.onError { error ->
                Logger.e(TAG) { "loadStudentData(): error=$error" }
                updateState {
                    it.copy(
                        isLoading = false,
                        viewState = RecognizeStudentState.ViewState.Error(error.toUiText())
                    )
                }
            }
        }
    }

    /**
     * Local helper for liveness timing. If you later introduce a true monotonic
     * clock abstraction, you can replace this implementation there.
     */
    private fun nowMillis(): Long = ntpClock.now()

    private fun startLivenessCheck() {
        if (!ntpClock.isSynced()) {
            updateState {
                it.copy(
                    viewState = RecognizeStudentState.ViewState.Error(
                        UiText.DynamicString("Unable to synchronize network time. Please check your internet connection and try again.")
                    )
                )
            }
            return
        }

        val sequence = generateLivenessSequence()
        startTimeMillis = nowMillis()
        lastStepCompletedTime = startTimeMillis
        timeoutActive = true
        Logger.d(TAG) { "startLivenessCheck(): sequence=$sequence, startTimeMillis=$startTimeMillis" }

        updateState {
            it.copy(
                viewState = RecognizeStudentState.ViewState.Content,
                livenessSequence = sequence,
                currentStep = 0,
                isLivenessComplete = false,
                isRecognizing = false,
                shouldCaptureEmbedding = false,
                isLoading = false,
                error = it.error
            )
        }
    }

    private fun generateLivenessSequence(): List<HeadMovement> {
        val movements = HeadMovement.entries
        val sequence = mutableListOf<HeadMovement>()
        var lastMovement: HeadMovement? = null

        // First 3 steps: random LEFT/RIGHT/STRAIGHT with no immediate repetition
        repeat(3) {
            var nextMovement = movements.random()
            while (nextMovement == lastMovement) {
                nextMovement = movements.random()
            }
            sequence.add(nextMovement)
            lastMovement = nextMovement
        }

        // 4th (final) step: always STRAIGHT, so we capture on a straight face
        sequence.add(HeadMovement.STRAIGHT)

        return sequence
    }

    override fun handleAction(action: RecognizeStudentAction) {
        when (action) {
            is RecognizeStudentAction.NavigateBack -> {
                // Cancel global timeout when user leaves screen manually
                cancelGlobalTimeout()
                viewModelScope.launch { sendEvent(RecognizeStudentEvent.NavigateBack) }
            }

            is RecognizeStudentAction.OnPermissionResult -> {
                updateState { it.copy(cameraPermissionGranted = action.isGranted) }
            }

            is RecognizeStudentAction.OnFaceDetected -> {
                validateMovement(action.yaw)
            }

            is RecognizeStudentAction.OnLivenessSuccess -> { /* Handled implicitly */
            }

            is RecognizeStudentAction.OnRecognitionSuccess -> {
                compareSimilarityWithThreshold(action.similarity)
            }

            is RecognizeStudentAction.OnFailure -> {
                // Instead of showing raw error text on camera, route it through dialog
                showErrorDialog(action.message)
            }

            is RecognizeStudentAction.OnEmbeddingCaptureConsumed -> {
                updateState { it.copy(shouldCaptureEmbedding = false) }
            }

            is RecognizeStudentAction.OnCheatingDetected -> {
                val stepIndex = state.currentStep
                if (stepIndex >= 2 && !state.isLivenessComplete) {
                    Logger.d(TAG) { "Cheating suspected: face left camera during critical liveness steps (stepIndex=$stepIndex)." }
                    showErrorDialog(
                        UiText.DynamicString("Cheating suspected. Please retry the liveness check without leaving the camera."),
                        isCheating = true
                    )
                }
            }

            is RecognizeStudentAction.OnDismissDialog -> {
                updateState { it.copy(dialogState = null) }
            }

            is RecognizeStudentAction.OnRetryFromDialog -> {
                updateState {
                    it.copy(
                        dialogState = null,
                        isRecognizing = false,
                        isLivenessComplete = false,
                        shouldCaptureEmbedding = false,
                        isCheatingSuspected = false,
                        error = null,
                    )
                }
                startLivenessCheck()
            }

            // Screen lifecycle: start/cancel the 90s global timeout
            is RecognizeStudentAction.OnScreenStarted -> {
                startGlobalTimeoutIfNeeded()
            }

            is RecognizeStudentAction.OnScreenStopped -> {
                cancelGlobalTimeout()
            }

            is RecognizeStudentAction.OnGlobalTimeoutElapsed -> {
                handleGlobalTimeoutElapsed()
            }
        }
    }

    // Start a 90-second timeout tied to this screen. Only start once per entry.
    private fun startGlobalTimeoutIfNeeded() {
        if (state.isGlobalTimeoutActive || state.hasGlobalTimeoutFired) return

        Logger.d(TAG) { "Starting global 90s timeout for RecognizeStudent screen" }
        updateState { it.copy(isGlobalTimeoutActive = true) }

        globalTimeoutJob?.cancel()
        globalTimeoutJob = viewModelScope.launch {
            delay(90_000L)

            // If liveness already succeeded or timeout already handled, do nothing
            if (state.isLivenessComplete || state.hasGlobalTimeoutFired) {
                Logger.d(TAG) { "Global timeout job finished but liveness already completed or timeout handled." }
                return@launch
            }

            Logger.d(TAG) { "Global 90s timeout elapsed; sending OnGlobalTimeoutElapsed action" }
            updateState {
                it.copy(
                    isGlobalTimeoutActive = false,
                    hasGlobalTimeoutFired = true,
                )
            }
            // Route through normal MVI action handling
            trySendAction(RecognizeStudentAction.OnGlobalTimeoutElapsed)
        }
    }

    private fun cancelGlobalTimeout() {
        if (globalTimeoutJob == null && !state.isGlobalTimeoutActive) return
        Logger.d(TAG) { "Cancelling global 90s timeout job" }
        globalTimeoutJob?.cancel()
        globalTimeoutJob = null
        updateState { it.copy(isGlobalTimeoutActive = false) }
    }

    private fun handleGlobalTimeoutElapsed() {
        Logger.d(TAG) { "Handling global timeout: backing out and showing snackbar" }

        // Stop any ongoing liveness and prevent further recognition
        updateState {
            it.copy(
                isRecognizing = false,
                shouldCaptureEmbedding = false,
                isLivenessComplete = false,
                dialogState = null,
            )
        }

        viewModelScope.launch {
            // Show a global snackbar so user understands why they were navigated back
            SnackbarController.sendEvent(
                SnackbarEvent(
                    message = "Liveness verification timed out. Please try again.",
                )
            )
            // Then navigate back using the existing NavigateBack event
            sendEvent(RecognizeStudentEvent.NavigateBack)
        }
    }

    private fun validateMovement(yaw: Float) {
        // FIX 1: Completely ignore camera frames if a dialog is showing or liveness is done
        if (state.isLivenessComplete || state.dialogState != null) return

        val currentTime = nowMillis()
        Logger.d(TAG) {
            "validateMovement(): yaw=$yaw, currentTime=$currentTime, startTimeMillis=$startTimeMillis, elapsed=${currentTime - startTimeMillis}, lastStepCompletedTime=$lastStepCompletedTime"
        }

        // 15s timeout, only if a liveness run is active
        if (timeoutActive && currentTime - startTimeMillis > 15000) {
            Logger.d(TAG) { "Liveness timeout reached." }
            startLivenessCheck()
            showErrorDialog(UiText.DynamicString("Liveness timeout. Try again."))
            return
        }

        // 400ms Cooldown between step completions
        if (currentTime - lastStepCompletedTime < 400) return

        val requiredMovement = state.livenessSequence.getOrNull(state.currentStep) ?: return

        // Mirrored Front Camera Logic with slightly more forgiving thresholds
        val isCorrect = when (requiredMovement) {
            HeadMovement.LEFT -> yaw > 15f
            HeadMovement.RIGHT -> yaw < -15f
            HeadMovement.STRAIGHT -> yaw in -12f..12f
        }

        // --- Cheating / Spoofing Detection Logic ---
        // Give a 1-second grace period at step 0 so users don't instantly fail if they start slightly askew
        if (state.currentStep > 0 || currentTime - startTimeMillis > 1000) {
            val previousMovement = if (state.currentStep > 0) {
                state.livenessSequence[state.currentStep - 1]
            } else {
                HeadMovement.STRAIGHT
            }

            // Using 20f to be slightly more forgiving of natural bobbing/leaning before determining an intentional opposite look
            val isCheating = when (requiredMovement) {
                HeadMovement.LEFT -> {
                    if (previousMovement == HeadMovement.STRAIGHT) yaw < -20f else false
                }

                HeadMovement.RIGHT -> {
                    if (previousMovement == HeadMovement.STRAIGHT) yaw > 20f else false
                }

                HeadMovement.STRAIGHT -> {
                    when (previousMovement) {
                        HeadMovement.LEFT -> {
                            yaw < -20f
                        }

                        HeadMovement.RIGHT -> {
                            yaw > 20f
                        }

                        else -> {
                            yaw > 20f || yaw < -20f
                        }
                    }
                }
            }

            if (isCheating) {
                Logger.d(TAG) { "Cheating detected: opposite direction movement! req=$requiredMovement, prev=$previousMovement, yaw=$yaw" }
                showErrorDialog(
                    UiText.DynamicString("Cheating suspected: Incorrect head movement detected. Please follow the instructions."),
                    isCheating = true
                )
                return
            }
        }
        // --- END Cheating Detection ---

        Logger.d(TAG) { "validateMovement(): required=$requiredMovement, isCorrect=$isCorrect, step=${state.currentStep}" }

        if (isCorrect) {
            lastStepCompletedTime = currentTime
            val nextStep = state.currentStep + 1

            if (nextStep >= state.livenessSequence.size) {
                Logger.d(TAG) { "Liveness complete! Triggering capture state on final STRAIGHT step." }
                updateState {
                    it.copy(
                        currentStep = nextStep,
                        isLivenessComplete = true,
                        isRecognizing = true,
                        shouldCaptureEmbedding = true,
                        error = null
                    )
                }
            } else {
                updateState { it.copy(currentStep = nextStep, error = null) }
            }
        }
    }

    private fun showErrorDialog(message: UiText, isCheating: Boolean = false) {
        // Stop the current liveness run; a new one will start when user taps Retry
        timeoutActive = false

        updateState {
            it.copy(
                isRecognizing = false,
                shouldCaptureEmbedding = false,
                isLivenessComplete = false,
                isCheatingSuspected = isCheating,
                error = null,
                currentStep = 0, // FIX 2: Hard-reset the step counter so they can't resume
                dialogState = DialogState(
                    dialogType = DialogType.ERROR,
                    title = UiText.DynamicString(if (isCheating) "Cheating Suspected" else "Face Not Recognized"),
                    message = message,
                )
            )
        }
    }

    private fun compareSimilarityWithThreshold(similarity: Float) {
        if (similarity >= FACE_MATCH_THRESHOLD) {
            Logger.d(TAG) { "Similarity = $similarity" }
            Logger.d(TAG) { "Similarity >= $FACE_MATCH_THRESHOLD Marking attendance..." }
            markAttendance()
        } else {
            Logger.d(TAG) { "Similarity < $FACE_MATCH_THRESHOLD Recognition Failed." }
            val score = (similarity * 100).toInt().coerceAtLeast(0)
            showErrorDialog(
                UiText.DynamicString(
                    "Face not recognized. Try again."
                )
            )
            startLivenessCheck()
        }
    }

    private fun markAttendance() {
        viewModelScope.launch {
            val studentId = userRepository.getUserId().firstOrNull()
            if (studentId != null) {
                attendanceRepository.markAttendance(attendanceId, studentId)
                    .onSuccess {
                        Logger.d(TAG) { "Network Call: Attendance marked successfully!" }
                        SnackbarController.sendEvent(SnackbarEvent(message = "Attendance marked successfully!"))
                        updateState {
                            it.copy(
                                isRecognizing = false,
                                isLivenessComplete = true,
                                shouldCaptureEmbedding = false,
                                error = null
                            )
                        }
                        sendEvent(RecognizeStudentEvent.MapsToSuccess)
                    }
                    .onError { error ->
                        Logger.e(TAG) { "Network Call: Failed to mark attendance: $error" }
                        SnackbarController.sendEvent(SnackbarEvent(message = "Failed to mark attendance"))
                        updateState {
                            it.copy(
                                isRecognizing = false,
                                isLivenessComplete = false,
                                shouldCaptureEmbedding = false
                            )
                        }
                        startLivenessCheck()
                    }
            } else {
                updateState {
                    it.copy(
                        error = UiText.DynamicString("User not logged in"),
                        isRecognizing = false,
                        shouldCaptureEmbedding = false
                    )
                }
            }
        }
    }
}

