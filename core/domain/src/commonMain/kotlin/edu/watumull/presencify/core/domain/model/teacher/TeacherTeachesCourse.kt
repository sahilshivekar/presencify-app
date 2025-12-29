package edu.watumull.presencify.core.domain.model.teacher

import edu.watumull.presencify.core.domain.model.academics.Course

data class TeacherTeachesCourse(
    val id: String,
    val teacherId: String,
    val courseId: String,
    val teacher: Teacher? = null,
    val course: Course? = null
)
