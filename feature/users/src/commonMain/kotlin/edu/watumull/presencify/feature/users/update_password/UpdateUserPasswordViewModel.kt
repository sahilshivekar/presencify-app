package edu.watumull.presencify.feature.users.update_password

import androidx.lifecycle.viewModelScope
import edu.watumull.presencify.core.designsystem.components.dialog.DialogType
import edu.watumull.presencify.core.domain.model.auth.UserRole
import edu.watumull.presencify.core.domain.onError
import edu.watumull.presencify.core.domain.onSuccess
import edu.watumull.presencify.core.domain.repository.auth.UserRepository
import edu.watumull.presencify.core.domain.repository.student_auth.StudentAuthRepository
import edu.watumull.presencify.core.domain.repository.teacher_auth.TeacherAuthRepository
import edu.watumull.presencify.core.presentation.UiText
import edu.watumull.presencify.core.presentation.components.dialog.DialogState
import edu.watumull.presencify.core.presentation.global_snackbar.SnackbarController
import edu.watumull.presencify.core.presentation.global_snackbar.SnackbarEvent
import edu.watumull.presencify.core.presentation.toUiText
import edu.watumull.presencify.core.presentation.utils.BaseViewModel
import edu.watumull.presencify.core.presentation.validation.validateAsPassword
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class UpdateUserPasswordViewModel(
    private val studentAuthRepository: StudentAuthRepository,
    private val teacherAuthRepository: TeacherAuthRepository,
    private val userRepository: UserRepository,
) : BaseViewModel<UpdateUserPasswordState, UpdateUserPasswordEvent, UpdateUserPasswordAction>(
    initialState = UpdateUserPasswordState()
) {

    override fun handleAction(action: UpdateUserPasswordAction) {
        when (action) {
            is UpdateUserPasswordAction.ChangePassword -> {
                updateState {
                    it.copy(
                        password = action.password,
                        passwordError = null,
                        confirmPasswordError = null
                    )
                }

                val validationResult = action.password.validateAsPassword()
                updateState {
                    it.copy(passwordError = if (validationResult.successful) null else validationResult.errorMessage)
                }

                if (state.confirmPassword.isNotEmpty()) {
                    validatePasswordMatch(action.password, state.confirmPassword)
                }
            }

            is UpdateUserPasswordAction.ChangeConfirmPassword -> {
                updateState {
                    it.copy(
                        confirmPassword = action.confirmPassword,
                        confirmPasswordError = null
                    )
                }
                validatePasswordMatch(state.password, action.confirmPassword)
            }

            UpdateUserPasswordAction.TogglePasswordVisibility -> {
                updateState { it.copy(isPasswordVisible = !it.isPasswordVisible) }
            }

            UpdateUserPasswordAction.ClickUpdatePassword -> {
                updatePassword()
            }

            UpdateUserPasswordAction.ClickBackButton -> {
                sendEvent(UpdateUserPasswordEvent.NavigateBack)
            }

            UpdateUserPasswordAction.DismissDialog -> {
                updateState { it.copy(dialogState = null) }
            }
        }
    }

    private fun validatePasswordMatch(password: String, confirmPassword: String) {
        if (confirmPassword.isNotEmpty() && password != confirmPassword) {
            updateState { it.copy(confirmPasswordError = "Passwords do not match") }
        } else {
            updateState { it.copy(confirmPasswordError = null) }
        }
    }

    private fun updatePassword() {
        val password = state.password
        val confirmPassword = state.confirmPassword

        val passwordValidationResult = password.validateAsPassword()
        val passwordsMatch = password == confirmPassword

        updateState {
            it.copy(
                passwordError = if (passwordValidationResult.successful) null else passwordValidationResult.errorMessage,
                confirmPasswordError = if (passwordsMatch) null else "Passwords do not match"
            )
        }

        if (!passwordValidationResult.successful || !passwordsMatch) {
            updateState { it.copy(isPasswordVisible = true) }
            return
        }

        updateState {
            it.copy(
                isUpdating = true,
                passwordError = null,
                confirmPasswordError = null,
                isPasswordVisible = false,
                dialogState = null
            )
        }

        viewModelScope.launch {
            val userRole = userRepository.getUserRole().firstOrNull()

            val result = when (userRole) {
                UserRole.STUDENT -> studentAuthRepository.updatePassword(password, confirmPassword)
                UserRole.TEACHER -> teacherAuthRepository.updatePassword(password, confirmPassword)
                else -> null
            }

            if (result == null) {
                updateState {
                    it.copy(
                        isUpdating = false,
                        dialogState = DialogState(
                            title = UiText.DynamicString("Not Allowed"),
                            message = UiText.DynamicString(
                                "Unable to determine your account role. Please log in again and retry."
                            ),
                            dialogType = DialogType.ERROR
                        )
                    )
                }
                return@launch
            }

            result
                .onSuccess {
                    updateState { it.copy(isUpdating = false) }
                    SnackbarController.sendEvent(SnackbarEvent("Password updated successfully."))
                    sendEvent(UpdateUserPasswordEvent.NavigateToMyDetails)
                }
                .onError { error ->
                    updateState {
                        it.copy(
                            isUpdating = false,
                            isPasswordVisible = true,
                            dialogState = DialogState(
                                title = UiText.DynamicString("Failed to Update Password"),
                                message = error.toUiText(),
                                dialogType = DialogType.ERROR
                            )
                        )
                    }
                }
        }
    }
}
