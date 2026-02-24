package edu.watumull.presencify.core.data.dto.schedule.request

import kotlinx.serialization.Serializable

@Serializable
data class AddRoomRequest(
    val roomNumber: String,
    val sittingCapacity: Int,
    val name: String? = null,
    val type: String? = null // RoomType as String
)

@Serializable
data class UpdateRoomRequest(
    val roomNumber: String? = null,
    val sittingCapacity: Int? = null,
    val name: String? = null,
    val type: String? = null // RoomType as String
)
