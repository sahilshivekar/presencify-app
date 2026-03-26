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
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlin.math.max
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

    init {
        loadStudentData()
    }

    private fun loadStudentData() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            val studentId = userRepository.getUserId().firstOrNull()
            if (studentId == null) {
                updateState { it.copy(isLoading = false, error = UiText.DynamicString("User not logged in")) }
                return@launch
            }

            val result = studentRepository.getStudentById(studentId)
            result.onSuccess { student ->
                storedFaceDescriptor = student.faceDescriptor
                if (storedFaceDescriptor == null) {
                    Logger.d(TAG) { "Student profile has no Face Descriptor registered!" }
                    updateState { it.copy(isLoading = false, error = UiText.DynamicString("Face not registered in database.")) }
                } else {
                    Logger.d(TAG) { "Successfully loaded DB Face Descriptor. Size: ${storedFaceDescriptor?.size}" }
                    startLivenessCheck()
                }
            }.onError { error ->
                updateState { it.copy(isLoading = false, error = error.toUiText()) }
            }
        }
    }

    private fun startLivenessCheck() {
        val sequence = generateLivenessSequence()
        startTimeMillis = ntpClock.getCurrentNtpTimeMs()
        lastStepCompletedTime = startTimeMillis

        updateState {
            it.copy(
                livenessSequence = sequence,
                currentStep = 0,
                isLivenessComplete = false,
                isRecognizing = false,
                shouldCaptureEmbedding = false,
                isLoading = false,
                // Keep the error so the UI can display it during the restart
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
            is RecognizeStudentAction.OnLivenessSuccess -> { /* Handled implicitly */ }
            is RecognizeStudentAction.OnRecognitionSuccess -> {
                compareFaceEmbedding(action.embedding)
            }
            is RecognizeStudentAction.OnRecognitionSuccessWithMirror -> {
                compareFaceEmbeddingWithMirror(action.original, action.mirrored)
            }
            is RecognizeStudentAction.OnFailure -> {
                updateState { it.copy(error = action.message) }
            }
            is RecognizeStudentAction.OnEmbeddingCaptureConsumed -> {
                updateState { it.copy(shouldCaptureEmbedding = false) }
            }
            is RecognizeStudentAction.OnCheatingDetected -> {
                // Only treat as cheating if we are in later liveness steps (3rd or 4th),
                // where a friend might try to swap in a photo.
                val stepIndex = state.currentStep
                if (stepIndex >= 2 && !state.isLivenessComplete) {
                    Logger.d(TAG) { "Cheating suspected: face left camera during critical liveness steps (stepIndex=$stepIndex)." }
                    updateState {
                        it.copy(
                            isCheatingSuspected = true,
                            isRecognizing = false,
                            isLivenessComplete = false,
                            shouldCaptureEmbedding = false,
                            error = UiText.DynamicString("Cheating suspected. Please retry the liveness check without leaving the camera.")
                        )
                    }
                    startLivenessCheck()
                }
            }
        }
    }

    private fun validateMovement(yaw: Float) {
        if (state.isLivenessComplete || state.error != null) return

        val currentTime = ntpClock.getCurrentNtpTimeMs()

        // 15s timeout
        if (currentTime - startTimeMillis > 15000) {
            Logger.d(TAG) { "Liveness timeout reached." }
            updateState { it.copy(error = UiText.DynamicString("Liveness timeout. Try again.")) }
            startLivenessCheck()
            return
        }

        // 400ms Cooldown
        if (currentTime - lastStepCompletedTime < 400) return

        val requiredMovement = state.livenessSequence.getOrNull(state.currentStep) ?: return

        // Mirrored Front Camera Logic
        val isCorrect = when (requiredMovement) {
            HeadMovement.LEFT -> yaw > 15f
            HeadMovement.RIGHT -> yaw < -15f
            HeadMovement.STRAIGHT -> yaw in -12f..12f
        }

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
                        error = null // Clear errors on success
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
            updateState {
                it.copy(error = UiText.DynamicString("No face registered"), isRecognizing = false, shouldCaptureEmbedding = false)
            }
            return
        }

        val dbArray = stored.toFloatArray()

        Logger.d(TAG) { "Comparing Arrays -> Camera Model Output Size: ${cameraEmbedding.size} | Database Output Size: ${dbArray.size}" }

        // Size Mismatch Check
        if (cameraEmbedding.size != dbArray.size) {
            val errorMsg = "CRITICAL AI MISMATCH: Camera generated ${cameraEmbedding.size} values, but Database has ${dbArray.size} values."
            Logger.e(TAG) { errorMsg }
            updateState {
                it.copy(
                    isRecognizing = false,
                    isLivenessComplete = false,
                    shouldCaptureEmbedding = false,
                    error = UiText.DynamicString(errorMsg)
                )
            }
            startLivenessCheck()
            return
        }

        // Perform Math
        try {
            val similarity = cosineSimilarity(cameraEmbedding, dbArray)
            Logger.d(TAG) { "Math Calculation Complete. Cosine Similarity Score: $similarity" }

            // THRESHOLD
            if (similarity >= 0.7f) {
                Logger.d(TAG) { "Score >= 0.7! Marking attendance..." }
                markAttendance()
            } else {
                Logger.d(TAG) { "Score < 0.7. Recognition Failed." }
                updateState {
                    it.copy(
                        isRecognizing = false,
                        isLivenessComplete = false,
                        shouldCaptureEmbedding = false,
                        error = UiText.DynamicString("Face not recognized (Score: ${similarity.toString().take(4)}). Try again.")
                    )
                }
                startLivenessCheck()
            }
        } catch (e: Exception) {
            Logger.e(TAG, e) { "Math crash in cosineSimilarity" }
            updateState {
                it.copy(
                    isRecognizing = false,
                    isLivenessComplete = false,
                    shouldCaptureEmbedding = false,
                    error = UiText.DynamicString("Math Error: ${e.message}")
                )
            }
            startLivenessCheck()
        }
    }

    private fun compareFaceEmbeddingWithMirror(originalEmbedding: FloatArray, mirroredEmbedding: FloatArray) {
        val stored = storedFaceDescriptor
        if (stored == null) {
            Logger.e(TAG) { "FATAL: compareFaceEmbeddingWithMirror called but storedFaceDescriptor is NULL." }
            updateState {
                it.copy(error = UiText.DynamicString("No face registered"), isRecognizing = false, shouldCaptureEmbedding = false)
            }
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
            updateState {
                it.copy(
                    isRecognizing = false,
                    isLivenessComplete = false,
                    shouldCaptureEmbedding = false,
                    error = UiText.DynamicString(errorMsg)
                )
            }
            startLivenessCheck()
            return
        }

        try {
            val simOriginal = cosineSimilarity(originalEmbedding, dbArray)
            val simMirrored = cosineSimilarity(mirroredEmbedding, dbArray)
            val bestSim = max(simOriginal, simMirrored)

            Logger.d(TAG) { "Mirror-aware similarity: original=$simOriginal, mirrored=$simMirrored, best=$bestSim" }

            // THRESHOLD
            if (bestSim >= 0.7f) {
                Logger.d(TAG) { "Best score >= 0.7! Marking attendance..." }
                markAttendance()
            } else {
                Logger.d(TAG) { "Best score < 0.7. Recognition Failed." }
                updateState {
                    it.copy(
                        isRecognizing = false,
                        isLivenessComplete = false,
                        shouldCaptureEmbedding = false,
                        error = UiText.DynamicString(
                            "Face not recognized (Score: ${bestSim.toString().take(4)}). Try again."
                        )
                    )
                }
                startLivenessCheck()
            }
        } catch (e: Exception) {
            Logger.e(TAG, e) { "Math crash in cosineSimilarity (mirror-aware)" }
            updateState {
                it.copy(
                    isRecognizing = false,
                    isLivenessComplete = false,
                    shouldCaptureEmbedding = false,
                    error = UiText.DynamicString("Math Error: ${e.message}")
                )
            }
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
                        SnackbarController.sendEvent(SnackbarEvent(message = "Network Error: Failed to mark attendance"))
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
                    it.copy(error = UiText.DynamicString("User not logged in"), isRecognizing = false, shouldCaptureEmbedding = false)
                }
            }
        }
    }

    private fun cosineSimilarity(a: FloatArray, b: FloatArray): Float {
        if (a.size != b.size) return 0f
        var dotProduct = 0f
        var normA = 0f
        var normB = 0f
        for (i in a.indices) {
            dotProduct += a[i] * b[i]
            normA += a[i] * a[i]
            normB += b[i] * b[i]
        }
        return if (normA > 0 && normB > 0) {
            dotProduct / (sqrt(normA) * sqrt(normB))
        } else {
            0f
        }
    }
}