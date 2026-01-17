package edu.watumull.presencify.core.data.mapper.student

import edu.watumull.presencify.core.data.dto.student.StudentBatchDto
import edu.watumull.presencify.core.data.mapper.academics.toDomain
import edu.watumull.presencify.core.domain.model.student.StudentBatch

fun StudentBatchDto.toDomain(): StudentBatch = StudentBatch(
    id = id,
    studentId = studentId,
    batchId = batchId,
    startDate = startDate,
    endDate = endDate,
    student = student?.toDomain(),
    batch = batch?.toDomain()
)
