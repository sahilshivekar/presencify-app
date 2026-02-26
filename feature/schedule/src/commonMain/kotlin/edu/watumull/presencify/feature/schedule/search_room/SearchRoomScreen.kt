package edu.watumull.presencify.feature.schedule.search_room

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import edu.watumull.presencify.core.design.systems.components.PresencifyBottomSheetScaffold
import edu.watumull.presencify.core.design.systems.components.PresencifyButton
import edu.watumull.presencify.core.design.systems.components.PresencifyDefaultLoadingScreen
import edu.watumull.presencify.core.design.systems.components.PresencifyNoResultsIndicator
import edu.watumull.presencify.core.design.systems.components.PresencifyOutlinedButton
import edu.watumull.presencify.core.design.systems.components.PresencifyOutlinedButton
import edu.watumull.presencify.core.design.systems.components.PresencifySearchBar
import edu.watumull.presencify.core.design.systems.components.PresencifyTextButton
import edu.watumull.presencify.core.design.systems.components.PresencifyTextField
import edu.watumull.presencify.core.design.systems.components.dialog.PresencifyAlertDialog
import edu.watumull.presencify.core.domain.enums.RoomSortBy
import edu.watumull.presencify.core.domain.enums.RoomSortOrder
import edu.watumull.presencify.core.presentation.UiConstants
import edu.watumull.presencify.core.presentation.components.RoomListItem
import edu.watumull.presencify.core.presentation.utils.toReadableString
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchRoomScreen(
    state: SearchRoomState,
    onAction: (SearchRoomAction) -> Unit,
) {
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            skipHiddenState = false,
            initialValue = SheetValue.Hidden
        )
    )

    val scope = rememberCoroutineScope()

    PresencifyBottomSheetScaffold(
        backPress = { onAction(SearchRoomAction.BackButtonClick) },
        topBarTitle = "Search Rooms",
        scaffoldState = scaffoldState,
        sheetContent = {
            SearchRoomBottomSheetContent(
                state = state,
                onAction = onAction,
                onDismiss = {
                    scope.launch { scaffoldState.bottomSheetState.hide() }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onAction(SearchRoomAction.ClickFloatingActionButton) },
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add room"
                )
            }
        }
    ) { paddingValues ->
        when (state.viewState) {
            is SearchRoomState.ViewState.Loading -> {
                PresencifyDefaultLoadingScreen()
            }

            is SearchRoomState.ViewState.Error -> {
                PresencifyNoResultsIndicator(
                    text = state.viewState.message.asString()
                )
            }

            is SearchRoomState.ViewState.Content -> {
                SearchRoomScreenContent(
                    state = state,
                    onAction = onAction,
                    onFilterClick = { scope.launch { scaffoldState.bottomSheetState.expand() } },
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }

    state.dialogState?.let { dialogState ->
        PresencifyAlertDialog(
            isVisible = dialogState.isVisible,
            dialogType = dialogState.dialogType,
            title = dialogState.title,
            message = dialogState.message.asString(),
            onConfirm = {
                when (dialogState.dialogIntention) {
                    DialogIntention.GENERIC -> {
                        // Handle generic dialog confirmation
                    }
                }
            },
            onDismiss = {
                onAction(SearchRoomAction.DismissDialog)
            }
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun SearchRoomScreenContent(
    state: SearchRoomState,
    onAction: (SearchRoomAction) -> Unit,
    onFilterClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val pullRefreshState = rememberPullRefreshState(
        refreshing = state.isRefreshing,
        onRefresh = { onAction(SearchRoomAction.Refresh) }
    )
    val lazyListState = rememberLazyListState()

    LaunchedEffect(state.rooms) {
        snapshotFlow {
            lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
        }.distinctUntilChanged().collect { lastVisibleIndex ->
            // If lastVisibleIndex == 0 then it means the list is empty and the loading indicator is an inside item{} taking index 0
            // initial load should only be trigger within init block of the view model, so that we can apply pre-filtering before loading students for the first time
            if (lastVisibleIndex != null && lastVisibleIndex != 0 && lastVisibleIndex >= state.rooms.lastIndex - 10) {
                onAction(SearchRoomAction.LoadMoreRooms)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = UiConstants.MAX_CONTENT_WIDTH)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            PresencifySearchBar(
                query = state.searchQuery,
                onQueryChange = { onAction(SearchRoomAction.UpdateSearchQuery(it)) },
                onFilterClick = onFilterClick,
                placeholder = "Search rooms...",
                onSearchClick = { onAction(SearchRoomAction.Search) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .pullRefresh(pullRefreshState)
            ) {
                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = state.rooms,
                        key = { it.id }
                    ) { room ->
                        RoomListItem(
                            roomNumber = room.roomNumber,
                            sittingCapacity = room.sittingCapacity,
                            type = room.type,
                            name = room.name,
                            onClick = { onAction(SearchRoomAction.RoomCardClick(room.id)) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        when {
                            state.isLoadingMore || (state.rooms.isEmpty() && state.isLoadingRooms) -> {
                                PresencifyDefaultLoadingScreen()
                            }


                            state.rooms.isEmpty() && !state.isLoadingRooms -> {
                                PresencifyNoResultsIndicator(
                                    text = "No students found"
                                )
                            }
                        }
                    }
                }
                if (state.isRefreshing) {
                    PullRefreshIndicator(
                        refreshing = state.isRefreshing,
                        state = pullRefreshState,
                        modifier = Modifier.align(Alignment.TopCenter)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchRoomBottomSheetContent(
    state: SearchRoomState,
    onAction: (SearchRoomAction) -> Unit,
    onDismiss: () -> Unit,
) {
    val startTimePickerState = rememberTimePickerState()
    val endTimePickerState = rememberTimePickerState()
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header
        Text(
            text = "Filter Rooms",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        // Sort By Filter
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Sort By",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.sortByOptions.forEach { sortBy ->
                    FilterChip(
                        selected = state.selectedSortBy == sortBy,
                        onClick = { onAction(SearchRoomAction.SelectSortBy(sortBy)) },
                        label = {
                            Text(
                                text = when (sortBy) {
                                    RoomSortBy.ROOM_NUMBER -> "Room Number"
                                    RoomSortBy.SITTING_CAPACITY -> "Capacity"
                                }
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }
        }

        // Sort Order Filter
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Sort Order",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.sortOrderOptions.forEach { sortOrder ->
                    FilterChip(
                        selected = state.selectedSortOrder == sortOrder,
                        onClick = { onAction(SearchRoomAction.SelectSortOrder(sortOrder)) },
                        label = {
                            Text(
                                text = when (sortOrder) {
                                    RoomSortOrder.ASCENDING -> "Ascending"
                                    RoomSortOrder.DESCENDING -> "Descending"
                                }
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }
        }

        // Room Type Filter
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Room Type",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.roomTypeOptions.forEach { roomType ->
                    FilterChip(
                        selected = state.selectedRoomTypes.contains(roomType),
                        onClick = { onAction(SearchRoomAction.ToggleRoomType(roomType)) },
                        label = { Text(text = roomType.toDisplayLabel()) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }
        }


        // Capacity Range Filter
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Capacity Range",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                PresencifyTextField(
                    value = state.minCapacity,
                    onValueChange = { onAction(SearchRoomAction.UpdateMinCapacity(it)) },
                    label = "Min Capacity",
                    supportingText = state.minCapacityError,
                    isError = state.minCapacityError != null,
                    modifier = Modifier.weight(1f)
                )

                PresencifyTextField(
                    value = state.maxCapacity,
                    onValueChange = { onAction(SearchRoomAction.UpdateMaxCapacity(it)) },
                    label = "Max Capacity",
                    supportingText = state.maxCapacityError,
                    isError = state.maxCapacityError != null,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Busy Time Range Filter
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Show rooms available on",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Start Time Picker
                PresencifyTextField(
                    value = state.freeBetweenStartTime?.toReadableString() ?: "",
                    onValueChange = { },
                    label = "Start Time",
                    readOnly = true,
                    supportingText = state.busyStartTimeError,
                    isError = state.busyStartTimeError != null,
                    leadingIcon = {
                        IconButton(onClick = { showStartTimePicker = true }) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = "Select start time"
                            )
                        }
                    },
                    trailingIcon = if (state.freeBetweenStartTime != null) {
                        {
                            IconButton(onClick = { onAction(SearchRoomAction.UpdateBusyStartTime(null)) }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear start time"
                                )
                            }
                        }
                    } else null,
                    modifier = Modifier.weight(1f)
                )

                // End Time Picker
                PresencifyTextField(
                    value = state.freeBetweenEndTime?.toReadableString() ?: "",
                    onValueChange = { },
                    label = "End Time",
                    readOnly = true,
                    supportingText = state.busyEndTimeError,
                    isError = state.busyEndTimeError != null,
                    leadingIcon = {
                        IconButton(onClick = { showEndTimePicker = true }) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = "Select end time"
                            )
                        }
                    },
                    trailingIcon = if (state.freeBetweenEndTime != null) {
                        {
                            IconButton(onClick = { onAction(SearchRoomAction.UpdateBusyEndTime(null)) }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear end time"
                                )
                            }
                        }
                    } else null,
                    modifier = Modifier.weight(1f)
                )
            }
            // Day of Week Filter
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    state.dayOfWeekOptions.forEach { day ->
                        FilterChip(
                            selected = state.selectedDayOfWeek == day,
                            onClick = {
                                onAction(
                                    SearchRoomAction.SelectDayOfWeek(
                                        if (state.selectedDayOfWeek == day) null else day
                                    )
                                )
                            },
                            label = { Text(text = day.toDisplayLabel()) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }

                // Show error if exists
                state.dayOfWeekError?.let { error ->
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

        }

        // Action Buttons
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            PresencifyOutlinedButton(
                onClick = {
                    onAction(SearchRoomAction.ResetFilters)
                },
                text = "Reset",
                modifier = Modifier.weight(1f)
            )

            PresencifyButton(
                onClick = {
                    onAction(SearchRoomAction.ApplyFilters)
                    onDismiss()
                },
                text = "Apply",
                modifier = Modifier.weight(1f)
            )
        }
    }

    // Start Time Picker Dialog
    if (showStartTimePicker) {
        TimePickerDialog(
            onDismissRequest = { showStartTimePicker = false },
            onConfirm = {
                val time = LocalTime(startTimePickerState.hour, startTimePickerState.minute)
                onAction(SearchRoomAction.UpdateBusyStartTime(time))
                showStartTimePicker = false
            }
        ) {
            TimePicker(state = startTimePickerState)
        }
    }

    // End Time Picker Dialog
    if (showEndTimePicker) {
        TimePickerDialog(
            onDismissRequest = { showEndTimePicker = false },
            onConfirm = {
                val time = LocalTime(endTimePickerState.hour, endTimePickerState.minute)
                onAction(SearchRoomAction.UpdateBusyEndTime(time))
                showEndTimePicker = false
            }
        ) {
            TimePicker(state = endTimePickerState)
        }
    }
}

@Composable
private fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                content()
            }
        }
    )
}
