package edu.watumull.presencify.core.data.dto.academics

import kotlinx.serialization.Serializable

@Serializable
data class SemesterCoursesDto(
    val compulsoryCourses: List<CourseDto>,
    val optionalCourses: List<DivisionCourseDto>
)
