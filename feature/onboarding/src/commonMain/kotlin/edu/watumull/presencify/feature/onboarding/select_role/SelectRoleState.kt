package edu.watumull.presencify.feature.onboarding.select_role

import edu.watumull.presencify.core.designsystem.components.dialog.DialogType

data class SelectRoleState(
    val viewState: ViewState = ViewState.Loading,
) {
    sealed interface ViewState {
        data object Loading : ViewState
        data class Error(val message: String) : ViewState
        data object Content : ViewState
    }
}