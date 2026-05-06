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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import edu.watumull.presencify.core.designsystem.components.PresencifyBottomSheetScaffold
import edu.watumull.presencify.core.designsystem.components.PresencifyButton
import edu.watumull.presencify.core.designsystem.components.PresencifyDefaultLoadingScreen
import edu.watumull.presencify.core.designsystem.components.PresencifyNoResultsIndicator
import edu.watumull.presencify.core.designsystem.components.PresencifyOutlinedButton
import edu.watumull.presencify.core.designsystem.components.PresencifySearchBar
import edu.watumull.presencify.core.designsystem.components.PresencifyTextField
import edu.watumull.presencify.core.designsystem.components.PresencifyTimePickerTextField
import edu.watumull.presencify.core.designsystem.components.dialog.PresencifyAlertDialog
import edu.watumull.presencify.core.designsystem.theme.DesignToken
import edu.watumull.presencify.core.domain.enums.RoomSortBy
import edu.watumull.presencify.core.domain.enums.RoomSortOrder
import edu.watumull.presencify.core.domain.model.auth.UserRole
import edu.watumull.presencify.core.presentation.UiConstants
import edu.watumull.presencify.core.presentation.components.RoomListItem
import edu.watumull.presencify.core.presentation.composition_locals.LocalUserRole
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

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
            if (LocalUserRole.current == UserRole.ADMIN) {

                FloatingActionButton(
                    onClick = { onAction(SearchRoomAction.ClickFloatingActionButton) },
                    modifier = Modifier.padding(DesignToken.spacing.lg)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add room"
                    )
                }
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
            .padding(DesignToken.spacing.lg),
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

            Spacer(modifier = Modifier.height(DesignToken.spacing.lg))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .pullRefresh(pullRefreshState)
            ) {
                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.md)
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

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(DesignToken.spacing.xl)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.lg)
    ) {
        // Header
        Text(
            text = "Filter Rooms",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        // Sort By Filter
        Column(verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.sm)) {
            Text(
                text = "Sort By",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(DesignToken.spacing.sm),
                verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.sm)
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
        Column(verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.sm)) {
            Text(
                text = "Sort Order",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(DesignToken.spacing.sm),
                verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.sm)
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
        Column(verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.sm)) {
            Text(
                text = "Room Type",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(DesignToken.spacing.sm),
                verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.sm)
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
        Column(verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.sm)) {
            Text(
                text = "Capacity Range",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(DesignToken.spacing.md),
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
        Column(verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.sm)) {
            Text(
                text = "Show rooms available on",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(DesignToken.spacing.md),
                modifier = Modifier.fillMaxWidth()
            ) {
                PresencifyTimePickerTextField(
                    value = state.freeBetweenStartTime,
                    onValueChange = { onAction(SearchRoomAction.UpdateBusyStartTime(it)) },
                    label = "Start Time",
                    modifier = Modifier.weight(1f),
                    supportingText = state.busyStartTimeError,
                    isError = state.busyStartTimeError != null,
                )
                PresencifyTimePickerTextField(
                    value = state.freeBetweenEndTime,
                    onValueChange = { onAction(SearchRoomAction.UpdateBusyEndTime(it)) },
                    label = "End Time",
                    modifier = Modifier.weight(1f),
                    supportingText = state.busyEndTimeError,
                    isError = state.busyEndTimeError != null,
                )
            }
            // Day of Week Filter
            Column(verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.sm)) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(DesignToken.spacing.sm),
                    verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.sm)
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
            horizontalArrangement = Arrangement.spacedBy(DesignToken.spacing.md),
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


}
