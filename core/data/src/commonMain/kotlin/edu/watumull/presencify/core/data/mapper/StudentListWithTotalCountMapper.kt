package edu.watumull.presencify.core.data.mapper

import edu.watumull.presencify.core.data.dto.StudentListWithTotalCountDto
import edu.watumull.presencify.core.domain.model.student.StudentListWithTotalCount

fun StudentListWithTotalCountDto.toDomain(): StudentListWithTotalCount = StudentListWithTotalCount(
    students = students.map { it.toDomain() },
    totalStudents = totalStudents
)
