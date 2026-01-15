package edu.watumull.presencify.feature.admin.mgt.update_password

import androidx.lifecycle.viewModelScope
import edu.watumull.presencify.core.design.systems.components.dialog.DialogType
import edu.watumull.presencify.core.domain.onError
import edu.watumull.presencify.core.domain.onSuccess
import edu.watumull.presencify.core.domain.repository.admin_auth.AdminAuthRepository
import edu.watumull.presencify.core.presentation.global_snackbar.SnackbarController
import edu.watumull.presencify.core.presentation.global_snackbar.SnackbarEvent
import edu.watumull.presencify.core.presentation.toUiText
import edu.watumull.presencify.core.presentation.utils.BaseViewModel
import edu.watumull.presencify.core.presentation.validation.validateAsPassword
import kotlinx.coroutines.launch

class UpdatePasswordViewModel(
    private val adminAuthRepository: AdminAuthRepository,
) : BaseViewModel<UpdatePasswordState, UpdatePasswordEvent, UpdatePasswordAction>(
    initialState = UpdatePasswordState()
) {

    override fun handleAction(action: UpdatePasswordAction) {
        when (action) {
            is UpdatePasswordAction.ChangePassword -> {
                updateState {
                    it.copy(
                        password = action.password,
                        passwordError = null,
                        confirmPasswordError = null
                    )
                }

                // Validate on keystroke for forms
                val validationResult = action.password.validateAsPassword()
                updateState {
                    it.copy(
                        passwordError = if (validationResult.successful) null else validationResult.errorMessage
                    )
                }

                // Also check confirm password match if it's not empty
                if (state.confirmPassword.isNotEmpty()) {
                    validatePasswordMatch(action.password, state.confirmPassword)
                }
            }

            is UpdatePasswordAction.ChangeConfirmPassword -> {
                updateState {
                    it.copy(
                        confirmPassword = action.confirmPassword,
                        confirmPasswordError = null
                    )
                }

                // Validate password match
                validatePasswordMatch(state.password, action.confirmPassword)
            }

            is UpdatePasswordAction.TogglePasswordVisibility -> {
                updateState {
                    it.copy(isPasswordVisible = !it.isPasswordVisible)
                }
            }

            is UpdatePasswordAction.ClickUpdatePassword -> {
                updatePassword()
            }

            is UpdatePasswordAction.ClickBackButton -> {
                sendEvent(UpdatePasswordEvent.NavigateBack)
            }

            is UpdatePasswordAction.DismissDialog -> {
                updateState { it.copy(dialogState = null) }
            }
        }
    }

    private fun validatePasswordMatch(password: String, confirmPassword: String) {
        if (confirmPassword.isNotEmpty() && password != confirmPassword) {
            updateState {
                it.copy(confirmPasswordError = "Passwords do not match")
            }
        } else {
            updateState {
                it.copy(confirmPasswordError = null)
            }
        }
    }

    private fun updatePassword() {
        val currentState = state
        val password = currentState.password
        val confirmPassword = currentState.confirmPassword

        // Final validation on submit
        val passwordValidationResult = password.validateAsPassword()
        val passwordsMatch = password == confirmPassword

        updateState {
            it.copy(
                passwordError = if (passwordValidationResult.successful) null else passwordValidationResult.errorMessage,
                confirmPasswordError = if (passwordsMatch) null else "Passwords do not match"
            )
        }

        val hasError = !passwordValidationResult.successful || !passwordsMatch

        if (hasError) {
            updateState { it.copy(isPasswordVisible = true) }
            return
        }

        updateState {
            it.copy(
                isUpdating = true,
                passwordError = null,
                confirmPasswordError = null,
                isPasswordVisible = false
            )
        }

        viewModelScope.launch {
            adminAuthRepository.updateAdminPassword(password, confirmPassword)
                .onSuccess {
                    updateState { it.copy(isUpdating = false) }

                    SnackbarController.sendEvent(SnackbarEvent("Password updated successfully."))

                    sendEvent(UpdatePasswordEvent.NavigateToAdminDetails)
                }
                .onError { error ->
                    updateState {
                        it.copy(
                            isUpdating = false,
                            isPasswordVisible = true,
                            dialogState = UpdatePasswordState.DialogState(
                                isVisible = true,
                                title = "Failed to Update Password",
                                message = error.toUiText(),
                                dialogType = DialogType.ERROR,
                                dialogIntention = DialogIntention.ERROR
                            )
                        )
                    }
                }
        }
    }
}
