package edu.watumull.presencify.core.data.mapper

import edu.watumull.presencify.core.data.dto.BatchListWithTotalCountDto
import edu.watumull.presencify.core.domain.model.academics.BatchListWithTotalCount

fun BatchListWithTotalCountDto.toDomain(): BatchListWithTotalCount = BatchListWithTotalCount(
    batches = batches.map { it.toDomain() },
    totalCount = totalCount
)
