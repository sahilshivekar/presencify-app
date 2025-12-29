package edu.watumull.presencify.core.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SemesterCourseDto(
    val id: String,
    val semesterId: String,
    val courseId: String,
    val createdAt: String,
    val updatedAt: String,
    @SerialName("Semester")
    val semester: SemesterDto? = null,
    @SerialName("Course")
    val course: CourseDto? = null
)
