package edu.watumull.presencify.core.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class TeacherListWithTotalCountDto(
    val teacher: List<TeacherDto>,
    val totalTeacher: Int
)
