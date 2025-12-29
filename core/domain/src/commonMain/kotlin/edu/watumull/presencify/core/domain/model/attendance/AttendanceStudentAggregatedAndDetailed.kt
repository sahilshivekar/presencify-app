package edu.watumull.presencify.core.domain.model.attendance

data class AttendanceStudentAggregatedAndDetailed(
    val aggregatedAttendance: AggregatedAttendance,
    val detailedAttendanceRecord: List<DetailedAttendanceRecord>
)
