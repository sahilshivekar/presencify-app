package edu.watumull.presencify.feature.student.auth.login

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
import kotlinx.coroutines.launch

class StudentLoginViewModel(
    private val studentAuthRepository: StudentAuthRepository,
) : BaseViewModel<StudentLoginState, StudentLoginEvent, StudentLoginAction>(
    initialState = StudentLoginState()
) {

    override fun handleAction(action: StudentLoginAction) {
        when (action) {
            is StudentLoginAction.ChangeEmailOrPRN -> {
                updateState {
                    it.copy(
                        emailOrPRN = action.emailOrPRN,
                        emailOrPRNError = null
                    )
                }
            }

            is StudentLoginAction.ChangePassword -> {
                updateState {
                    it.copy(
                        password = action.password,
                        passwordError = null
                    )
                }
            }

            is StudentLoginAction.TogglePasswordVisibility -> {
                updateState { it.copy(isPasswordVisible = action.isVisible) }
            }

            is StudentLoginAction.ClickLogin -> {
                performLogin()
            }

            is StudentLoginAction.ClickBackButton -> {
                sendEvent(StudentLoginEvent.NavigateBack)
            }

            is StudentLoginAction.ClickForgotPassword -> {
                sendEvent(StudentLoginEvent.NavigateToForgotPassword)
            }

            is StudentLoginAction.DismissDialog -> {
                updateState { it.copy(dialogState = null) }
            }
        }
    }

    private fun performLogin() {
        val currentState = state
        val emailOrPRN = currentState.emailOrPRN.trim()
        val password = currentState.password

        // Reset errors
        updateState {
            it.copy(
                emailOrPRNError = null,
                passwordError = null
            )
        }

        // Validation
        var hasErrors = false
        if (emailOrPRN.isBlank()) {
            updateState { it.copy(emailOrPRNError = "Email or PRN is required") }
            hasErrors = true
        }

        if (password.isBlank()) {
            updateState { it.copy(passwordError = "Password cannot be empty") }
            hasErrors = true
        }

        if (hasErrors) {
            return
        }

        // Start loading
        updateState {
            it.copy(
                isLoading = true,
                isPasswordVisible = false // Hide password during login
            )
        }

        viewModelScope.launch {
            studentAuthRepository.loginStudent(emailOrPRN, password)
                .onSuccess {
                    updateState { it.copy(isLoading = false) }

                    SnackbarController.sendEvent(SnackbarEvent("Login successful"))

                    sendEvent(StudentLoginEvent.NavigateToHome)
                }
                .onError { error ->

                    updateState {
                        it.copy(
                            isLoading = false,
                            dialogState = DialogState(
                                title = UiText.DynamicString("Login Failed"),
                                message = error.toUiText(),
                                dialogType = DialogType.ERROR
                            )
                        )
                    }
                }
        }
    }
}
