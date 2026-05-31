package edu.watumull.presencify.feature.student.auth.forgot_password

import androidx.lifecycle.viewModelScope
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
import edu.watumull.presencify.core.presentation.validation.validateAsEmail
import kotlinx.coroutines.launch

class StudentForgotPasswordViewModel(
    private val studentAuthRepository: StudentAuthRepository,
) : BaseViewModel<StudentForgotPasswordState, StudentForgotPasswordEvent, StudentForgotPasswordAction>(
    initialState = StudentForgotPasswordState()
) {

    override fun handleAction(action: StudentForgotPasswordAction) {
        when (action) {
            is StudentForgotPasswordAction.ChangeEmail -> {
                updateState {
                    it.copy(
                        email = action.email,
                        emailError = null
                    )
                }
            }

            is StudentForgotPasswordAction.ClickSendCode -> {
                sendVerificationCode()
            }

            is StudentForgotPasswordAction.ClickBackButton -> {
                sendEvent(StudentForgotPasswordEvent.NavigateBack)
            }

            is StudentForgotPasswordAction.DismissDialog -> {
                updateState { it.copy(dialogState = null) }
            }
        }
    }

    private fun sendVerificationCode() {
        val currentState = state
        val email = currentState.email.trim()

        // Reset errors
        updateState {
            it.copy(emailError = null)
        }

        // Validation
        val emailValidationResult = email.validateAsEmail()
        if (!emailValidationResult.successful) {
            updateState {
                it.copy(emailError = emailValidationResult.errorMessage)
            }
            return
        }

        // Start loading
        updateState {
            it.copy(isLoading = true)
        }

        viewModelScope.launch {
            studentAuthRepository.sendVerificationCodeToEmail(email)
                .onSuccess {
                    updateState { it.copy(isLoading = false) }

                    SnackbarController.sendEvent(SnackbarEvent("Verification code sent to $email"))

                    sendEvent(StudentForgotPasswordEvent.NavigateToVerifyCode(email))
                }
                .onError { error ->
                    updateState {
                        it.copy(
                            isLoading = false,
                            dialogState = DialogState(
                                title = UiText.DynamicString("Failed to Send Code"),
                                message = error.toUiText(),
                                dialogType = DialogType.ERROR
                            )
                        )
                    }
                }
        }
    }
}
