package edu.watumull.presencify.core.data.mapper

import edu.watumull.presencify.core.data.dto.DivisionDto
import edu.watumull.presencify.core.domain.model.academics.Division

fun DivisionDto.toDomain(): Division = Division(
    id = id,
    divisionCode = divisionCode,
    semesterId = semesterId,
    semester = semester?.toDomain(),
    batches = batches?.map { it.toDomain() },
    studentDivisions = studentDivisions?.map { it.toDomain() },
    timetable = timetable?.toDomain()
)
