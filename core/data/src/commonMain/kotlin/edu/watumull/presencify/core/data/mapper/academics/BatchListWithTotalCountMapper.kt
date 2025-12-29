package edu.watumull.presencify.core.data.mapper.academics

import edu.watumull.presencify.core.data.dto.academics.BatchListWithTotalCountDto
import edu.watumull.presencify.core.domain.model.academics.BatchListWithTotalCount

fun BatchListWithTotalCountDto.toDomain(): BatchListWithTotalCount = BatchListWithTotalCount(
    batches = batches.map { it.toDomain() },
    totalCount = totalCount
)
