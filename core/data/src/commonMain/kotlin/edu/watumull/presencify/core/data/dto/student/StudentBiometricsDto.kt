package edu.watumull.presencify.core.data.dto.student

import edu.watumull.presencify.core.domain.enums.BiometricVerificationStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StudentBiometricsDto(
    @SerialName("biometricVerificationStatus")
    val biometricVerificationStatus: BiometricVerificationStatus,
    @SerialName("presignedUrls")
    val presignedUrls: List<String>
)
