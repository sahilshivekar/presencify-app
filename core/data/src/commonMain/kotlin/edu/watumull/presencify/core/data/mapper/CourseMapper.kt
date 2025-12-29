package edu.watumull.presencify.core.data.mapper

import edu.watumull.presencify.core.data.dto.CourseDto
import edu.watumull.presencify.core.domain.model.academics.Course

fun CourseDto.toDomain(): Course = Course(
    id = id,
    schemeId = schemeId,
    code = code,
    name = name,
    optionalSubject = optionalSubject,
    scheme = scheme?.toDomain(),
    branchCourseSemesters = branchCourseSemesters?.map { it.toDomain() },
    classes = classes?.map { it.toDomain() },
    teacherTeachesCourses = teacherTeachesCourses?.map { it.toDomain() }
)
