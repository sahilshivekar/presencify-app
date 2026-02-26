package edu.watumull.presencify.feature.schedule.search_room

import edu.watumull.presencify.core.design.systems.components.dialog.DialogType
import edu.watumull.presencify.core.domain.enums.DayOfWeek
import edu.watumull.presencify.core.domain.enums.RoomSortBy
import edu.watumull.presencify.core.domain.enums.RoomSortOrder
import edu.watumull.presencify.core.domain.enums.RoomType
import edu.watumull.presencify.core.domain.model.schedule.Room
import edu.watumull.presencify.core.presentation.UiText
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.datetime.LocalTime

data class SearchRoomState(
    val viewState: ViewState = ViewState.Content,
    val dialogState: DialogState? = null,

    // Search & Filter
    val searchQuery: String = "",
    val isRefreshing: Boolean = false,

    // Rooms List
    val rooms: PersistentList<Room> = persistentListOf(),
    val isLoadingRooms: Boolean = true,

    // Filter Options - Sort By
    val sortByOptions: ImmutableList<RoomSortBy> = RoomSortBy.entries.toImmutableList(),
    val selectedSortBy: RoomSortBy = RoomSortBy.ROOM_NUMBER,

    // Filter Options - Sort Order
    val sortOrderOptions: ImmutableList<RoomSortOrder> = RoomSortOrder.entries.toImmutableList(),
    val selectedSortOrder: RoomSortOrder = RoomSortOrder.ASCENDING,

    // Filter Options - Room Type
    val roomTypeOptions: ImmutableList<RoomType> = RoomType.entries.toImmutableList(),
    val selectedRoomTypes: PersistentList<RoomType> = persistentListOf(),

    // Filter Options - Day of Week
    val dayOfWeekOptions: ImmutableList<DayOfWeek> = DayOfWeek.entries.toImmutableList(),
    val selectedDayOfWeek: DayOfWeek? = null,

    // Filter Options - Capacity
    val minCapacity: String = "",
    val maxCapacity: String = "",
    val minCapacityError: String? = null,
    val maxCapacityError: String? = null,

    // Filter Options - Busy Time Range
    val freeBetweenStartTime: LocalTime? = null,
    val freeBetweenEndTime: LocalTime? = null,
    val busyStartTimeError: String? = null,
    val busyEndTimeError: String? = null,
    val dayOfWeekError: String? = null,

    // Pagination
    val currentPage: Int = 1,
    val isLoadingMore: Boolean = false
) {
    sealed interface ViewState {
        data object Loading : ViewState
        data class Error(val message: UiText) : ViewState
        data object Content : ViewState
    }

    data class DialogState(
        val isVisible: Boolean = true,
        val dialogType: DialogType = DialogType.INFO,
        val dialogIntention: DialogIntention = DialogIntention.GENERIC,
        val title: String = "",
        val message: UiText = UiText.DynamicString(""),
    )
}

enum class DialogIntention {
    GENERIC,
}
