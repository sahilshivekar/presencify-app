package edu.watumull.presencify.core.data.mapper

import edu.watumull.presencify.core.data.dto.TeacherListWithTotalCountDto
import edu.watumull.presencify.core.domain.model.teacher.TeacherListWithTotalCount

fun TeacherListWithTotalCountDto.toDomain(): TeacherListWithTotalCount = TeacherListWithTotalCount(
    teacher = teacher.map { it.toDomain() },
    totalTeacher = totalTeacher
)
