package edu.watumull.presencify.core.domain.model.shedule

data class Room(
    val id: String,
    val roomNumber: String,
    val name: String? = null,
    val type: RoomType? = null,
    val sittingCapacity: Int,
    val classes: List<ClassSession>? = null
)
