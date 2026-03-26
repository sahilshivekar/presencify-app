package edu.watumull.presencify.feature.attendance.recognize_student

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
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
import kotlin.math.sqrt

class RecognizeStudentViewModel(
    savedStateHandle: SavedStateHandle,
    private val userRepository: UserRepository,
    private val studentRepository: StudentRepository,
    private val attendanceRepository: AttendanceRepository, // Inject AttendanceRepository
    private val ntpClock: NtpClock
) : BaseViewModel<RecognizeStudentState, RecognizeStudentEvent, RecognizeStudentAction>(
    initialState = RecognizeStudentState()
) {
    private val attendanceId = savedStateHandle.toRoute<AttendanceRoutes.RecognizeStudent>().attendanceId
    private var startTimeMillis: Long = 0
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
                    updateState { it.copy(isLoading = false, error = UiText.DynamicString("Face not registered")) }
                } else {
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
        updateState {
            it.copy(
                livenessSequence = sequence,
                currentStep = 0,
                isLivenessComplete = false,
                isRecognizing = false,
                shouldCaptureEmbedding = false,
                isLoading = false,
                error = null
            )
        }
    }

    private fun restartLivenessCheck() {
         startLivenessCheck()
    }

    private fun generateLivenessSequence(): List<HeadMovement> {
        val movements = HeadMovement.entries
        val sequence = mutableListOf<HeadMovement>()
        var lastMovement: HeadMovement? = null

        repeat(3) {
            var nextMovement = movements.random()
            while (nextMovement == lastMovement) {
                nextMovement = movements.random()
            }
            sequence.add(nextMovement)
            lastMovement = nextMovement
        }
        return sequence
    }

    override fun handleAction(action: RecognizeStudentAction) {
        when (action) {
            is RecognizeStudentAction.NavigateBack -> {
                viewModelScope.launch {
                    sendEvent(RecognizeStudentEvent.NavigateBack)
                }
            }
            is RecognizeStudentAction.OnPermissionResult -> {
                updateState { it.copy(cameraPermissionGranted = action.isGranted) }
            }
            is RecognizeStudentAction.OnFaceDetected -> {
                validateMovement(action.yaw)
            }
            is RecognizeStudentAction.OnLivenessSuccess -> {
                 // Handled within validateMovement mostly, but can be explicit
            }
            is RecognizeStudentAction.OnRecognitionSuccess -> {
                compareFaceEmbedding(action.embedding)
            }
            is RecognizeStudentAction.OnFailure -> {
                updateState { it.copy(error = action.message) }
            }
            is RecognizeStudentAction.OnEmbeddingCaptureConsumed -> {
                updateState { it.copy(shouldCaptureEmbedding = false) }
            }
        }
    }

    private fun validateMovement(yaw: Float) {
        if (state.isLivenessComplete || state.error != null) return

        // 8s timeout check
        if (ntpClock.getCurrentNtpTimeMs() - startTimeMillis > 8000) {
            restartLivenessCheck()
            return
        }

        val requiredMovement = state.livenessSequence.getOrNull(state.currentStep) ?: return

        val isCorrect = when (requiredMovement) {
            HeadMovement.LEFT -> yaw < -15
            HeadMovement.RIGHT -> yaw > 15
            HeadMovement.STRAIGHT -> yaw > -10 && yaw < 10
        }

        if (isCorrect) {
            val nextStep = state.currentStep + 1
            if (nextStep >= state.livenessSequence.size) {
                 updateState {
                     it.copy(
                         currentStep = nextStep,
                         isLivenessComplete = true,
                         isRecognizing = true,
                         shouldCaptureEmbedding = true
                     )
                 }
            } else {
                updateState { it.copy(currentStep = nextStep) }
            }
        } else {
             // Reset sequence if movement is clearly wrong?
             // The prompt says: "If a movement is incorrect, reset the sequence."
             // However, head movement is continuous. We shouldn't reset just because yaw is 0 when moving to 15.
             // We should reset if they do the WRONG gesture (e.g. Left instead of Right).
             // But detecting "wrong" gesture depends on thresholds.
             // If required is LEFT (< -15) and user looks RIGHT (> 15), that is incorrect.
             // If user is STRAIGHT (-10..10), it might be transition.

             if (isClearlyWrong(requiredMovement, yaw)) {
                 restartLivenessCheck()
             }
        }
    }

    private fun isClearlyWrong(required: HeadMovement, yaw: Float): Boolean {
        return when (required) {
            HeadMovement.LEFT -> yaw > 15 // Looked Right
            HeadMovement.RIGHT -> yaw < -15 // Looked Left
            HeadMovement.STRAIGHT -> yaw < -15 || yaw > 15 // Looked Left or Right
        }
    }

    private fun compareFaceEmbedding(embedding: FloatArray) {
        val stored = storedFaceDescriptor
        if (stored == null) {
             updateState { it.copy(error = UiText.DynamicString("No face registered"), isRecognizing = false, shouldCaptureEmbedding = false) }
             return
        }

        // Convert stored List<Float> to FloatArray
        val storedArray = stored.toFloatArray()

        val similarity = cosineSimilarity(embedding, storedArray)
        if (similarity >= 0.7) {
             markAttendance()
        } else {
             updateState {
                 it.copy(
                     isRecognizing = false,
                     isLivenessComplete = false,
                     shouldCaptureEmbedding = false,
                     error = UiText.DynamicString("Face not recognized. Try again.")
                 )
             }
             // Restart after failure
             restartLivenessCheck()
        }
    }

    private fun markAttendance() {
        viewModelScope.launch {
            val studentId = userRepository.getUserId().firstOrNull()
            if (studentId != null) {
                attendanceRepository.markAttendance(attendanceId, studentId)
                    .onSuccess {
                        SnackbarController.sendEvent(
                            SnackbarEvent(
                                message = "Attendance marked successfully!"
                            )
                        )
                        updateState {
                            it.copy(
                                isRecognizing = false,
                                isLivenessComplete = true,
                                shouldCaptureEmbedding = false
                            )
                        }
                        sendEvent(RecognizeStudentEvent.MapsToSuccess)
                    }
                    .onError { error ->
                        SnackbarController.sendEvent(
                            SnackbarEvent(
                                message = "Failed to mark attendance"
                            )
                        )
                         // Should we restart liveness check on network error? Or let user navigate back?
                         // Maybe just show error and let them try again?
                         updateState {
                             it.copy(
                                 isRecognizing = false,
                                 isLivenessComplete = false,
                                 shouldCaptureEmbedding = false
                             )
                         }
                         restartLivenessCheck()
                    }
            } else {
                updateState { it.copy(error = UiText.DynamicString("User not logged in"), isRecognizing = false, shouldCaptureEmbedding = false) }
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
