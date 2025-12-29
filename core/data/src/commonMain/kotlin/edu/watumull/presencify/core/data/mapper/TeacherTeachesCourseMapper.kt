package edu.watumull.presencify.core.data.mapper

import edu.watumull.presencify.core.data.dto.TeacherTeachesCourseDto
import edu.watumull.presencify.core.domain.model.teacher.TeacherTeachesCourse

fun TeacherTeachesCourseDto.toDomain(): TeacherTeachesCourse =
    TeacherTeachesCourse(
        id = id,
        teacherId = teacherId,
        courseId = courseId,
        teacher = teacher?.toDomain(),
        course = course?.toDomain()
    )
