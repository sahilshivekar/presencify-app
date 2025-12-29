package edu.watumull.presencify.core.domain.model.student

data class StudentFCMToken(
    val id: String,
    val studentId: String,
    val token: String,
    val deviceInfo: String?,
    val student: Student? = null
)
