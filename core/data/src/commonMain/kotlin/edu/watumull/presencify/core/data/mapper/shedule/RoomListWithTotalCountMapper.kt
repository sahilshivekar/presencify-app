package edu.watumull.presencify.core.data.mapper.shedule

import edu.watumull.presencify.core.data.dto.shedule.RoomListWithTotalCountDto
import edu.watumull.presencify.core.domain.model.shedule.RoomListWithTotalCount

fun RoomListWithTotalCountDto.toDomain(): RoomListWithTotalCount = RoomListWithTotalCount(
    rooms = rooms.map { it.toDomain() },
    totalCount = totalCount
)
