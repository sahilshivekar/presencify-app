package edu.watumull.presencify.core.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class StudentListWithTotalCountDto(
    val students: List<StudentDto>,
    val totalStudents: Int
)
