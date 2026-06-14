package edu.watumull.presencify.core.data.mapper.student

import edu.watumull.presencify.core.data.dto.student.StudentFCMTokenDto
import edu.watumull.presencify.core.domain.model.student.StudentFCMToken

fun StudentFCMTokenDto.toDomain(): StudentFCMToken =
    StudentFCMToken(
        id = id,
        studentId = studentId,
        fcmToken = fcmToken,
        deviceId = deviceId,
        deviceModel = deviceModel,
        osVersion = osVersion,
        appVersion = appVersion,
        deviceType = deviceType,
        student = student?.toDomain()
    )
