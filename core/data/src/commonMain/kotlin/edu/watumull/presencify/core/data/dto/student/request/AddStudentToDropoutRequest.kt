package edu.watumull.presencify.core.data.dto.student.request

import kotlinx.serialization.Serializable

@Serializable
data class AddStudentToDropoutRequest(
    val studentId: String,
    val academicStartYear: Int,
    val academicEndYear: Int
)
