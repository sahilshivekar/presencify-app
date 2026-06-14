package edu.watumull.presencify.core.domain.model.student

data class StudentFCMToken(
    val id: String,
    val studentId: String,
    val fcmToken: String,
    val deviceId: String,
    val deviceModel: String? = null,
    val osVersion: String? = null,
    val appVersion: String? = null,
    val deviceType: String,
    val student: Student? = null
)
