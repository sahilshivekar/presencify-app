package edu.watumull.presencify.core.data.mapper

import edu.watumull.presencify.core.data.dto.StudentFCMTokenDto
import edu.watumull.presencify.core.domain.model.student.StudentFCMToken

fun StudentFCMTokenDto.toDomain(): StudentFCMToken =
    StudentFCMToken(
        id = id,
        studentId = studentId,
        token = token,
        deviceInfo = deviceInfo,
        student = student?.toDomain()
    )
