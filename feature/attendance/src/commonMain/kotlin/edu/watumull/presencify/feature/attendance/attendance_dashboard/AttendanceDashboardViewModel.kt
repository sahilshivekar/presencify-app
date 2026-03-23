package edu.watumull.presencify.feature.attendance.attendance_dashboard

import edu.watumull.presencify.core.presentation.utils.BaseViewModel

class AttendanceDashboardViewModel : BaseViewModel<AttendanceDashboardState, AttendanceDashboardEvent, AttendanceDashboardAction>(
    initialState = AttendanceDashboardState()
) {
    override fun handleAction(action: AttendanceDashboardAction) {
        when (action) {
            AttendanceDashboardAction.BackButtonClick -> {
                sendEvent(AttendanceDashboardEvent.NavigateBack)
            }
            AttendanceDashboardAction.NavigateToStudentAttendanceAnalytics -> {
                sendEvent(AttendanceDashboardEvent.NavigateToStudentAttendanceAnalytics)
            }
            AttendanceDashboardAction.NavigateToAggregateAttendanceAnalytics -> {
                sendEvent(AttendanceDashboardEvent.NavigateToAggregateAttendanceAnalytics)
            }
            AttendanceDashboardAction.NavigateToSearchAttendance -> {
                sendEvent(AttendanceDashboardEvent.NavigateToSearchAttendance)
            }
            AttendanceDashboardAction.NavigateToCreateAttendance -> {
                sendEvent(AttendanceDashboardEvent.NavigateToCreateAttendance)
            }
            AttendanceDashboardAction.NavigateToSearchStudentForBiometrics -> {
                sendEvent(AttendanceDashboardEvent.NavigateToSearchStudentForBiometrics)
            }
        }
    }
}
