package edu.watumull.presencify.feature.schedule.add_edit_room

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import edu.watumull.presencify.core.design.systems.components.dialog.DialogType
import edu.watumull.presencify.core.domain.onError
import edu.watumull.presencify.core.domain.onSuccess
import edu.watumull.presencify.core.domain.repository.schedule.RoomRepository
import edu.watumull.presencify.core.presentation.UiText
import edu.watumull.presencify.core.presentation.global_snackbar.SnackbarController
import edu.watumull.presencify.core.presentation.global_snackbar.SnackbarEvent
import edu.watumull.presencify.core.presentation.toUiText
import edu.watumull.presencify.core.presentation.utils.BaseViewModel
import edu.watumull.presencify.core.presentation.validation.validateAsRoomNumber
import edu.watumull.presencify.core.presentation.validation.validateAsSittingCapacity
import edu.watumull.presencify.feature.schedule.add_edit_room.AddEditRoomEvent.NavigateBack
import edu.watumull.presencify.feature.schedule.navigation.ScheduleRoutes
import kotlinx.coroutines.launch

class AddEditRoomViewModel(
    private val roomRepository: RoomRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<AddEditRoomState, AddEditRoomEvent, AddEditRoomAction>(
    initialState = run {
        val routeParams = savedStateHandle.toRoute<ScheduleRoutes.AddEditRoom>()
        AddEditRoomState(
            isEditMode = routeParams.roomId != null,
            roomId = routeParams.roomId
        )
    }
) {

    init {
        if (stateFlow.value.isEditMode && stateFlow.value.roomId != null) {
            loadRoomDetails(stateFlow.value.roomId!!)
        }
    }

    private fun loadRoomDetails(roomId: String) {
        viewModelScope.launch {
            updateState { it.copy(viewState = AddEditRoomState.ViewState.Loading) }

            roomRepository.getRoomById(roomId)
                .onSuccess { room ->
                    updateState {
                        it.copy(
                            viewState = AddEditRoomState.ViewState.Content,
                            roomNumber = room.roomNumber,
                            name = room.name ?: "",
                            sittingCapacity = room.sittingCapacity.toString(),
                            roomType = room.type
                        )
                    }
                }
                .onError { error ->
                    updateState {
                        it.copy(
                            viewState = AddEditRoomState.ViewState.Error(error.toUiText())
                        )
                    }
                }
        }
    }

    private fun validateFields(): Boolean {
        val state = stateFlow.value

        val roomNumberValidation = state.roomNumber.validateAsRoomNumber()
        val capacityValidation = state.sittingCapacity.validateAsSittingCapacity()

        updateState {
            it.copy(
                roomNumberError = roomNumberValidation.errorMessage,
                sittingCapacityError = capacityValidation.errorMessage
            )
        }

        if (!roomNumberValidation.successful || !capacityValidation.successful) {
            val errorMessage = listOfNotNull(
                roomNumberValidation.errorMessage,
                capacityValidation.errorMessage
            ).firstOrNull() ?: "Please fix the errors"

            updateState {
                it.copy(
                    dialogState = AddEditRoomState.DialogState(
                        dialogType = DialogType.ERROR,
                        title = "Validation Error",
                        message = UiText.DynamicString(errorMessage),
                        dialogIntention = DialogIntention.GENERIC
                    )
                )
            }
            return false
        }

        return true
    }

    private fun submitRoom() {
        if (!validateFields()) return

        val state = stateFlow.value

        viewModelScope.launch {
            updateState { it.copy(isSubmitting = true) }

            val result = if (state.isEditMode && state.roomId != null) {
                roomRepository.updateRoom(
                    roomId = state.roomId,
                    roomNumber = state.roomNumber.trim(),
                    sittingCapacity = state.sittingCapacity.toInt(),
                    name = state.name.trim().ifBlank { null },
                    type = state.roomType
                )
            } else {
                roomRepository.addRoom(
                    roomNumber = state.roomNumber.trim(),
                    sittingCapacity = state.sittingCapacity.toInt(),
                    name = state.name.trim().ifBlank { null },
                    type = state.roomType
                )
            }

            result
                .onSuccess {
                    updateState { it.copy(isSubmitting = false) }

                    SnackbarController.sendEvent(
                        SnackbarEvent(
                            message = if (state.isEditMode) "Room updated successfully" else "Room added successfully"
                        )
                    )

                    sendEvent(NavigateBack)
                }
                .onError { error ->
                    updateState {
                        it.copy(
                            isSubmitting = false,
                            dialogState = AddEditRoomState.DialogState(
                                dialogType = DialogType.ERROR,
                                title = "Error",
                                message = error.toUiText(),
                                dialogIntention = DialogIntention.GENERIC
                            )
                        )
                    }
                }
        }
    }

    override fun handleAction(action: AddEditRoomAction) {
        when (action) {
            AddEditRoomAction.BackButtonClick -> {
                sendEvent(NavigateBack)
            }

            AddEditRoomAction.DismissDialog -> {
                updateState { it.copy(dialogState = null) }
            }

            is AddEditRoomAction.UpdateRoomNumber -> {
                updateState {
                    it.copy(
                        roomNumber = action.roomNumber,
                        roomNumberError = null
                    )
                }
            }

            is AddEditRoomAction.UpdateName -> {
                updateState {
                    it.copy(
                        name = action.name,
                        nameError = null
                    )
                }
            }

            is AddEditRoomAction.UpdateSittingCapacity -> {
                updateState {
                    it.copy(
                        sittingCapacity = action.capacity,
                        sittingCapacityError = null
                    )
                }
            }

            is AddEditRoomAction.UpdateRoomType -> {
                updateState {
                    it.copy(
                        roomType = action.type,
                        roomTypeError = null
                    )
                }
            }

            is AddEditRoomAction.ChangeRoomTypeDropDownVisibility -> {
                updateState { it.copy(isRoomTypeDropdownOpen = action.isVisible) }
            }

            AddEditRoomAction.SubmitClick -> {
                submitRoom()
            }
        }
    }
}
