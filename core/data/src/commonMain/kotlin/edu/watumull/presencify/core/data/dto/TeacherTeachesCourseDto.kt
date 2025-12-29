package edu.watumull.presencify.core.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TeacherTeachesCourseDto(
    val id: String,
    val teacherId: String,
    val courseId: String,
    val createdAt: String,
    val updatedAt: String,
    @SerialName("Teacher")
    val teacher: TeacherDto? = null,
    @SerialName("Course")
    val course: CourseDto? = null
)
