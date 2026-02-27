package edu.watumull.presencify.feature.attendance.attendance_dashboard

data class AttendanceDashboardState(
    val viewState: ViewState = ViewState.Content
) {
    sealed interface ViewState {
        data object Content : ViewState
    }
}
