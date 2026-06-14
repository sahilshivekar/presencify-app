package edu.watumull.presencify.core.data.dto.student

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StudentFCMTokenDto(
    val id: String,
    val studentId: String,
    val fcmToken: String,
    val deviceId: String,
    val deviceModel: String? = null,
    val osVersion: String? = null,
    val appVersion: String? = null,
    val deviceType: String,
    val createdAt: String,
    val updatedAt: String,
    @SerialName("Student")
    val student: StudentDto? = null
)
