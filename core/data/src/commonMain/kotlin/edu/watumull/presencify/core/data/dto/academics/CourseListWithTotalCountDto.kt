package edu.watumull.presencify.core.data.dto.academics

import kotlinx.serialization.Serializable

@Serializable
data class CourseListWithTotalCountDto(
    val courses: List<CourseDto>,
    val totalCount: Int
)
