package edu.watumull.presencify.feature.schedule.search_room

import edu.watumull.presencify.core.domain.enums.DayOfWeek
import edu.watumull.presencify.core.domain.enums.RoomSortBy
import edu.watumull.presencify.core.domain.enums.RoomSortOrder
import edu.watumull.presencify.core.domain.enums.RoomType
import kotlinx.datetime.LocalTime

sealed interface SearchRoomAction {
    data object BackButtonClick : SearchRoomAction
    data object DismissDialog : SearchRoomAction

    // Search & Refresh
    data class UpdateSearchQuery(val query: String) : SearchRoomAction
    data object Search : SearchRoomAction
    data object Refresh : SearchRoomAction

    // Filters - Sort
    data class SelectSortBy(val sortBy: RoomSortBy) : SearchRoomAction
    data class SelectSortOrder(val sortOrder: RoomSortOrder) : SearchRoomAction

    // Filters - Room Type
    data class ToggleRoomType(val roomType: RoomType) : SearchRoomAction

    // Filters - Day of Week (single selection)
    data class SelectDayOfWeek(val dayOfWeek: DayOfWeek?) : SearchRoomAction

    // Filters - Capacity
    data class UpdateMinCapacity(val capacity: String) : SearchRoomAction
    data class UpdateMaxCapacity(val capacity: String) : SearchRoomAction

    // Filters - Busy Time Range
    data class UpdateBusyStartTime(val time: LocalTime?) : SearchRoomAction
    data class UpdateBusyEndTime(val time: LocalTime?) : SearchRoomAction

    data object ResetFilters : SearchRoomAction
    data object ApplyFilters : SearchRoomAction

    // Room Actions
    data class RoomCardClick(val roomId: String) : SearchRoomAction

    // Pagination
    data object LoadMoreRooms : SearchRoomAction

    data object ClickFloatingActionButton : SearchRoomAction
}
