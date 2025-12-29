package edu.watumull.presencify.core.data.mapper.academics

import edu.watumull.presencify.core.data.dto.academics.SchemeDto
import edu.watumull.presencify.core.data.dto.academics.UniversityDto
import edu.watumull.presencify.core.data.mapper.student.toDomain
import edu.watumull.presencify.core.domain.model.academics.Scheme
import edu.watumull.presencify.core.domain.model.academics.University

fun UniversityDto.toDomain(): University = University(
    id = id,
    name = name,
    abbreviation = abbreviation,
    schemes = schemes?.map { it.toDomain() }
)

fun SchemeDto.toDomain(): Scheme =
    Scheme(
        id = id,
        name = name,
        universityId = universityId,
        university = university?.toDomain(),
        semesters = semesters?.map { it.toDomain() },
        students = students?.map { it.toDomain() },
        courses = courses?.map { it.toDomain() }
    )
