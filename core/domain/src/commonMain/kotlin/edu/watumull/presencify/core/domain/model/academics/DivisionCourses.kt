package edu.watumull.presencify.core.domain.model.academics

data class DivisionCourses(
    val compulsoryCourses: List<Course>,
    val optionalCourses: List<DivisionCourse>
)
