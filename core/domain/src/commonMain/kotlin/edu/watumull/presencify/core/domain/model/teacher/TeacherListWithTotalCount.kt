package edu.watumull.presencify.core.domain.model.teacher

data class TeacherListWithTotalCount(
    val teacher: List<Teacher>,
    val totalTeacher: Int
)