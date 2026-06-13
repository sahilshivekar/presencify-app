package edu.watumull.presencify.core.domain.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class BiometricVerificationStatus(val value: String) {
    @SerialName("Not Submitted")
    NOT_SUBMITTED("Not Submitted"),
    @SerialName("Pending Review")
    PENDING_REVIEW("Pending Review"),
    @SerialName("Approved")
    APPROVED("Approved"),
    @SerialName("Rejected")
    REJECTED("Rejected");

    companion object {
        fun fromValue(value: String): BiometricVerificationStatus? = entries.find { it.value == value }
    }
}
