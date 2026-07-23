package edu.watumull.presencify.core.domain.model.academics

data class DivisionCourse(
    val id: String,
    val divisionId: String,
    val courseId: String,
    val course: Course? = null,
    val division: Division? = null
)
