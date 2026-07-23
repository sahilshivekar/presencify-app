package edu.watumull.presencify.core.domain.model.academics

data class SemesterCourses(
    val compulsoryCourses: List<Course>,
    val optionalCourses: List<DivisionCourse>
)
