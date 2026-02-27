package edu.watumull.presencify.core.data.dto.attendance

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class CreateAttendanceRequestDto(
    val classId: String,
    val date: LocalDate
)
