package edu.watumull.presencify.core.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RoomDto(
    val id: String,
    val roomNumber: String,
    val name: String? = null,
    val type: String? = null,
    val sittingCapacity: Int,
    val createdAt: String,
    val updatedAt: String,
    @SerialName("Classes")
    val classes: List<ClassDto>? = null
)
