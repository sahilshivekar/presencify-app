package edu.watumull.presencify.core.data.mapper

import edu.watumull.presencify.core.data.dto.SemesterCourseDto
import edu.watumull.presencify.core.domain.model.academics.SemesterCourse

fun SemesterCourseDto.toDomain(): SemesterCourse = SemesterCourse(
    id = id,
    semesterId = semesterId,
    courseId = courseId,
    semester = semester?.toDomain(),
    course = course?.toDomain()
)
