package edu.watumull.presencify.feature.teacher.auth.verify_code

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
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
import edu.watumull.presencify.feature.teacher.auth.navigation.TeacherAuthRoutes
import kotlinx.coroutines.launch

class TeacherVerifyCodeViewModel(
    private val teacherAuthRepository: TeacherAuthRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<TeacherVerifyCodeState, TeacherVerifyCodeEvent, TeacherVerifyCodeAction>(
    initialState = TeacherVerifyCodeState(
        email = savedStateHandle.toRoute<TeacherAuthRoutes.TeacherVerifyCode>().email
    )
) {

    override fun handleAction(action: TeacherVerifyCodeAction) {
        when (action) {
            is TeacherVerifyCodeAction.ChangeCode -> {
                updateState {
                    it.copy(
                        code = action.code,
                        codeError = null
                    )
                }
            }

            is TeacherVerifyCodeAction.ClickVerifyCode -> {
                verifyCode()
            }

            is TeacherVerifyCodeAction.ClickBackButton -> {
                sendEvent(TeacherVerifyCodeEvent.NavigateBack)
            }

            is TeacherVerifyCodeAction.DismissDialog -> {
                updateState { it.copy(dialogState = null) }
            }
        }
    }

    private fun verifyCode() {
        val currentState = state
        val email = currentState.email.trim()
        val code = currentState.code.trim()

        updateState {
            it.copy(codeError = null)
        }

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

        updateState {
            it.copy(isLoading = true)
        }

        viewModelScope.launch {
            teacherAuthRepository.verifyCode(email, code)
                .onSuccess {
                    updateState { it.copy(isLoading = false) }

                    SnackbarController.sendEvent(SnackbarEvent("Verification successful"))

                    sendEvent(TeacherVerifyCodeEvent.NavigateToHome)
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
