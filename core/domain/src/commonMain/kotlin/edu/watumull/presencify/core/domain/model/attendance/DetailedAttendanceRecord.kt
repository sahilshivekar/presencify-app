package edu.watumull.presencify.core.domain.model.attendance

import kotlinx.datetime.LocalDate

data class DetailedAttendanceRecord(
    val attendanceId: String,
    val date: LocalDate,
    val attendanceStatus: Boolean
)
