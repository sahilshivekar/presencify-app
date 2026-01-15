package edu.watumull.presencify.feature.admin.auth.verify_code

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import edu.watumull.presencify.core.design.systems.components.dialog.DialogType
import edu.watumull.presencify.core.domain.onError
import edu.watumull.presencify.core.domain.onSuccess
import edu.watumull.presencify.core.domain.repository.admin_auth.AdminAuthRepository
import edu.watumull.presencify.core.presentation.global_snackbar.SnackbarController
import edu.watumull.presencify.core.presentation.global_snackbar.SnackbarEvent
import edu.watumull.presencify.core.presentation.toUiText
import edu.watumull.presencify.core.presentation.utils.BaseViewModel
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
                            dialogState = AdminVerifyCodeState.DialogState(
                                isVisible = true,
                                title = "Verification Failed",
                                message = error.toUiText(),
                                dialogType = DialogType.ERROR,
                                dialogIntention = DialogIntention.VERIFY_CODE_ERROR
                            )
                        )
                    }
                }
        }
    }
}
