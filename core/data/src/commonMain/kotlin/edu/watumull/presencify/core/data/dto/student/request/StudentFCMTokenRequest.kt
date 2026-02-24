package edu.watumull.presencify.core.data.dto.student.request

import kotlinx.serialization.Serializable

@Serializable
data class StudentFCMTokenRequest(
    val studentId: String,
    val fcmToken: String
)
