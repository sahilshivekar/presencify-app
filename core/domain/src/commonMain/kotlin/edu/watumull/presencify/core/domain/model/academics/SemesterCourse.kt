package edu.watumull.presencify.core.domain.model.academics

data class SemesterCourse(
    val id: String,
    val semesterId: String,
    val courseId: String,
    val semester: Semester? = null,
    val course: Course? = null
)
