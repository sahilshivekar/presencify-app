package edu.watumull.presencify.feature.schedule.search_class

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberDatePickerState
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
import edu.watumull.presencify.core.design.systems.components.PresencifySearchBar
import edu.watumull.presencify.core.design.systems.components.PresencifyTextButton
import edu.watumull.presencify.core.design.systems.components.PresencifyTextField
import edu.watumull.presencify.core.design.systems.components.dialog.PresencifyAlertDialog
import edu.watumull.presencify.core.presentation.UiConstants
import edu.watumull.presencify.core.presentation.components.ClassListItem
import edu.watumull.presencify.core.presentation.utils.toReadableString
import edu.watumull.presencify.feature.schedule.navigation.SearchClassIntention
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchClassScreen(
    state: SearchClassState,
    onAction: (SearchClassAction) -> Unit,
) {
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            skipHiddenState = false,
            initialValue = SheetValue.Hidden
        )
    )

    val scope = rememberCoroutineScope()

    PresencifyBottomSheetScaffold(
        backPress = { onAction(SearchClassAction.BackButtonClick) },
        topBarTitle = if(state.intention == SearchClassIntention.CREATE_ATTENDANCE_SHEET) "Select Class" else "Search Classes",
        scaffoldState = scaffoldState,
        sheetContent = {
            SearchClassBottomSheetContent(
                state = state,
                onAction = onAction,
                onDismiss = {
                    scope.launch { scaffoldState.bottomSheetState.hide() }
                },
            )
        }
    ) { paddingValues ->
        when (state.viewState) {
            is SearchClassState.ViewState.Loading -> {
                PresencifyDefaultLoadingScreen()
            }

            is SearchClassState.ViewState.Error -> {
                PresencifyNoResultsIndicator(
                    text = state.viewState.message.asString()
                )
            }

            is SearchClassState.ViewState.Content -> {
                SearchClassScreenContent(
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
                onAction(SearchClassAction.DismissDialog)
            }
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun SearchClassScreenContent(
    state: SearchClassState,
    onAction: (SearchClassAction) -> Unit,
    onFilterClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val pullRefreshState = rememberPullRefreshState(
        refreshing = state.isRefreshing,
        onRefresh = { onAction(SearchClassAction.Refresh) }
    )
    val lazyListState = rememberLazyListState()

    LaunchedEffect(state.classes) {
        snapshotFlow {
            lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
        }.distinctUntilChanged().collect { lastVisibleIndex ->
            // If lastVisibleIndex == 0 then it means the list is empty and the loading indicator is an inside item{} taking index 0
            // initial load should only be trigger within init block of the view model, so that we can apply pre-filtering before loading students for the first time
            if (lastVisibleIndex != null && lastVisibleIndex != 0 && lastVisibleIndex >= state.classes.lastIndex - 10) {
                onAction(SearchClassAction.LoadMoreClasses)
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
                onQueryChange = { onAction(SearchClassAction.UpdateSearchQuery(it)) },
                onFilterClick = onFilterClick,
                placeholder = "Search classes...",
                onSearchClick = { onAction(SearchClassAction.Search) }
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
                        items = state.classes,
                        key = { it.id }
                    ) { classSession ->
                        // Extract division and semester info
                        val division = classSession.timetable?.division
                        val batch = classSession.batch
                        val semester = division?.semester
                        val branch = semester?.branch

                        // Build division/batch display text
                        val divisionBatchText = when {
                            batch != null -> batch.batchCode
                            division != null -> division.divisionCode
                            else -> null
                        }

                        // Build semester and academic year text
                        val semesterText = semester?.let { sem ->
                            val semNum = sem.semesterNumber.value
                            val academicYear = "${sem.academicStartYear}-${sem.academicEndYear}"
                            "Sem: $semNum $academicYear"
                        }

                        ClassListItem(
                            courseName = classSession.course?.name ?: "Unknown Course",
                            teacherName = classSession.teacher?.let { "${it.firstName} ${it.lastName}" } ?: "Unknown Teacher",
                            startTime = classSession.startTime.toReadableString(),
                            endTime = classSession.endTime.toReadableString(),
                            dayOfWeek = classSession.dayOfWeek.toDisplayLabel(),
                            activeFrom = classSession.activeFrom.toReadableString(),
                            activeTill = classSession.activeTill.toReadableString(),
                            classType = classSession.classType.toDisplayLabel(),
                            isExtraClass = classSession.isExtraClass,
                            roomNumber = classSession.room?.roomNumber,
                            divisionOrBatchText = divisionBatchText,
                            branchAbbreviation = branch?.abbreviation,
                            semesterText = semesterText,
                            onClick = { onAction(SearchClassAction.ClassCardClick(classSession.id)) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        when {
                            state.isLoadingMore || (state.classes.isEmpty() && state.isLoadingClasses) -> {
                                PresencifyDefaultLoadingScreen()
                            }

                            state.classes.isEmpty() && !state.isLoadingClasses -> {
                                PresencifyNoResultsIndicator(
                                    text = "No classes found"
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
                )}
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun SearchClassBottomSheetContent(
    state: SearchClassState,
    onAction: (SearchClassAction) -> Unit,
    onDismiss: () -> Unit,
) {
    val startTimePickerState = rememberTimePickerState()
    val endTimePickerState = rememberTimePickerState()
    val activeFromDatePickerState = rememberDatePickerState()
    val activeTillDatePickerState = rememberDatePickerState()
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }
    var showActiveFromPicker by remember { mutableStateOf(false) }
    var showActiveTillPicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header with Reset
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Filter Classes",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Reset",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable {
                    onAction(SearchClassAction.ResetFilters)
                }
            )
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // Branch Filter
        FilterSection(
            title = "Branch",
            isLoading = state.areBranchesLoading
        ) {
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.branchOptions.forEach { branch ->
                    FilterChip(
                        selected = state.selectedBranches.contains(branch),
                        onClick = { onAction(SearchClassAction.ToggleBranch(branch)) },
                        label = { Text(branch.abbreviation) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }
        }

        // Semester Filter
        FilterSection(title = "Semester") {
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.semesterOptions.forEach { semester ->
                    FilterChip(
                        selected = state.selectedSemesters.contains(semester),
                        onClick = { onAction(SearchClassAction.ToggleSemester(semester)) },
                        label = { Text("Sem ${semester.value}") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }
        }

        // Academic Year of Semester Filter
        FilterSection(title = "Academic Year of Semester") {
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.academicYearOfSemesterOptions.forEach { year ->
                    FilterChip(
                        selected = state.selectedAcademicYearOfSemester == year,
                        onClick = {
                            val newYear = if (state.selectedAcademicYearOfSemester == year) null else year
                            onAction(SearchClassAction.SelectAcademicYearOfSemester(newYear))
                        },
                        label = { Text(year) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }
        }

        // Division Filter
        FilterSection(
            title = "Division",
            isLoading = state.areDivisionsLoading,
            emptyMessage = when {
                state.selectedSemesters.isEmpty() ||
                        state.selectedBranches.isEmpty() ||
                        state.selectedAcademicYearOfSemester == null ->
                    "Select semester, branch, and academic year to load divisions"
                state.divisionOptions.isEmpty() ->
                    "No divisions found for selected filters"
                else -> null
            }
        ) {
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.divisionOptions.forEach { division ->
                    FilterChip(
                        selected = state.selectedDivision == division,
                        onClick = {
                            val newDivision = if (state.selectedDivision == division) null else division
                            onAction(SearchClassAction.SelectDivision(newDivision))
                        },
                        label = { Text(division.divisionCode) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }
        }

        // Batch Filter
        FilterSection(
            title = "Batch",
            isLoading = state.areBatchesLoading,
            emptyMessage = when {
                state.selectedSemesters.isEmpty() ||
                        state.selectedBranches.isEmpty() ||
                        state.selectedAcademicYearOfSemester == null ->
                    "Select semester, branch, and academic year to load batches"
                state.batchOptions.isEmpty() ->
                    "No batches found for selected filters"
                else -> null
            }
        ) {
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.batchOptions.forEach { batch ->
                    FilterChip(
                        selected = state.selectedBatch == batch,
                        onClick = {
                            val newBatch = if (state.selectedBatch == batch) null else batch
                            onAction(SearchClassAction.SelectBatch(newBatch))
                        },
                        label = { Text(batch.batchCode) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }
        }

        // Class Type Filter
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Class Type",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.classTypeOptions.forEach { classType ->
                    FilterChip(
                        selected = state.selectedClassTypes.contains(classType),
                        onClick = { onAction(SearchClassAction.ToggleClassType(classType)) },
                        label = { Text(text = classType.toDisplayLabel()) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }
        }

        // Extra Class Filter
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Only show Extra Classes",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
                Switch(
                    checked = state.isExtraClass == true,
                    onCheckedChange = {
                        onAction(SearchClassAction.UpdateIsExtraClass(if (it) true else null))
                    }
                )
            }
        }

        // Time Range Filter
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Time Range",
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
                    value = state.startTime?.toReadableString() ?: "",
                    onValueChange = { },
                    label = "Start Time",
                    readOnly = true,
                    supportingText = state.startTimeError,
                    isError = state.startTimeError != null,
                    leadingIcon = {
                        IconButton(onClick = { showStartTimePicker = true }) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = "Select start time"
                            )
                        }
                    },
                    trailingIcon = if (state.startTime != null) {
                        {
                            IconButton(onClick = { onAction(SearchClassAction.UpdateStartTime(null)) }) {
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
                    value = state.endTime?.toReadableString() ?: "",
                    onValueChange = { },
                    label = "End Time",
                    readOnly = true,
                    supportingText = state.endTimeError,
                    isError = state.endTimeError != null,
                    leadingIcon = {
                        IconButton(onClick = { showEndTimePicker = true }) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = "Select end time"
                            )
                        }
                    },
                    trailingIcon = if (state.endTime != null) {
                        {
                            IconButton(onClick = { onAction(SearchClassAction.UpdateEndTime(null)) }) {
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
        }

        // Date Range Filter (Active From/Till)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Active Date Range",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Active From Date Picker
                PresencifyTextField(
                    value = state.activeFrom?.toReadableString() ?: "",
                    onValueChange = { },
                    label = "Active From",
                    readOnly = true,
                    supportingText = state.activeFromError,
                    isError = state.activeFromError != null,
                    leadingIcon = {
                        IconButton(onClick = { showActiveFromPicker = true }) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Select active from date"
                            )
                        }
                    },
                    trailingIcon = if (state.activeFrom != null) {
                        {
                            IconButton(onClick = { onAction(SearchClassAction.UpdateActiveFrom(null)) }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear active from date"
                                )
                            }
                        }
                    } else null,
                    modifier = Modifier.weight(1f)
                )

                // Active Till Date Picker
                PresencifyTextField(
                    value = state.activeTill?.toReadableString() ?: "",
                    onValueChange = { },
                    label = "Active Till",
                    readOnly = true,
                    supportingText = state.activeTillError,
                    isError = state.activeTillError != null,
                    leadingIcon = {
                        IconButton(onClick = { showActiveTillPicker = true }) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Select active till date"
                            )
                        }
                    },
                    trailingIcon = if (state.activeTill != null) {
                        {
                            IconButton(onClick = { onAction(SearchClassAction.UpdateActiveTill(null)) }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear active till date"
                                )
                            }
                        }
                    } else null,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Rooms Filter
        FilterSection(
            title = "Rooms",
            isLoading = state.isLoadingRooms,
            emptyMessage = if (state.availableRooms.isEmpty() && !state.isLoadingRooms) "No rooms available" else null
        ) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.availableRooms.forEach { room ->
                    FilterChip(
                        selected = state.selectedRoomIds.contains(room.id),
                        onClick = { onAction(SearchClassAction.ToggleRoom(room.id)) },
                        label = { Text(text = room.toDisplayLabel()) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }
        }

        // Teachers Filter
        FilterSection(
            title = "Teachers",
            isLoading = state.isLoadingTeachers,
            emptyMessage = if (state.availableTeachers.isEmpty() && !state.isLoadingTeachers) "No teachers available" else null
        ) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.availableTeachers.forEach { teacher ->
                    FilterChip(
                        selected = state.selectedTeacherIds.contains(teacher.id),
                        onClick = { onAction(SearchClassAction.ToggleTeacher(teacher.id)) },
                        label = { Text(text = "${teacher.firstName} ${teacher.lastName}") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }
        }

        // Action Buttons
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            PresencifyTextButton(
                onClick = {
                    onAction(SearchClassAction.ResetFilters)
                },
                text = "Reset",
                modifier = Modifier.weight(1f)
            )

            PresencifyButton(
                onClick = {
                    onAction(SearchClassAction.ApplyFilters)
                    onDismiss()
                },
                text = "Apply",
                modifier = Modifier.weight(1f)
            )
        }
    }

    // Time Pickers
    if (showStartTimePicker) {
        TimePickerDialog(
            onDismissRequest = { showStartTimePicker = false },
            onConfirm = {
                val time = LocalTime(startTimePickerState.hour, startTimePickerState.minute)
                onAction(SearchClassAction.UpdateStartTime(time))
                showStartTimePicker = false
            }
        ) {
            TimePicker(state = startTimePickerState)
        }
    }

    if (showEndTimePicker) {
        TimePickerDialog(
            onDismissRequest = { showEndTimePicker = false },
            onConfirm = {
                val time = LocalTime(endTimePickerState.hour, endTimePickerState.minute)
                onAction(SearchClassAction.UpdateEndTime(time))
                showEndTimePicker = false
            }
        ) {
            TimePicker(state = endTimePickerState)
        }
    }

    // Date Pickers
    if (showActiveFromPicker) {
        DatePickerDialog(
            onDismissRequest = { showActiveFromPicker = false },
            onConfirm = {
                activeFromDatePickerState.selectedDateMillis?.let { millis ->
                    val epochDays = millis / (24 * 60 * 60 * 1000)
                    val selectedDate = LocalDate.fromEpochDays(epochDays.toInt())
                    onAction(SearchClassAction.UpdateActiveFrom(selectedDate))
                }
                showActiveFromPicker = false
            }
        ) {
            DatePicker(state = activeFromDatePickerState)
        }
    }

    if (showActiveTillPicker) {
        DatePickerDialog(
            onDismissRequest = { showActiveTillPicker = false },
            onConfirm = {
                activeTillDatePickerState.selectedDateMillis?.let { millis ->
                    val epochDays = millis / (24 * 60 * 60 * 1000)
                    val selectedDate = LocalDate.fromEpochDays(epochDays.toInt())
                    onAction(SearchClassAction.UpdateActiveTill(selectedDate))
                }
                showActiveTillPicker = false
            }
        ) {
            DatePicker(state = activeTillDatePickerState)
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

@Composable
private fun DatePickerDialog(
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
            content()
        }
    )
}

@Composable
private fun FilterSection(
    title: String,
    isLoading: Boolean = false,
    emptyMessage: String? = null,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold
        )

        when {
            isLoading -> {
                CircularProgressIndicator(
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(20.dp)
                        .padding(top = 4.dp)
                )
            }
            emptyMessage != null -> {
                Text(
                    text = emptyMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            else -> {
                content()
            }
        }
    }
}
