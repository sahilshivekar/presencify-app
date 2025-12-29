package edu.watumull.presencify.core.domain.model.shedule

data class RoomListWithTotalCount(
    val rooms: List<Room>,
    val totalCount: Int
)