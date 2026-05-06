package edu.watumull.presencify.feature.schedule.room_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import edu.watumull.presencify.core.designsystem.components.dialog.DialogType
import edu.watumull.presencify.core.domain.onError
import edu.watumull.presencify.core.domain.onSuccess
import edu.watumull.presencify.core.domain.repository.schedule.RoomRepository
import edu.watumull.presencify.core.presentation.UiText
import edu.watumull.presencify.core.presentation.global_snackbar.SnackbarController
import edu.watumull.presencify.core.presentation.global_snackbar.SnackbarEvent
import edu.watumull.presencify.core.presentation.toUiText
import edu.watumull.presencify.core.presentation.utils.BaseViewModel
import edu.watumull.presencify.feature.schedule.navigation.ScheduleRoutes
import kotlinx.coroutines.launch

class RoomDetailsViewModel(
    private val roomRepository: RoomRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<RoomDetailsState, RoomDetailsEvent, RoomDetailsAction>(
    initialState = RoomDetailsState(
        roomId = savedStateHandle.toRoute<ScheduleRoutes.RoomDetails>().roomId
    )
) {

    init {
        viewModelScope.launch {
            loadRoom()
        }
    }

    private suspend fun loadRoom() {
        val roomId = state.roomId

        roomRepository.getRoomById(roomId)
            .onSuccess { room ->
                updateState { it.copy(viewState = RoomDetailsState.ViewState.Content, room = room) }
            }
            .onError { error ->
                updateState { it.copy(viewState = RoomDetailsState.ViewState.Error(error.toUiText())) }
            }
    }

    override fun handleAction(action: RoomDetailsAction) {
        when (action) {
            is RoomDetailsAction.BackButtonClick -> sendEvent(RoomDetailsEvent.NavigateBack)
            is RoomDetailsAction.DismissDialog -> updateState { it.copy(dialogState = null) }
            is RoomDetailsAction.RemoveRoomClick -> updateState {
                it.copy(
                    dialogState = RoomDetailsState.DialogState(
                        dialogType = DialogType.CONFIRM_RISKY_ACTION,
                        dialogIntention = DialogIntention.CONFIRM_REMOVE_ROOM,
                        title = "Remove Room",
                        message = UiText.DynamicString(
                            "Are you sure you want to remove ${state.room?.roomNumber ?: "this room"}?"
                        )
                    )
                )
            }
            is RoomDetailsAction.ConfirmRemoveRoom -> {
                viewModelScope.launch {
                    removeRoom()
                }
            }
            is RoomDetailsAction.EditRoomClick -> sendEvent(RoomDetailsEvent.NavigateToEditRoom(state.roomId))
        }
    }

    private suspend fun removeRoom() {
        val roomId = state.room?.id ?: return

        updateState { it.copy(isRemovingRoom = true, dialogState = null) }

        roomRepository.removeRoom(roomId)
            .onSuccess {
                updateState { it.copy(isRemovingRoom = false) }
                viewModelScope.launch {
                    SnackbarController.sendEvent(
                        SnackbarEvent(message = "Room removed successfully")
                    )
                }
                sendEvent(RoomDetailsEvent.NavigateBack)
            }
            .onError { error ->
                updateState {
                    it.copy(
                        isRemovingRoom = false,
                        dialogState = RoomDetailsState.DialogState(
                            dialogType = DialogType.ERROR,
                            dialogIntention = DialogIntention.GENERIC,
                            title = "Error Removing Room",
                            message = error.toUiText()
                        )
                    )
                }
            }
    }
}
