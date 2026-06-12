package edu.watumull.presencify.core.data.mapper.student

import edu.watumull.presencify.core.data.dto.student.StudentBiometricsDto
import edu.watumull.presencify.core.domain.model.student.StudentBiometrics

fun StudentBiometricsDto.toDomain() = StudentBiometrics(
    biometricVerificationStatus = biometricVerificationStatus,
    presignedUrls = presignedUrls
)
