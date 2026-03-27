package edu.watumull.presencify.feature.attendance.recognize_student

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import co.touchlab.kermit.Logger
import edu.watumull.presencify.core.domain.NtpClock
import edu.watumull.presencify.core.domain.onError
import edu.watumull.presencify.core.domain.onSuccess
import edu.watumull.presencify.core.domain.repository.attendance.AttendanceRepository
import edu.watumull.presencify.core.domain.repository.auth.UserRepository
import edu.watumull.presencify.core.domain.repository.student.StudentRepository
import edu.watumull.presencify.core.presentation.UiText
import edu.watumull.presencify.core.presentation.toUiText
import edu.watumull.presencify.core.presentation.utils.BaseViewModel
import edu.watumull.presencify.feature.attendance.navigation.AttendanceRoutes
import edu.watumull.presencify.core.presentation.global_snackbar.SnackbarController
import edu.watumull.presencify.core.presentation.global_snackbar.SnackbarEvent
import edu.watumull.presencify.core.design.systems.components.dialog.DialogType
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.firstOrNull
import kotlin.math.min
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
    private var storedFaceDescriptor: List<Float>? = null
    private var timeoutActive: Boolean = false

    init {
        Logger.d(TAG) { "VM init: loading student data for attendanceId=$attendanceId" }
        loadStudentData()
    }

    private fun loadStudentData() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            val studentId = userRepository.getUserId().firstOrNull()
            Logger.d(TAG) { "loadStudentData(): userId=$studentId" }
            if (studentId == null) {
                updateState { it.copy(isLoading = false, error = UiText.DynamicString("User not logged in")) }
                return@launch
            }

            val result = studentRepository.getStudentById(studentId)
            result.onSuccess { student ->
                storedFaceDescriptor = student.faceDescriptor
                if (storedFaceDescriptor == null) {
                    Logger.d(TAG) { "Student profile has no Face Descriptor registered!" }
                    updateState {
                        it.copy(
                            isLoading = false,
                            error = UiText.DynamicString("Face not registered in database.")
                        )
                    }
                } else {
                    Logger.d(TAG) {
                        "Successfully loaded DB Face Descriptor. Size: ${storedFaceDescriptor?.size}"
                    }
                    startLivenessCheck()
                }
            }.onError { error ->
                Logger.e(TAG) { "loadStudentData(): error=$error" }
                updateState { it.copy(isLoading = false, error = error.toUiText()) }
            }
        }
    }

    /**
     * Local helper for liveness timing. If you later introduce a true monotonic
     * clock abstraction, you can replace this implementation there.
     */
    private fun nowMillis(): Long = ntpClock.getCurrentNtpTimeMs()

    private fun startLivenessCheck() {
        val sequence = generateLivenessSequence()
        startTimeMillis = nowMillis()
        lastStepCompletedTime = startTimeMillis
        timeoutActive = true
        Logger.d(TAG) { "startLivenessCheck(): sequence=$sequence, startTimeMillis=$startTimeMillis" }

        updateState {
            it.copy(
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
                compareFaceEmbedding(action.embedding)
            }

            is RecognizeStudentAction.OnRecognitionSuccessWithMirror -> {
                compareFaceEmbeddingWithMirror(action.original, action.mirrored)
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
                dialogState = RecognizeStudentDialogState(
                    isVisible = true,
                    dialogType = DialogType.ERROR,
                    title = if (isCheating) "Cheating Suspected" else "Face Not Recognized",
                    message = message,
                )
            )
        }
    }

    private fun validateMovement(yaw: Float) {
        if (state.isLivenessComplete) return

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

    private fun compareFaceEmbedding(cameraEmbedding: FloatArray) {
        val stored = storedFaceDescriptor
        if (stored == null) {
            Logger.e(TAG) { "FATAL: compareFaceEmbedding called but storedFaceDescriptor is NULL." }
            showErrorDialog(UiText.DynamicString("No face registered"))
            return
        }

        val dbArray = stored.toFloatArray()

        Logger.d(TAG) {
            "Comparing Arrays -> Camera Model Output Size: ${cameraEmbedding.size} | Database Output Size: ${dbArray.size}"
        }
        Logger.d(TAG) { "Camera Array Start: ${cameraEmbedding.take(5)}" }
        Logger.d(TAG) { "DB Array Start: ${dbArray.take(5)}" }

        // Size Mismatch Check
        if (cameraEmbedding.size != dbArray.size) {
            val errorMsg =
                "CRITICAL AI MISMATCH: Camera generated ${cameraEmbedding.size} values, but Database has ${dbArray.size} values."
            Logger.e(TAG) { errorMsg }
            showErrorDialog(UiText.DynamicString(errorMsg))
            startLivenessCheck()
            return
        }

        try {
            val normCamera = l2Normalize(cameraEmbedding)
            val normDb = l2Normalize(dbArray)
            val distance = euclideanDistance(normCamera, normDb)

            Logger.d(TAG) { "Math Calculation Complete. Normalized Distance: $distance" }

            if (distance < 0.9f) {
                Logger.d(TAG) { "Distance < 0.9! Marking attendance..." }
                markAttendance()
            } else {
                Logger.d(TAG) { "Distance >= 0.9. Recognition Failed." }
                showErrorDialog(
                    UiText.DynamicString(
                        "Face not recognized (Distance: ${distance.toString().take(4)}). Try again."
                    )
                )
                startLivenessCheck()
            }
        } catch (e: Exception) {
            Logger.e(TAG, e) { "Math crash in euclideanDistance" }
            showErrorDialog(UiText.DynamicString("Math Error: ${e.message}"))
            startLivenessCheck()
        }
    }

    private fun compareFaceEmbeddingWithMirror(originalEmbedding: FloatArray, mirroredEmbedding: FloatArray) {
        val stored = storedFaceDescriptor
        if (stored == null) {
            Logger.e(TAG) { "FATAL: compareFaceEmbeddingWithMirror called but storedFaceDescriptor is NULL." }
            showErrorDialog(UiText.DynamicString("No face registered"))
            return
        }

        val dbArray = stored.toFloatArray()

        Logger.d(TAG) {
            "Comparing Arrays (mirror-aware) -> Original Size: ${originalEmbedding.size}, Mirrored Size: ${mirroredEmbedding.size}, DB Size: ${dbArray.size}"
        }

        // Size Mismatch Check
        if (originalEmbedding.size != dbArray.size || mirroredEmbedding.size != dbArray.size) {
            val errorMsg = "CRITICAL AI MISMATCH: Embedding sizes do not match database descriptor."
            Logger.e(TAG) { errorMsg }
            showErrorDialog(UiText.DynamicString(errorMsg))
            startLivenessCheck()
            return
        }

        try {
            val normOriginal = l2Normalize(originalEmbedding)
            val normMirrored = l2Normalize(mirroredEmbedding)
            val normDb = l2Normalize(dbArray)
            Logger.d(TAG) { "Original Camera Array: ${originalEmbedding.take(5)}" }
            Logger.d(TAG) { "Mirrored Camera Array: ${mirroredEmbedding.take(5)}" }
            Logger.d(TAG) { "Database Array: ${dbArray.take(5)}" }
            val distOriginal = euclideanDistance(normOriginal, normDb)
            val distMirrored = euclideanDistance(normMirrored, normDb)
            val bestDist = min(distOriginal, distMirrored)

            Logger.d(TAG) { "Mirror-aware distance: original=$distOriginal, mirrored=$distMirrored, best=$bestDist" }

            if (bestDist < 0.9f) {
                Logger.d(TAG) { "Best distance < 0.9 Marking attendance..." }
                markAttendance()
            } else {
                Logger.d(TAG) { "Best distance >= 0.9. Recognition Failed." }
                showErrorDialog(
                    UiText.DynamicString(
                        "Face not recognized (Distance: ${bestDist.toString().take(4)}). Try again."
                    )
                )
                startLivenessCheck()
            }
        } catch (e: Exception) {
            Logger.e(TAG, e) { "Math crash in euclideanDistance (mirror-aware)" }
            showErrorDialog(UiText.DynamicString("Math Error: ${e.message}"))
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

    private fun l2Normalize(v: FloatArray): FloatArray {
        val norm = sqrt(v.fold(0f) { acc, next -> acc + (next * next) })
        return if (norm != 0f) v.map { it / norm }.toFloatArray() else v
    }

    private fun euclideanDistance(a: FloatArray, b: FloatArray): Float {
        if (a.size != b.size) return Float.MAX_VALUE
        var sum = 0f
        for (i in a.indices) {
            val diff = a[i] - b[i]
            sum += diff * diff
        }
        return sqrt(sum)
    }
}