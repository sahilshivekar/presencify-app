package edu.watumull.presencify.core.domain.model.student

data class Dropout(
    val id: String,
    val studentId: String,
    val academicStartYear: Int,
    val academicEndYear: Int,
    val student: Student? = null
)
