package edu.watumull.presencify.core.data.mapper.teacher

import edu.watumull.presencify.core.data.dto.teacher.TeacherTeachesCourseDto
import edu.watumull.presencify.core.data.mapper.academics.toDomain
import edu.watumull.presencify.core.domain.model.teacher.TeacherTeachesCourse

fun TeacherTeachesCourseDto.toDomain(): TeacherTeachesCourse =
    TeacherTeachesCourse(
        id = id,
        teacherId = teacherId,
        courseId = courseId,
        teacher = teacher?.toDomain(),
        course = course?.toDomain()
    )
