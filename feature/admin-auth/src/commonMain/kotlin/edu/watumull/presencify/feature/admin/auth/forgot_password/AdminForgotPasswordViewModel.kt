package edu.watumull.presencify.feature.admin.auth.forgot_password

import androidx.lifecycle.viewModelScope
import edu.watumull.presencify.core.design.systems.components.dialog.DialogType
import edu.watumull.presencify.core.domain.onError
import edu.watumull.presencify.core.domain.onSuccess
import edu.watumull.presencify.core.domain.repository.admin_auth.AdminAuthRepository
import edu.watumull.presencify.core.presentation.global_snackbar.SnackbarController
import edu.watumull.presencify.core.presentation.global_snackbar.SnackbarEvent
import edu.watumull.presencify.core.presentation.toUiText
import edu.watumull.presencify.core.presentation.utils.BaseViewModel
import edu.watumull.presencify.core.presentation.validation.validateAsEmail
import kotlinx.coroutines.launch

class AdminForgotPasswordViewModel(
    private val adminAuthRepository: AdminAuthRepository,
) : BaseViewModel<AdminForgotPasswordState, AdminForgotPasswordEvent, AdminForgotPasswordAction>(
    initialState = AdminForgotPasswordState()
) {

    override fun handleAction(action: AdminForgotPasswordAction) {
        when (action) {
            is AdminForgotPasswordAction.ChangeEmail -> {
                updateState {
                    it.copy(
                        email = action.email,
                        emailError = null
                    )
                }
            }

            is AdminForgotPasswordAction.ClickSendCode -> {
                sendVerificationCode()
            }

            is AdminForgotPasswordAction.ClickBackButton -> {
                sendEvent(AdminForgotPasswordEvent.NavigateBack)
            }

            is AdminForgotPasswordAction.DismissDialog -> {
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
            adminAuthRepository.sendVerificationCodeToEmailForForgotPassword(email)
                .onSuccess {
                    updateState { it.copy(isLoading = false) }

                    SnackbarController.sendEvent(SnackbarEvent("Verification code sent to $email"))

                    sendEvent(AdminForgotPasswordEvent.NavigateToVerifyCode(email))
                }
                .onError { error ->
                    updateState {
                        it.copy(
                            isLoading = false,
                            dialogState = AdminForgotPasswordState.DialogState(
                                isVisible = true,
                                title = "Failed to Send Code",
                                message = error.toUiText(),
                                dialogType = DialogType.ERROR,
                                dialogIntention = DialogIntention.FORGOT_PASSWORD_ERROR
                            )
                        )
                    }
                }
        }
    }
}
