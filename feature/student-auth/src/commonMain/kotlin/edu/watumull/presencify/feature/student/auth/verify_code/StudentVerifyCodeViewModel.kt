package edu.watumull.presencify.feature.student.auth.verify_code

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import edu.watumull.presencify.core.designsystem.components.dialog.DialogType
import edu.watumull.presencify.core.domain.onError
import edu.watumull.presencify.core.domain.onSuccess
import edu.watumull.presencify.core.domain.repository.student_auth.StudentAuthRepository
import edu.watumull.presencify.core.presentation.UiText
import edu.watumull.presencify.core.presentation.components.dialog.DialogState
import edu.watumull.presencify.core.presentation.global_snackbar.SnackbarController
import edu.watumull.presencify.core.presentation.global_snackbar.SnackbarEvent
import edu.watumull.presencify.core.presentation.toUiText
import edu.watumull.presencify.core.presentation.utils.BaseViewModel
import edu.watumull.presencify.feature.student.auth.navigation.StudentAuthRoutes
import kotlinx.coroutines.launch

class StudentVerifyCodeViewModel(
    private val studentAuthRepository: StudentAuthRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<StudentVerifyCodeState, StudentVerifyCodeEvent, StudentVerifyCodeAction>(
    initialState = StudentVerifyCodeState(
        email = savedStateHandle.toRoute<StudentAuthRoutes.StudentVerifyCode>().email
    )
) {

    override fun handleAction(action: StudentVerifyCodeAction) {
        when (action) {
            is StudentVerifyCodeAction.ChangeCode -> {
                updateState {
                    it.copy(
                        code = action.code,
                        codeError = null
                    )
                }
            }

            is StudentVerifyCodeAction.ClickVerifyCode -> {
                verifyCode()
            }

            is StudentVerifyCodeAction.ClickBackButton -> {
                sendEvent(StudentVerifyCodeEvent.NavigateBack)
            }

            is StudentVerifyCodeAction.DismissDialog -> {
                updateState { it.copy(dialogState = null) }
            }
        }
    }

    private fun verifyCode() {
        val currentState = state
        val email = currentState.email.trim()
        val code = currentState.code.trim()

        // Reset errors
        updateState {
            it.copy(codeError = null)
        }

        // Validation
        if (code.isBlank()) {
            updateState {
                it.copy(codeError = "Code can't be blank")
            }
            return
        }

        if (email.isBlank()) {
            viewModelScope.launch {
                SnackbarController.sendEvent(SnackbarEvent("Technical error occurred, please restart the app"))
            }
            return
        }

        // Start loading
        updateState {
            it.copy(isLoading = true)
        }

        viewModelScope.launch {
            studentAuthRepository.verifyCode(email, code)
                .onSuccess {
                    updateState { it.copy(isLoading = false) }

                    SnackbarController.sendEvent(SnackbarEvent("Verification successful"))

                    sendEvent(StudentVerifyCodeEvent.NavigateToHome)
                }
                .onError { error ->
                    updateState {
                        it.copy(
                            isLoading = false,
                            dialogState = DialogState(
                                title = UiText.DynamicString("Verification Failed"),
                                message = error.toUiText(),
                                dialogType = DialogType.ERROR
                            )
                        )
                    }
                }
        }
    }
}
