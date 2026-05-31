package edu.watumull.presencify.feature.schedule.search_room

import androidx.lifecycle.viewModelScope
import edu.watumull.presencify.core.domain.enums.RoomSortBy
import edu.watumull.presencify.core.domain.enums.RoomSortOrder
import edu.watumull.presencify.core.domain.repository.schedule.RoomRepository
import edu.watumull.presencify.core.presentation.UiText
import edu.watumull.presencify.core.presentation.pagination.Paginator
import edu.watumull.presencify.core.presentation.toUiText
import edu.watumull.presencify.core.presentation.utils.BaseViewModel
import edu.watumull.presencify.feature.schedule.search_room.SearchRoomEvent.NavigateBack
import edu.watumull.presencify.feature.schedule.search_room.SearchRoomEvent.NavigateToAddEditRoom
import edu.watumull.presencify.feature.schedule.search_room.SearchRoomEvent.NavigateToRoomDetails
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SearchRoomViewModel(
    private val roomRepository: RoomRepository
) : BaseViewModel<SearchRoomState, SearchRoomEvent, SearchRoomAction>(
    initialState = SearchRoomState()
) {

    private val paginator = Paginator(
        initialKey = 1,
        onLoadUpdated = { isLoading ->
            updateState { it.copy(isLoadingMore = isLoading) }
        },
        onRequest = { page ->
            val state = stateFlow.value

            // Parse capacity values
            val minCap = state.minCapacity.toIntOrNull()
            val maxCap = state.maxCapacity.toIntOrNull()

            roomRepository.getRooms(
                searchQuery = state.searchQuery.ifBlank { null },
                sortBy = state.selectedSortBy,
                sortOrder = state.selectedSortOrder,
                type = state.selectedRoomTypes.firstOrNull(), // Server accepts single value
                minCapacity = minCap,
                maxCapacity = maxCap,
                freeBetweenStartTime = state.freeBetweenStartTime,
                freeBetweenEndTime = state.freeBetweenEndTime,
                dayOfWeek = state.selectedDayOfWeek,
                page = page,
                limit = 20
            )
        },
        getNextKey = { currentPage, _ ->
            currentPage + 1
        },
        onError = { error ->
            updateState {
                it.copy(
                    viewState = SearchRoomState.ViewState.Error(error.toUiText()),
                    isLoadingRooms = false
                )
            }
        },
        onSuccess = { response, _ ->
            updateState {
                it.copy(
                    rooms = if (stateFlow.value.currentPage == 1) response.rooms.toPersistentList() else it.rooms.addAll(
                        response.rooms.toPersistentList()
                    ),
                    currentPage = stateFlow.value.currentPage + 1,
                    isRefreshing = false,
                    isLoadingRooms = false
                )
            }
        },
        endReached = { currentPage, response ->
            // End reached when we have loaded all rooms
            // currentPage here is the page that was just loaded (before getNextKey is called)
            val totalLoadedRooms = currentPage * 20
            totalLoadedRooms >= response.totalCount
        }
    )

    init {
        // Setup debounced search immediately
        setupDebouncedSearch()

        viewModelScope.launch {
            // Initial load
            refreshSearch()
        }
    }

    @OptIn(FlowPreview::class)
    private fun setupDebouncedSearch() {
        viewModelScope.launch {
            stateFlow
                .map { it.searchQuery }
                .debounce(300)
                .distinctUntilChanged()
                .collectLatest { _ ->
                    refreshSearch()
                }
        }
    }

    private fun loadNextRooms() {
        viewModelScope.launch {
            paginator.loadNextItems()
        }
    }

    private fun refreshSearch() {
        updateState { it.copy(rooms = persistentListOf(), currentPage = 1) }
        paginator.reset()
        loadNextRooms()
    }

    private fun validateFilters(): Boolean {
        val state = stateFlow.value
        var isValid = true

        // Validate minCapacity
        val minCapError = if (state.minCapacity.isNotBlank()) {
            val minCap = state.minCapacity.toIntOrNull()
            when {
                minCap == null -> "Must be a valid number"
                minCap < 0 -> "Must be positive"
                else -> null
            }
        } else null

        // Validate maxCapacity
        val maxCapError = if (state.maxCapacity.isNotBlank()) {
            val maxCap = state.maxCapacity.toIntOrNull()
            when {
                maxCap == null -> "Must be a valid number"
                maxCap < 0 -> "Must be positive"
                else -> null
            }
        } else null

        // Validate capacity range
        val rangeError = if (minCapError == null && maxCapError == null &&
            state.minCapacity.isNotBlank() && state.maxCapacity.isNotBlank()) {
            val minCap = state.minCapacity.toInt()
            val maxCap = state.maxCapacity.toInt()
            if (minCap > maxCap) "Min capacity cannot be greater than max capacity" else null
        } else null

        // Validate busy time range
        var busyStartError: String? = null
        var busyEndError: String? = null
        var dayOfWeekError: String? = null

        // Validate that all three (start time, end time, day of week) are selected together or none
        val hasStartTime = state.freeBetweenStartTime != null
        val hasEndTime = state.freeBetweenEndTime != null
        val hasDayOfWeek = state.selectedDayOfWeek != null

        val timeAndDayCount = listOf(hasStartTime, hasEndTime, hasDayOfWeek).count { it }

        if (timeAndDayCount > 0 && timeAndDayCount < 3) {
            if (!hasStartTime) {
                busyStartError = "Start time is required when filtering by availability"
            }
            if (!hasEndTime) {
                busyEndError = "End time is required when filtering by availability"
            }
            if (!hasDayOfWeek) {
                dayOfWeekError = "Day of week is required when filtering by availability"
            }
        } else if (hasStartTime && hasEndTime && state.freeBetweenStartTime!! >= state.freeBetweenEndTime!!) {
            busyEndError = "End time must be after start time"
        }

        updateState {
            it.copy(
                minCapacityError = minCapError,
                maxCapacityError = rangeError ?: maxCapError,
                busyStartTimeError = busyStartError,
                busyEndTimeError = busyEndError,
                dayOfWeekError = dayOfWeekError
            )
        }

        isValid = minCapError == null && maxCapError == null && rangeError == null &&
                  busyStartError == null && busyEndError == null && dayOfWeekError == null

        if (!isValid) {
            val errorMessage = listOfNotNull(
                minCapError, maxCapError, rangeError, busyStartError, busyEndError, dayOfWeekError
            ).firstOrNull() ?: "Invalid filter values"

            updateState {
                it.copy(
                    viewState = SearchRoomState.ViewState.Error(UiText.DynamicString(errorMessage))
                )
            }
        }

        return isValid
    }

    override fun handleAction(action: SearchRoomAction) {
        when (action) {
            SearchRoomAction.NavigateBack -> {
                sendEvent(NavigateBack)
            }


            is SearchRoomAction.UpdateSearchQuery -> {
                updateState { it.copy(searchQuery = action.query) }
            }

            SearchRoomAction.Search -> {
                refreshSearch()
            }

            SearchRoomAction.Refresh -> {
                updateState { it.copy(isRefreshing = true) }
                refreshSearch()
            }

            is SearchRoomAction.SelectSortBy -> {
                updateState { it.copy(selectedSortBy = action.sortBy) }
            }

            is SearchRoomAction.SelectSortOrder -> {
                updateState { it.copy(selectedSortOrder = action.sortOrder) }
            }

            is SearchRoomAction.ToggleRoomType -> {
                updateState {
                    val currentTypes = it.selectedRoomTypes
                    val newTypes = if (currentTypes.contains(action.roomType)) {
                        currentTypes.remove(action.roomType)
                    } else {
                        // Server accepts only single value, so clear others
                        persistentListOf(action.roomType)
                    }
                    it.copy(selectedRoomTypes = newTypes)
                }
            }

            is SearchRoomAction.SelectDayOfWeek -> {
                updateState {
                    it.copy(
                        selectedDayOfWeek = action.dayOfWeek,
                        dayOfWeekError = null
                    )
                }
            }

            is SearchRoomAction.UpdateMinCapacity -> {
                updateState {
                    it.copy(
                        minCapacity = action.capacity,
                        minCapacityError = null
                    )
                }
            }

            is SearchRoomAction.UpdateMaxCapacity -> {
                updateState {
                    it.copy(
                        maxCapacity = action.capacity,
                        maxCapacityError = null
                    )
                }
            }

            is SearchRoomAction.UpdateBusyStartTime -> {
                updateState {
                    it.copy(
                        freeBetweenStartTime = action.time,
                        busyStartTimeError = null
                    )
                }
            }

            is SearchRoomAction.UpdateBusyEndTime -> {
                updateState {
                    it.copy(
                        freeBetweenEndTime = action.time,
                        busyEndTimeError = null
                    )
                }
            }

            SearchRoomAction.ResetFilters -> {
                updateState {
                    it.copy(
                        selectedSortBy = RoomSortBy.ROOM_NUMBER,
                        selectedSortOrder = RoomSortOrder.ASCENDING,
                        selectedRoomTypes = persistentListOf(),
                        selectedDayOfWeek = null,
                        minCapacity = "",
                        maxCapacity = "",
                        minCapacityError = null,
                        maxCapacityError = null,
                        freeBetweenStartTime = null,
                        freeBetweenEndTime = null,
                        busyStartTimeError = null,
                        busyEndTimeError = null,
                        dayOfWeekError = null
                    )
                }
            }

            SearchRoomAction.ApplyFilters -> {
                if (validateFilters()) {
                    refreshSearch()
                }
            }

            is SearchRoomAction.RoomCardClick -> {
                sendEvent(NavigateToRoomDetails(action.roomId))
            }

            SearchRoomAction.LoadMoreRooms -> {
                loadNextRooms()
            }

            SearchRoomAction.ClickFloatingActionButton -> {
                sendEvent(NavigateToAddEditRoom)
            }
        }
    }
}
