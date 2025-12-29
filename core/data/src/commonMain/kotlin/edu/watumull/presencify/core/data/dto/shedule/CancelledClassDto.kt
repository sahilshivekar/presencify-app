package edu.watumull.presencify.core.data.dto.shedule

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CancelledClassDto(
    val id: String,
    val classId: String,
    val cancelDate: String,
    val reason: String? = null,
    val createdAt: String,
    val updatedAt: String,
    @SerialName("Class")
    val klass: ClassDto? = null
)
