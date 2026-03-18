package edu.watumull.presencify.feature.attendance.aggregate_analytics

sealed interface AggregateAttendanceAnalyticsEvent {
    data object NavigateBack : AggregateAttendanceAnalyticsEvent
    data class NavigateToSearchAttendanceForCourse(val courseId: String) : AggregateAttendanceAnalyticsEvent
}
