package edu.watumull.presencify.feature.schedule.search_room

import edu.watumull.presencify.core.domain.enums.DayOfWeek
import edu.watumull.presencify.core.domain.enums.RoomSortBy
import edu.watumull.presencify.core.domain.enums.RoomSortOrder
import edu.watumull.presencify.core.domain.enums.RoomType
import kotlinx.datetime.LocalTime

sealed interface SearchRoomAction {
    data object NavigateBack : SearchRoomAction
    data object DismissDialog : SearchRoomAction

    data class UpdateSearchQuery(val query: String) : SearchRoomAction
    data object Search : SearchRoomAction
    data object Refresh : SearchRoomAction

    data class SelectSortBy(val sortBy: RoomSortBy) : SearchRoomAction
    data class SelectSortOrder(val sortOrder: RoomSortOrder) : SearchRoomAction

    data class ToggleRoomType(val roomType: RoomType) : SearchRoomAction

    data class SelectDayOfWeek(val dayOfWeek: DayOfWeek?) : SearchRoomAction

    data class UpdateMinCapacity(val capacity: String) : SearchRoomAction
    data class UpdateMaxCapacity(val capacity: String) : SearchRoomAction

    data class UpdateBusyStartTime(val time: LocalTime?) : SearchRoomAction
    data class UpdateBusyEndTime(val time: LocalTime?) : SearchRoomAction

    data object ResetFilters : SearchRoomAction
    data object ApplyFilters : SearchRoomAction

    data class RoomCardClick(val roomId: String) : SearchRoomAction

    data object LoadMoreRooms : SearchRoomAction

    data object ClickFloatingActionButton : SearchRoomAction
}
