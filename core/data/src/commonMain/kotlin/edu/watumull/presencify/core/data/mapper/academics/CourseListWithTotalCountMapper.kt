package edu.watumull.presencify.core.data.mapper.academics

import edu.watumull.presencify.core.data.dto.academics.CourseListWithTotalCountDto
import edu.watumull.presencify.core.domain.model.academics.CourseListWithTotalCount

fun CourseListWithTotalCountDto.toDomain(): CourseListWithTotalCount = CourseListWithTotalCount(
    courses = courses.map { it.toDomain() },
    totalCount = totalCount
)
