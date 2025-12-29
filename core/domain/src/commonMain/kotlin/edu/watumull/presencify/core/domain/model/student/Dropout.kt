package edu.watumull.presencify.core.domain.model.student

data class Dropout(
    val id: String,
    val studentId: String,
    val reason: String?,
    val dropoutDate: String,
    val student: Student? = null
)
