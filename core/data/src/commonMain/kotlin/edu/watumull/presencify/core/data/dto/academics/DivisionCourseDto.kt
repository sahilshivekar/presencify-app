package edu.watumull.presencify.core.data.dto.academics

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DivisionCourseDto(
    val id: String,
    val divisionId: String,
    val courseId: String,
    @SerialName("Course")
    val course: CourseDto? = null,
    @SerialName("Division")
    val division: DivisionDto? = null
)
