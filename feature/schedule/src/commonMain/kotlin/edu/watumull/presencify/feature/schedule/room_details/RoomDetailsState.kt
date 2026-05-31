package edu.watumull.presencify.feature.schedule.room_details

import edu.watumull.presencify.core.presentation.components.dialog.DialogState
import edu.watumull.presencify.core.domain.model.schedule.Room
import edu.watumull.presencify.core.presentation.UiText

data class RoomDetailsState(
    val viewState: ViewState = ViewState.Loading,
    val dialogState: DialogState? = null,
    val roomId: String = "",
    val room: Room? = null,
    val isRemovingRoom: Boolean = false,
) {
    sealed interface ViewState {
        data object Loading : ViewState
        data class Error(val message: UiText) : ViewState
        data object Content : ViewState
    }
}
