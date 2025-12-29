package edu.watumull.presencify.core.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class AttendanceRecordDto(
    val attendanceDate: String,
    val totalStudents: Int,
    val presentStudents: Int,
    val attendanceId: Int
)
