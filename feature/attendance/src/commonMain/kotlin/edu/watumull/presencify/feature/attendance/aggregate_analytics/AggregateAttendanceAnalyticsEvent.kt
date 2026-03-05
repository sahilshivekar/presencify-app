package edu.watumull.presencify.feature.attendance.aggregate_analytics

sealed interface AggregateAttendanceAnalyticsEvent {
    data object NavigateBack : AggregateAttendanceAnalyticsEvent
}
