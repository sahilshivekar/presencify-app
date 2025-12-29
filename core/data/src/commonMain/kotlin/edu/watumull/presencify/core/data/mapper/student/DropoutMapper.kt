package edu.watumull.presencify.core.data.mapper.student

import edu.watumull.presencify.core.data.dto.student.DropoutDto
import edu.watumull.presencify.core.domain.model.student.Dropout

fun DropoutDto.toDomain(): Dropout = Dropout(
    id = id,
    studentId = studentId,
    reason = reason,
    dropoutDate = dropoutDate,
    student = student?.toDomain()
)
