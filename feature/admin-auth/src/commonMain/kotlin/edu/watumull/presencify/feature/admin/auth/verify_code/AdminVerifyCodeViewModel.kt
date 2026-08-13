package edu.watumull.presencify.feature.admin.auth.verify_code

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import edu.watumull.presencify.core.designsystem.components.dialog.DialogType
import edu.watumull.presencify.core.domain.onError
import edu.watumull.presencify.core.domain.onSuccess
import edu.watumull.presencify.core.domain.repository.admin_auth.AdminAuthRepository
import edu.watumull.presencify.core.presentation.UiText
import edu.watumull.presencify.core.presentation.components.dialog.DialogState
import edu.watumull.presencify.core.presentation.global_snackbar.SnackbarController
import edu.watumull.presencify.core.presentation.global_snackbar.SnackbarEvent
import edu.watumull.presencify.core.presentation.toUiText
import edu.watumull.presencify.core.presentation.utils.BaseViewModel
import edu.watumull.presencify.core.presentation.validation.validateAsEmail
import edu.watumull.presencify.core.presentation.validation.validateAsVerificationCode
import edu.watumull.presencify.feature.admin.auth.navigation.AdminAuthRoutes
import kotlinx.coroutines.launch

class AdminVerifyCodeViewModel(
    private val adminAuthRepository: AdminAuthRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<AdminVerifyCodeState, AdminVerifyCodeEvent, AdminVerifyCodeAction>(
    initialState = AdminVerifyCodeState(
        email = savedStateHandle.toRoute<AdminAuthRoutes.AdminVerifyCode>().email
    )
) {

    override fun handleAction(action: AdminVerifyCodeAction) {
        when (action) {
            is AdminVerifyCodeAction.ChangeCode -> {
                updateState {
                    it.copy(
                        code = action.code,
                        codeError = null
                    )
                }
            }

            is AdminVerifyCodeAction.ClickVerifyCode -> {
                verifyCode()
            }

            is AdminVerifyCodeAction.ClickBackButton -> {
                sendEvent(AdminVerifyCodeEvent.NavigateBack)
            }

            is AdminVerifyCodeAction.DismissDialog -> {
                updateState { it.copy(dialogState = null) }
            }
        }
    }

    private fun verifyCode() {
        val currentState = state
        val email = currentState.email.trim()
        val code = currentState.code.trim()

        val codeValidation = code.validateAsVerificationCode()
        val emailValidation = email.validateAsEmail()

        updateState {
            it.copy(
                codeError = codeValidation.errorMessage
            )
        }

        if (!codeValidation.successful) return

        if (!emailValidation.successful) {
            viewModelScope.launch {
                SnackbarController.sendEvent(SnackbarEvent("Technical error occurred, please restart the app"))
            }
            return
        }

        updateState {
            it.copy(isLoading = true)
        }

        viewModelScope.launch {
            adminAuthRepository.verifyCode(email, code)
                .onSuccess {
                    updateState { it.copy(isLoading = false) }

                    SnackbarController.sendEvent(SnackbarEvent("Verification successful"))

                    sendEvent(AdminVerifyCodeEvent.NavigateToHome)
                }
                .onError { error ->
                    updateState {
                        it.copy(
                            isLoading = false,
                            dialogState = DialogState(
                                title = UiText.DynamicString("Verification Failed"),
                                message = error.toUiText(),
                                dialogType = DialogType.ERROR,
                            )
                        )
                    }
                }
        }
    }
}
