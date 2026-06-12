package edu.watumull.presencify.core.domain.model.student

import edu.watumull.presencify.core.domain.enums.BiometricVerificationStatus

data class StudentBiometrics(
    val biometricVerificationStatus: BiometricVerificationStatus,
    val presignedUrls: List<String>
)
