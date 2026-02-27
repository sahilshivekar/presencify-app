package edu.watumull.presencify.core.domain.model.attendance

data class AttendanceWithTotalCount(
    val attendances: List<Attendance>,
    val totalCount: Int
)
