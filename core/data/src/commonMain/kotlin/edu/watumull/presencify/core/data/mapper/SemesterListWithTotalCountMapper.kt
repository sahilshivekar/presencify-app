package edu.watumull.presencify.core.data.mapper

import edu.watumull.presencify.core.data.dto.SemesterListWithTotalCountDto
import edu.watumull.presencify.core.domain.model.academics.SemesterListWithTotalCount

fun SemesterListWithTotalCountDto.toDomain(): SemesterListWithTotalCount = SemesterListWithTotalCount(
    semesters = semesters.map { it.toDomain() },
    totalCount = totalCount
)
