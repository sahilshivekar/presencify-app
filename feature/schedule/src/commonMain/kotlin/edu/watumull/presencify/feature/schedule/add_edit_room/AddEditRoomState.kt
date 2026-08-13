package edu.watumull.presencify.feature.schedule.add_edit_room

import edu.watumull.presencify.core.domain.enums.RoomType
import edu.watumull.presencify.core.presentation.UiText
import edu.watumull.presencify.core.presentation.components.dialog.DialogState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

data class AddEditRoomState(
    val viewState: ViewState = ViewState.Content,
    val dialogState: DialogState? = null,

    val isEditMode: Boolean = false,
    val roomId: String? = null,

    val roomNumber: String = "",
    val roomNumberError: String? = null,

    val name: String = "",
    val nameError: String? = null,

    val sittingCapacity: String = "",
    val sittingCapacityError: String? = null,

    val roomType: RoomType? = null,
    val roomTypeError: String? = null,

    val roomTypeOptions: ImmutableList<RoomType> = RoomType.entries.toImmutableList(),
    val isRoomTypeDropdownOpen: Boolean = false,

    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false
) {
    sealed interface ViewState {
        data object Loading : ViewState
        data class Error(val message: UiText) : ViewState
        data object Content : ViewState
    }
}
