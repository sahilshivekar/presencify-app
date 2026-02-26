package edu.watumull.presencify.feature.schedule.room_details

sealed interface RoomDetailsEvent {
    data object NavigateBack : RoomDetailsEvent
    data class NavigateToEditRoom(val roomId: String) : RoomDetailsEvent
}
