package edu.watumull.presencify.feature.users.review_student_biometrics

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import edu.watumull.presencify.core.designsystem.components.dialog.DialogType
import edu.watumull.presencify.core.domain.onError
import edu.watumull.presencify.core.domain.onSuccess
import edu.watumull.presencify.core.domain.repository.student.StudentRepository
import edu.watumull.presencify.core.presentation.UiText
import edu.watumull.presencify.core.presentation.components.dialog.DialogState
import edu.watumull.presencify.core.presentation.global_snackbar.SnackbarController
import edu.watumull.presencify.core.presentation.global_snackbar.SnackbarEvent
import edu.watumull.presencify.core.presentation.toUiText
import edu.watumull.presencify.core.presentation.utils.BaseViewModel
import edu.watumull.presencify.feature.users.navigation.UsersRoutes
import kotlinx.coroutines.launch

class ReviewStudentBiometricsViewModel(
    private val studentRepository: StudentRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<ReviewStudentBiometricsState, ReviewStudentBiometricsEvent, ReviewStudentBiometricsAction>(
    initialState = ReviewStudentBiometricsState(
        studentId = savedStateHandle.toRoute<UsersRoutes.ReviewStudentBiometrics>().studentId
    )
) {
    init {
        loadBiometrics()
    }

    override fun handleAction(action: ReviewStudentBiometricsAction) {
        when (action) {
            ReviewStudentBiometricsAction.NavigateBack -> sendEvent(ReviewStudentBiometricsEvent.NavigateBack)
            ReviewStudentBiometricsAction.ApproveStudentBiometrics -> approveBiometrics()
            ReviewStudentBiometricsAction.DismissDialog -> dismissDialog()
            ReviewStudentBiometricsAction.RetryLoad -> loadBiometrics()
        }
    }

    private fun loadBiometrics() {
        viewModelScope.launch {
            updateState { it.copy(viewState = ReviewStudentBiometricsState.ViewState.Loading) }

            val result = studentRepository.getStudentBiometrics(state.studentId)

            result.onSuccess { biometrics ->
                updateState {
                    it.copy(
                        viewState = ReviewStudentBiometricsState.ViewState.Content,
                        biometricStatus = biometrics.biometricVerificationStatus,
                        presignedUrls = biometrics.presignedUrls
                    )
                }
            }.onError { error ->
                updateState {
                    it.copy(
                        viewState = ReviewStudentBiometricsState.ViewState.Error(error.toUiText())
                    )
                }
            }
        }
    }

    private fun approveBiometrics() {
        viewModelScope.launch {
            updateState { it.copy(isApproving = true) }

            val result = studentRepository.verifyStudentBiometrics(state.studentId)

            result.onSuccess {
                SnackbarController.sendEvent(SnackbarEvent("Biometrics approved successfully"))
                sendEvent(ReviewStudentBiometricsEvent.NavigateBack)
            }.onError { error ->
                updateState {
                    it.copy(
                        isApproving = false,
                        dialogState = DialogState(
                            title = UiText.DynamicString("Error"),
                            message = error.toUiText(),
                            dialogType = DialogType.ERROR
                        )
                    )
                }
            }
        }
    }

    private fun dismissDialog() {
        updateState { it.copy(dialogState = null) }
    }
}
