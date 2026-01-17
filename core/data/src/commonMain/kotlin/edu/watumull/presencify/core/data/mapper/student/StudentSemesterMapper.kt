package edu.watumull.presencify.core.data.mapper.student

import edu.watumull.presencify.core.data.dto.student.StudentSemesterDto
import edu.watumull.presencify.core.data.mapper.academics.toDomain
import edu.watumull.presencify.core.domain.model.student.StudentSemester

fun StudentSemesterDto.toDomain(): StudentSemester =
    StudentSemester(
        id = id,
        studentId = studentId,
        semesterId = semesterId,
        student = student?.toDomain(),
        semester = semester?.toDomain()
    )
