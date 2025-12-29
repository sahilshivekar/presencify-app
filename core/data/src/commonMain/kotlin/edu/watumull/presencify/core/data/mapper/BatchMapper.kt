package edu.watumull.presencify.core.data.mapper

import edu.watumull.presencify.core.data.dto.BatchDto
import edu.watumull.presencify.core.domain.model.academics.Batch

fun BatchDto.toDomain(): Batch = Batch(
    id = id,
    batchCode = batchCode,
    divisionId = divisionId,
    division = division?.toDomain(),
    studentBatches = studentBatches?.map { it.toDomain() },
    classes = classes?.map { it.toDomain() }
)
