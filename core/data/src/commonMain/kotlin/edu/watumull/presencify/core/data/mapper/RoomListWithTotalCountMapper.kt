package edu.watumull.presencify.core.data.mapper

import edu.watumull.presencify.core.data.dto.RoomListWithTotalCountDto
import edu.watumull.presencify.core.domain.model.shedule.RoomListWithTotalCount

fun RoomListWithTotalCountDto.toDomain(): RoomListWithTotalCount = RoomListWithTotalCount(
    rooms = rooms.map { it.toDomain() },
    totalCount = totalCount
)
