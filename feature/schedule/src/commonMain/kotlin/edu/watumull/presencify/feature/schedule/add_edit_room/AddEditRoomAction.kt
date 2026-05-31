package edu.watumull.presencify.feature.schedule.add_edit_room

import edu.watumull.presencify.core.domain.enums.RoomType

sealed interface AddEditRoomAction {
    data object NavigateBack : AddEditRoomAction
    data object ConfirmNavigateBack : AddEditRoomAction
    data object DismissDialog : AddEditRoomAction

    // Room Details
    data class UpdateRoomNumber(val roomNumber: String) : AddEditRoomAction
    data class UpdateName(val name: String) : AddEditRoomAction
    data class UpdateSittingCapacity(val capacity: String) : AddEditRoomAction
    data class UpdateRoomType(val type: RoomType?) : AddEditRoomAction
    data class ChangeRoomTypeDropDownVisibility(val isVisible: Boolean) : AddEditRoomAction

    // Submit
    data object SubmitClick : AddEditRoomAction
}
