package edu.watumull.presencify.feature.teacher.auth.login

import androidx.lifecycle.viewModelScope
import edu.watumull.presencify.core.designsystem.components.dialog.DialogType
import edu.watumull.presencify.core.domain.onError
import edu.watumull.presencify.core.domain.onSuccess
import edu.watumull.presencify.core.domain.repository.teacher_auth.TeacherAuthRepository
import edu.watumull.presencify.core.presentation.UiText
import edu.watumull.presencify.core.presentation.components.dialog.DialogState
import edu.watumull.presencify.core.presentation.global_snackbar.SnackbarController
import edu.watumull.presencify.core.presentation.global_snackbar.SnackbarEvent
import edu.watumull.presencify.core.presentation.toUiText
import edu.watumull.presencify.core.presentation.utils.BaseViewModel
import kotlinx.coroutines.launch

class TeacherLoginViewModel(
    private val teacherAuthRepository: TeacherAuthRepository,
) : BaseViewModel<TeacherLoginState, TeacherLoginEvent, TeacherLoginAction>(
    initialState = TeacherLoginState()
) {

    override fun handleAction(action: TeacherLoginAction) {
        when (action) {
            is TeacherLoginAction.ChangeEmail -> {
                updateState {
                    it.copy(
                        email = action.email,
                        emailError = null
                    )
                }
            }

            is TeacherLoginAction.ChangePassword -> {
                updateState {
                    it.copy(
                        password = action.password,
                        passwordError = null
                    )
                }
            }

            is TeacherLoginAction.TogglePasswordVisibility -> {
                updateState { it.copy(isPasswordVisible = action.isVisible) }
            }

            is TeacherLoginAction.ClickLogin -> {
                performLogin()
            }

            is TeacherLoginAction.ClickBackButton -> {
                sendEvent(TeacherLoginEvent.NavigateBack)
            }

            is TeacherLoginAction.ClickForgotPassword -> {
                 sendEvent(TeacherLoginEvent.NavigateToForgotPassword)
            }

            is TeacherLoginAction.DismissDialog -> {
                updateState { it.copy(dialogState = null) }
            }
        }
    }

    private fun performLogin() {
        val currentState = state
        val email = currentState.email.trim()
        val password = currentState.password

        // Reset errors
        updateState {
            it.copy(
                emailError = null,
                passwordError = null
            )
        }

        // Validation - Basic blank checks only as per rules
        var hasErrors = false
        if (email.isBlank()) {
            updateState { it.copy(emailError = "Email is required") }
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
            teacherAuthRepository.loginTeacher(email, password)
                .onSuccess {
                    updateState { it.copy(isLoading = false) }

                    SnackbarController.sendEvent(SnackbarEvent("Login successful"))

                    sendEvent(TeacherLoginEvent.NavigateToHome)
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
