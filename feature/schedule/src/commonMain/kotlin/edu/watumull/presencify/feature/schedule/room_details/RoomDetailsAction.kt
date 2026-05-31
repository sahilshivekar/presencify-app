package edu.watumull.presencify.feature.schedule.room_details

sealed interface RoomDetailsAction {
    data object NavigateBack : RoomDetailsAction
    data object DismissDialog : RoomDetailsAction
    data object RemoveRoomClick : RoomDetailsAction
    data object ConfirmRemoveRoom : RoomDetailsAction
    data object EditRoomClick : RoomDetailsAction
}
