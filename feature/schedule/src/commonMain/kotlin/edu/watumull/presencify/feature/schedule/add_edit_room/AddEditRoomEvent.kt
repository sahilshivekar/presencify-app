package edu.watumull.presencify.feature.schedule.add_edit_room

sealed interface AddEditRoomEvent {
    data object NavigateBack : AddEditRoomEvent
}
