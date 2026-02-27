package edu.watumull.presencify.core.data.dto.attendance

import kotlinx.serialization.Serializable

@Serializable
data class UpdateStudentAttendanceRequestDto(
    val attendanceId: String,
    val studentId: String,
    val newAttendanceStatus: Boolean
)
