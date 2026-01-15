package edu.watumull.presencify.feature.admin.mgt.admin_details

import edu.watumull.presencify.core.design.systems.components.dialog.DialogType
import edu.watumull.presencify.core.presentation.UiText

data class AdminDetailsState(
    val editableEmail: String = "",
    val editableUsername: String = "",
    val orgEmail: String = "",
    val orgUsername: String = "",
    val emailError: String? = null,
    val usernameError: String? = null,
    val isEditingDetails: Boolean = false,
    val isUpdatingDetails: Boolean = false,
    val isUsernameEmailEnabled: Boolean = false,
    val isVerified: Boolean? = null,
    val isSendingVerificationCode: Boolean = false,
    val isLoggingOut: Boolean = false,
    val isRemovingAccount: Boolean = false,
    val viewState: ViewState = ViewState.Loading,
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
    ERROR,
    REMOVE_ACCOUNT_CONFIRMATION
}
