package edu.watumull.presencify.core.domain.model.attendance

data class DetailedAttendanceRecord(
    val attendanceId: Int,
    val date: String,
    val attendanceStatus: Boolean
)
