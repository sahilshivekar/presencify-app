package edu.watumull.presencify.feature.schedule.dashboard

import edu.watumull.presencify.core.domain.model.auth.UserRole

sealed interface ScheduleDashboardAction {
    data object ClickRoom : ScheduleDashboardAction
    data object ClickClasses : ScheduleDashboardAction
    data object ClickTimetable : ScheduleDashboardAction
    data class LoadUpcomingClasses(val userRole: UserRole) : ScheduleDashboardAction
    data object ClearUpcomingClasses : ScheduleDashboardAction
}

