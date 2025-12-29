package edu.watumull.presencify.core.data.mapper.academics

import edu.watumull.presencify.core.data.dto.academics.SemesterDto
import edu.watumull.presencify.core.data.mapper.student.toDomain
import edu.watumull.presencify.core.domain.model.academics.Semester

fun SemesterDto.toDomain(): Semester = Semester(
    id = id,
    branchId = branchId,
    semesterNumber = semesterNumber,
    academicStartYear = academicStartYear,
    academicEndYear = academicEndYear,
    startDate = startDate,
    endDate = endDate,
    schemeId = schemeId,
    branch = branch?.toDomain(),
    scheme = scheme?.toDomain(),
    divisions = divisions?.map { it.toDomain() },
    studentSemesters = studentSemesters?.map { it.toDomain() },
    courses = courses?.map { it.toDomain() }
)
