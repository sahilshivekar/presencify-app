package edu.watumull.presencify.core.data.mapper.shedule

import edu.watumull.presencify.core.data.dto.shedule.RoomDto
import edu.watumull.presencify.core.domain.model.shedule.Room
import edu.watumull.presencify.core.domain.model.shedule.RoomType

fun RoomDto.toDomain(): Room = Room(
    id = id,
    roomNumber = roomNumber,
    name = name,
    type = RoomType.fromSerialized(type),
    sittingCapacity = sittingCapacity,
    classes = classes?.map { it.toDomain() }
)
