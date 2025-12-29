package edu.watumull.presencify.core.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class RoomListWithTotalCountDto(
    val rooms: List<RoomDto>,
    val totalCount: Int
)
