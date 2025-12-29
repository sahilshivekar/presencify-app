package edu.watumull.presencify.core.data.mapper

import edu.watumull.presencify.core.data.dto.ClassListWithTotalCountDto
import edu.watumull.presencify.core.domain.model.shedule.ClassListWithTotalCount

fun ClassListWithTotalCountDto.toDomain(): ClassListWithTotalCount = ClassListWithTotalCount(
    classes = classes.map { it.toDomain() },
    totalCount = totalCount
)
