package edu.watumull.presencify.core.data.dto.academics

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UniversityDto(
    val id: String,
    val name: String,
    val abbreviation: String,
    val createdAt: String,
    val updatedAt: String,
    @SerialName("Schemes")
    val schemes: List<SchemeDto>? = null
)
