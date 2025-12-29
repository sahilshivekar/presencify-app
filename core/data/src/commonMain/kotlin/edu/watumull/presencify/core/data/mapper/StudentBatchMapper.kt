package edu.watumull.presencify.core.data.mapper

import edu.watumull.presencify.core.data.dto.StudentBatchDto
import edu.watumull.presencify.core.domain.model.student.StudentBatch

fun StudentBatchDto.toDomain(): StudentBatch = StudentBatch(
    id = id,
    studentId = studentId,
    batchId = batchId,
    fromDate = fromDate,
    toDate = toDate,
    student = student?.toDomain(),
    batch = batch?.toDomain()
)
