package edu.watumull.presencify.domain

data class DetailedAttendanceRecord(
    val attendanceId: Int,
    val date: String,
    val attendanceStatus: Boolean
)
