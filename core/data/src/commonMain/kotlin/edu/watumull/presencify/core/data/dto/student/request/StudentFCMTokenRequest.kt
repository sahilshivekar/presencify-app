package edu.watumull.presencify.core.data.dto.student.request

import kotlinx.serialization.Serializable

@Serializable
data class StudentFCMTokenRequest(
    val studentId: String,
    val fcmToken: String,
    val deviceId: String,
    val deviceModel: String? = null,
    val osVersion: String? = null,
    val appVersion: String? = null,
    val deviceType: String
)
