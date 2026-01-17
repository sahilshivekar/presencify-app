package edu.watumull.presencify.feature.users.dashboard


data class UsersDashboardState(
    val viewState: ViewState = ViewState.Content
) {
    sealed interface ViewState {
        data object Content : ViewState
        data object Loading : ViewState
    }
}