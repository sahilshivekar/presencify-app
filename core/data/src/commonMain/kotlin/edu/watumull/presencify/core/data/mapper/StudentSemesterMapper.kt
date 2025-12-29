package edu.watumull.presencify.core.data.mapper

import edu.watumull.presencify.core.data.dto.StudentSemesterDto
import edu.watumull.presencify.core.domain.model.student.StudentSemester

fun StudentSemesterDto.toDomain(): StudentSemester =
    StudentSemester(
        id = id,
        studentId = studentId,
        semesterId = semesterId,
        fromDate = fromDate,
        toDate = toDate,
        student = student?.toDomain(),
        semester = semester?.toDomain()
    )
