package edu.watumull.presencify.core.data.dto.shedule

import kotlinx.serialization.Serializable

@Serializable
data class RoomListWithTotalCountDto(
    val rooms: List<RoomDto>,
    val totalCount: Int
)
