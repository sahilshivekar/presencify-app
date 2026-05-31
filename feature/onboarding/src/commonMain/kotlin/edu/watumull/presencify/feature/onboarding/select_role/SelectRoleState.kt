package edu.watumull.presencify.feature.onboarding.select_role

data class SelectRoleState(
    val viewState: ViewState = ViewState.Loading,
) {
    sealed interface ViewState {
        data object Loading : ViewState
        data class Error(val message: String) : ViewState
        data object Content : ViewState
    }
}