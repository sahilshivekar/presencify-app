package edu.watumull.presencify.core.data.mapper.student

import edu.watumull.presencify.core.data.dto.student.StudentDivisionDto
import edu.watumull.presencify.core.data.mapper.academics.toDomain
import edu.watumull.presencify.core.domain.model.student.StudentDivision

fun StudentDivisionDto.toDomain(): StudentDivision =
    StudentDivision(
        id = id,
        studentId = studentId,
        divisionId = divisionId,
        startDate = startDate,
        endDate = endDate,
        student = student?.toDomain(),
        division = division?.toDomain()
    )
