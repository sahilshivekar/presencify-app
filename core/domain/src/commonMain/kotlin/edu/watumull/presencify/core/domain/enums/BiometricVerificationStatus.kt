package edu.watumull.presencify.core.domain.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class BiometricVerificationStatus(val value: String) {
    @SerialName("not_submitted")
    NOT_SUBMITTED("Not submitted"),
    @SerialName("pending_review")
    PENDING_REVIEW("Pending review"),
    @SerialName("approved")
    APPROVED("approved");

    companion object {
        fun fromValue(value: String): BiometricVerificationStatus? = entries.find { it.value == value }
    }
}
