package edu.watumull.presencify.core.domain.model.student

data class StudentListWithTotalCount(
    val students: List<Student>,
    val totalStudents: Int
)