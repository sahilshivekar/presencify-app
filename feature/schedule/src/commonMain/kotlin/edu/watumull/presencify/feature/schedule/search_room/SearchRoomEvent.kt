package edu.watumull.presencify.feature.schedule.search_room

sealed interface SearchRoomEvent {
    data object NavigateBack : SearchRoomEvent
    data class NavigateToRoomDetails(val roomId: String) : SearchRoomEvent
    data object NavigateToAddEditRoom : SearchRoomEvent
}
