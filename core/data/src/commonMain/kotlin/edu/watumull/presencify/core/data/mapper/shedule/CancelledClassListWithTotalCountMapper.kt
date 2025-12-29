package edu.watumull.presencify.core.data.mapper.shedule

import edu.watumull.presencify.core.data.dto.shedule.CancelledClassListWithTotalCountDto
import edu.watumull.presencify.core.domain.model.shedule.CancelledClassListWithTotalCount

fun CancelledClassListWithTotalCountDto.toDomain(): CancelledClassListWithTotalCount = CancelledClassListWithTotalCount(
    cancelledClasses = cancelledClasses.map { it.toDomain() },
    totalCount = totalCount
)
