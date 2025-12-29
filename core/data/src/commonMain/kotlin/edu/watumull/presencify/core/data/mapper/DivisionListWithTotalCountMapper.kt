package edu.watumull.presencify.core.data.mapper

import edu.watumull.presencify.core.data.dto.DivisionListWithTotalCountDto
import edu.watumull.presencify.core.domain.model.academics.DivisionListWithTotalCount

fun DivisionListWithTotalCountDto.toDomain(): DivisionListWithTotalCount = DivisionListWithTotalCount(
    divisions = divisions.map { it.toDomain() },
    totalCount = totalCount
)
