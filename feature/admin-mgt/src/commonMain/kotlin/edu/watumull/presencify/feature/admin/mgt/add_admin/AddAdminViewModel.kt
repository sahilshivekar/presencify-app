package edu.watumull.presencify.feature.admin.mgt.add_admin

import androidx.lifecycle.viewModelScope
import edu.watumull.presencify.core.designsystem.components.dialog.DialogType
import edu.watumull.presencify.core.domain.onError
import edu.watumull.presencify.core.domain.onSuccess
import edu.watumull.presencify.core.domain.repository.admin.AdminRepository
import edu.watumull.presencify.core.presentation.UiText
import edu.watumull.presencify.core.presentation.components.dialog.DialogState
import edu.watumull.presencify.core.presentation.global_snackbar.SnackbarController
import edu.watumull.presencify.core.presentation.global_snackbar.SnackbarEvent
import edu.watumull.presencify.core.presentation.toUiText
import edu.watumull.presencify.core.presentation.utils.BaseViewModel
import edu.watumull.presencify.core.presentation.validation.validateAsAdminUsername
import edu.watumull.presencify.core.presentation.validation.validateAsEmail
import edu.watumull.presencify.core.presentation.validation.validateAsPassword
import kotlinx.coroutines.launch

class AddAdminViewModel(
    private val adminRepository: AdminRepository,
) : BaseViewModel<AddAdminState, AddAdminEvent, AddAdminAction>(
    initialState = AddAdminState()
) {

    override fun handleAction(action: AddAdminAction) {
        when (action) {
            is AddAdminAction.ChangeEmail -> {
                updateState {
                    it.copy(
                        email = action.email,
                        emailError = null
                    )
                }

                val validationResult = action.email.validateAsEmail()
                updateState {
                    it.copy(
                        emailError = if (validationResult.successful) null else validationResult.errorMessage
                    )
                }
            }

            is AddAdminAction.ChangeUsername -> {
                updateState {
                    it.copy(
                        username = action.username,
                        usernameError = null
                    )
                }

                val validationResult = action.username.validateAsAdminUsername()
                updateState {
                    it.copy(
                        usernameError = if (validationResult.successful) null else validationResult.errorMessage
                    )
                }
            }

            is AddAdminAction.ChangePassword -> {
                updateState {
                    it.copy(
                        password = action.password,
                        passwordError = null,
                        confirmPasswordError = null
                    )
                }

                val validationResult = action.password.validateAsPassword()
                updateState {
                    it.copy(
                        passwordError = if (validationResult.successful) null else validationResult.errorMessage
                    )
                }

                if (state.confirmPassword.isNotEmpty()) {
                    validatePasswordMatch(action.password, state.confirmPassword)
                }
            }

            is AddAdminAction.ChangeConfirmPassword -> {
                updateState {
                    it.copy(
                        confirmPassword = action.confirmPassword,
                        confirmPasswordError = null
                    )
                }

                validatePasswordMatch(state.password, action.confirmPassword)
            }

            is AddAdminAction.TogglePasswordVisibility -> {
                updateState {
                    it.copy(isPasswordVisible = !it.isPasswordVisible)
                }
            }

            is AddAdminAction.ClickAddAdmin -> {
                addAdmin()
            }

            is AddAdminAction.NavigateBack -> handleBackNavigation()

            is AddAdminAction.ConfirmNavigateBack -> confirmNavigateBack()

            is AddAdminAction.DismissDialog -> {
                updateState { it.copy(dialogState = null) }
            }
        }
    }

    private fun handleBackNavigation() {
        if (hasUnsavedChanges()) {
            updateState {
                it.copy(
                    dialogState = DialogState(
                        dialogType = DialogType.CONFIRM_NORMAL_ACTION,
                        title = UiText.DynamicString("Unsaved Changes"),
                        message = UiText.DynamicString("You have unsaved changes. Are you sure you want to leave?")
                    )
                )
            }
        } else {
            sendEvent(AddAdminEvent.NavigateBack)
        }
    }

    private fun confirmNavigateBack() {
        updateState { it.copy(dialogState = null) }
        sendEvent(AddAdminEvent.NavigateBack)
    }

    private fun hasUnsavedChanges(): Boolean {
        return state.email.isNotBlank() ||
               state.username.isNotBlank() ||
               state.password.isNotBlank() ||
               state.confirmPassword.isNotBlank()
    }

    private fun validatePasswordMatch(password: String, confirmPassword: String) {
        if (confirmPassword.isNotEmpty() && password != confirmPassword) {
            updateState {
                it.copy(confirmPasswordError = "Password and confirm password are not the same")
            }
        } else {
            updateState {
                it.copy(confirmPasswordError = null)
            }
        }
    }

    private fun addAdmin() {
        val currentState = state
        val email = currentState.email.trim()
        val username = currentState.username.trim()
        val password = currentState.password
        val confirmPassword = currentState.confirmPassword

        val emailValidationResult = email.validateAsEmail()
        val usernameValidationResult = username.validateAsAdminUsername()
        val passwordValidationResult = password.validateAsPassword()

        updateState {
            it.copy(
                emailError = if (emailValidationResult.successful) null else emailValidationResult.errorMessage,
                usernameError = if (usernameValidationResult.successful) null else usernameValidationResult.errorMessage,
                passwordError = if (passwordValidationResult.successful) null else passwordValidationResult.errorMessage,
                confirmPasswordError = if (password == confirmPassword) null else "Password and confirm password are not the same"
            )
        }

        val hasError = !emailValidationResult.successful ||
                      !usernameValidationResult.successful ||
                      !passwordValidationResult.successful ||
                      password != confirmPassword

        if (hasError) {
            updateState { it.copy(isPasswordVisible = true) }
            return
        }

        updateState {
            it.copy(
                isAdding = true,
                emailError = null,
                usernameError = null,
                passwordError = null,
                confirmPasswordError = null
            )
        }

        viewModelScope.launch {
            adminRepository.addAdmin(email, username, password)
                .onSuccess { admin ->
                    updateState { it.copy(isAdding = false) }

                    SnackbarController.sendEvent(SnackbarEvent("Admin added successfully."))

                    sendEvent(AddAdminEvent.NavigateToAdminDetails)
                }
                .onError { error ->
                    updateState {
                        it.copy(
                            isAdding = false,
                            isPasswordVisible = true,
                            dialogState = DialogState(
                                dialogType = DialogType.ERROR,
                                title = UiText.DynamicString("Failed to Add Admin"),
                                message = error.toUiText()
                            )
                        )
                    }
                }
        }
    }
}
