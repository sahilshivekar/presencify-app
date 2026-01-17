package edu.watumull.presencify.feature.academics.dashboard

data class AcademicsDashboardState(
    val viewState: ViewState = ViewState.Content
) {
    sealed interface ViewState {
        data object Content : ViewState
        data object Loading : ViewState
    }
}