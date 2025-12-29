package edu.watumull.presencify.core.data.mapper.academics

import edu.watumull.presencify.core.data.dto.academics.SemesterListWithTotalCountDto
import edu.watumull.presencify.core.domain.model.academics.SemesterListWithTotalCount

fun SemesterListWithTotalCountDto.toDomain(): SemesterListWithTotalCount = SemesterListWithTotalCount(
    semesters = semesters.map { it.toDomain() },
    totalCount = totalCount
)
