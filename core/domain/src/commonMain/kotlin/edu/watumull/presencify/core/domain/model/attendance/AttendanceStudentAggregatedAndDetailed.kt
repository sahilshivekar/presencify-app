package edu.watumull.presencify.domain

data class AttendanceStudentAggregatedAndDetailed(
    val aggregatedAttendance: AggregatedAttendance,
    val detailedAttendanceRecord: List<DetailedAttendanceRecord>
)
