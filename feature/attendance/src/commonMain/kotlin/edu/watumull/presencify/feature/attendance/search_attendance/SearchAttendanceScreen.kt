package edu.watumull.presencify.feature.attendance.search_attendance

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberStandardBottomSheetState
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
import androidx.compose.ui.unit.dp
import edu.watumull.presencify.core.design.systems.components.PresencifyBottomSheetScaffold
import edu.watumull.presencify.core.design.systems.components.PresencifyDefaultLoadingScreen
import edu.watumull.presencify.core.design.systems.components.PresencifyNoResultsIndicator
import edu.watumull.presencify.core.design.systems.components.PresencifySearchBar
import edu.watumull.presencify.core.design.systems.components.dialog.PresencifyAlertDialog
import edu.watumull.presencify.core.presentation.UiConstants
import edu.watumull.presencify.core.presentation.components.AttendanceListItem
import edu.watumull.presencify.core.presentation.components.CourseListItem
import edu.watumull.presencify.core.presentation.utils.toReadableString
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchAttendanceScreen(
    state: SearchAttendanceState,
    onAction: (SearchAttendanceAction) -> Unit,
) {
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            skipHiddenState = false,
            initialValue = SheetValue.Hidden
        )
    )

    val scope = rememberCoroutineScope()
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    PresencifyBottomSheetScaffold(
        backPress = { onAction(SearchAttendanceAction.BackButtonClick) },
        topBarTitle = if (state.routeCourseId != null) "Course Attendance" else "Search Attendance",
        scaffoldState = scaffoldState,
        sheetContent = {
            SearchAttendanceBottomSheetContent(
                state = state,
                onAction = onAction,
                onDateClick = { showDatePicker = true },
                onDismiss = {
                    scope.launch { scaffoldState.bottomSheetState.hide() }
                },
            )
        }
    ) { paddingValues ->
        when (state.viewState) {
            is SearchAttendanceState.ViewState.Loading -> {
                PresencifyDefaultLoadingScreen()
            }

            is SearchAttendanceState.ViewState.Error -> {
                PresencifyNoResultsIndicator(
                    text = state.viewState.message.asString()
                )
            }

            is SearchAttendanceState.ViewState.Content -> {
                SearchAttendanceScreenContent(
                    state = state,
                    onAction = onAction,
                    onFilterClick = { scope.launch { scaffoldState.bottomSheetState.expand() } },
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }

    // Date Picker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            onConfirm = {
                datePickerState.selectedDateMillis?.let { millis ->
                    val epochDays = millis / (24 * 60 * 60 * 1000)
                    val selectedDate = LocalDate.fromEpochDays(epochDays.toInt())
                    onAction(SearchAttendanceAction.SelectDate(selectedDate))
                }
                showDatePicker = false
            }
        ) {
            DatePicker(state = datePickerState)
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
                    SearchAttendanceState.DialogIntention.GENERIC -> {
                        // Handle generic dialog confirmation
                    }
                }
            },
            onDismiss = {
                onAction(SearchAttendanceAction.DismissDialog)
            }
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun SearchAttendanceScreenContent(
    state: SearchAttendanceState,
    onAction: (SearchAttendanceAction) -> Unit,
    onFilterClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val pullRefreshState = rememberPullRefreshState(
        refreshing = state.isRefreshing,
        onRefresh = { onAction(SearchAttendanceAction.Refresh) }
    )
    val lazyListState = rememberLazyListState()

    LaunchedEffect(state.attendances) {
        snapshotFlow {
            lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
        }.distinctUntilChanged().collect { lastVisibleIndex ->
            if (lastVisibleIndex != null && lastVisibleIndex != 0 && lastVisibleIndex >= state.attendances.lastIndex - 10) {
                onAction(SearchAttendanceAction.LoadMoreAttendances)
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
            if (state.routeCourseId != null && state.isRouteCourseLoading) {

            } else if (state.routeCourseId != null) {
                CourseListItem(
                    name = state.selectedCourse?.name ?: "",
                    code = state.selectedCourse?.code ?: "",
                    schemeName = state.selectedCourse?.scheme?.name ?: "",
                    optionalCourse = state.selectedCourse?.optionalCourse,
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                )
            } else {
                PresencifySearchBar(
                    query = state.searchQuery,
                    onQueryChange = { onAction(SearchAttendanceAction.UpdateSearchQuery(it)) },
                    onFilterClick = onFilterClick,
                    placeholder = "Search attendances...",
                    onSearchClick = { onAction(SearchAttendanceAction.Search) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .pullRefresh(pullRefreshState)
            ) {
                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    if (!state.isLoadingAttendances && state.attendances.isEmpty()) {
                        item {
                            PresencifyNoResultsIndicator(
                                text = "No attendances found"
                            )
                        }
                    }

                    items(
                        items = state.attendances,
                        key = { it.id }
                    ) { attendance ->
                        val classSession = attendance.klass
                        if (classSession != null) {
                            val isPresent = attendance.attendanceStudents?.find {
                                it.studentId == state.studentId
                            }?.attendanceStatus

                            var totalCount: Int? = null
                            var presentCount: Int? = null

                            if (state.studentId == null) {
                                totalCount = attendance.attendanceStudents?.size
                                presentCount = attendance.attendanceStudents?.count { it.attendanceStatus }
                            }

                            AttendanceListItem(
                                attendanceDate = attendance.date.toReadableString(),
                                courseName = if (state.routeCourseId == null) classSession.course?.name else null,
                                teacherName = classSession.teacher?.let { "${it.firstName} ${it.lastName}" }
                                    ?: "Unknown Teacher",
                                startTime = classSession.startTime.toReadableString(),
                                endTime = classSession.endTime.toReadableString(),
                                dayOfWeek = classSession.dayOfWeek.toDisplayLabel(),
                                onClick = { onAction(SearchAttendanceAction.AttendanceCardClick(attendance.id)) },
                                modifier = Modifier.fillMaxWidth(),
                                isPresent = isPresent,
                                totalCount = totalCount,
                                presentCount = presentCount,
                            )
                        }
                    }

                    if (state.isLoadingMore) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }

                PullRefreshIndicator(
                    refreshing = state.isRefreshing,
                    state = pullRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        }
    }
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
