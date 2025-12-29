package edu.watumull.presencify.core.data.mapper.shedule

import edu.watumull.presencify.core.data.dto.shedule.ClassListWithTotalCountDto
import edu.watumull.presencify.core.domain.model.shedule.ClassListWithTotalCount

fun ClassListWithTotalCountDto.toDomain(): ClassListWithTotalCount = ClassListWithTotalCount(
    classes = classes.map { it.toDomain() },
    totalCount = totalCount
)
