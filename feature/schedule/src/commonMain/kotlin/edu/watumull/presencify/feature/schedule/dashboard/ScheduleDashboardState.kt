package edu.watumull.presencify.feature.schedule.dashboard

import edu.watumull.presencify.core.domain.model.schedule.ClassSession
import edu.watumull.presencify.core.presentation.UiText

data class ScheduleDashboardState(
    val viewState: ViewState = ViewState.Content,
    val upcomingClasses: List<ClassSession> = emptyList(),
    val isLoadingUpcomingClasses: Boolean = false,
    val upcomingClassesError: UiText? = null
) {
    sealed interface ViewState {
        data object Loading : ViewState
        data object Content : ViewState
    }
}