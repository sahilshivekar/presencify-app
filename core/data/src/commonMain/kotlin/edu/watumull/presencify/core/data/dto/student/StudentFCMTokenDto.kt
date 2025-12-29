package edu.watumull.presencify.core.data.dto.student

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StudentFCMTokenDto(
    val id: String,
    val studentId: String,
    val token: String,
    val deviceInfo: String? = null,
    val createdAt: String,
    val updatedAt: String,
    @SerialName("Student")
    val student: StudentDto? = null
)
