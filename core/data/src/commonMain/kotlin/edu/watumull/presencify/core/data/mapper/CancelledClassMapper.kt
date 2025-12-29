package edu.watumull.presencify.core.data.mapper

import edu.watumull.presencify.core.data.dto.CancelledClassDto
import edu.watumull.presencify.core.domain.model.shedule.CancelledClass

fun CancelledClassDto.toDomain(): CancelledClass = CancelledClass(
    id = id,
    classId = classId,
    cancelDate = cancelDate,
    reason = reason,
    klass = klass?.toDomain()
)
