package edu.watumull.presencify.feature.student.auth.login

import edu.watumull.presencify.core.designsystem.components.dialog.DialogType
import edu.watumull.presencify.core.presentation.UiText

data class StudentLoginState(
    val emailOrPRN: String = "",
    val emailOrPRNError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val viewState: ViewState = ViewState.Content,
    val dialogState: DialogState? = null
) {
    sealed interface ViewState {
        data object Loading : ViewState
        data object Content : ViewState
        data class Error(val message: UiText) : ViewState
    }

    data class DialogState(
        val isVisible: Boolean = true,
        val dialogType: DialogType = DialogType.INFO,
        val dialogIntention: DialogIntention = DialogIntention.GENERIC,
        val title: String = "",
        val message: UiText? = null,
    )
}

enum class DialogIntention {
    GENERIC,
    LOGIN_ERROR,
    FORGOT_PASSWORD
}
