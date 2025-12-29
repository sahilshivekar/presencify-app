package edu.watumull.presencify.core.data.mapper

import edu.watumull.presencify.core.data.dto.StudentDivisionDto
import edu.watumull.presencify.core.domain.model.student.StudentDivision

fun StudentDivisionDto.toDomain(): StudentDivision =
    StudentDivision(
        id = id,
        studentId = studentId,
        divisionId = divisionId,
        fromDate = fromDate,
        toDate = toDate,
        student = student?.toDomain(),
        division = division?.toDomain()
    )
