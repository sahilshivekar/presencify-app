package edu.watumull.presencify.core.data.dto.student

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DropoutDto(
    val id: String,
    val studentId: String,
    val academicStartYear: Int,
    val academicEndYear: Int,
    val createdAt: String,
    val updatedAt: String,
    @SerialName("Student")
    val student: StudentDto? = null
)
