package edu.watumull.presencify.feature.users.search_student

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import edu.watumull.presencify.core.design.systems.components.PresencifyBottomSheetScaffold
import edu.watumull.presencify.core.design.systems.components.PresencifyDefaultLoadingScreen
import edu.watumull.presencify.core.design.systems.components.PresencifyNoResultsIndicator
import edu.watumull.presencify.core.design.systems.components.PresencifySearchBar
import edu.watumull.presencify.core.design.systems.components.dialog.PresencifyAlertDialog
import edu.watumull.presencify.core.presentation.UiConstants
import edu.watumull.presencify.core.presentation.components.StudentListItem
import edu.watumull.presencify.feature.users.navigation.SearchStudentIntention
import kotlinx.coroutines.flow.distinctUntilChanged

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchStudentScreen(
    state: SearchStudentState,
    onAction: (SearchStudentAction) -> Unit,
) {
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            skipHiddenState = false,
            initialValue = SheetValue.Hidden
        )
    )

    val scope = rememberCoroutineScope()

    val topBarTitle = when (state.intention) {
        SearchStudentIntention.DEFAULT -> "Search Students"
        SearchStudentIntention.ASSIGN_UNASSIGN_STUDENT_TO_SEMESTER -> "Assign/Unassign Students to Semester"
        SearchStudentIntention.ASSIGN_UNASSIGN_STUDENT_TO_DIVISION -> "Assign/Unassign Students to Division"
        SearchStudentIntention.MODIFY_STUDENT_DIVISION -> "Modify Student Division"
        SearchStudentIntention.ASSIGN_UNASSIGN_STUDENT_TO_BATCH -> "Assign/Unassign Students to Batch"
        SearchStudentIntention.MODIFY_STUDENT_BATCH -> "Modify Student Batch"
    }

    PresencifyBottomSheetScaffold(
        backPress = { onAction(SearchStudentAction.BackButtonClick) },
        topBarTitle = topBarTitle,
        scaffoldState = scaffoldState,
        sheetContent = {
            SearchStudentBottomSheetContent(
                state = state,
                onAction = onAction,
                onDismiss = {
                    scope.launch { scaffoldState.bottomSheetState.hide() }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onAction(SearchStudentAction.ClickFloatingActionButton) },
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add student"
                )
            }
        }
    ) { paddingValues ->
        when (state.viewState) {
            is SearchStudentState.ViewState.Loading -> {
                PresencifyDefaultLoadingScreen()
            }

            is SearchStudentState.ViewState.Error -> {
                PresencifyNoResultsIndicator(
                    text = state.viewState.message.asString()
                )
            }

            is SearchStudentState.ViewState.Content -> {
                SearchStudentScreenContent(
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
                onAction(SearchStudentAction.DismissDialog)
            }
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun SearchStudentScreenContent(
    state: SearchStudentState,
    onAction: (SearchStudentAction) -> Unit,
    onFilterClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val pullRefreshState = rememberPullRefreshState(
        refreshing = state.isRefreshing,
        onRefresh = { onAction(SearchStudentAction.Refresh) }
    )
    val lazyListState = rememberLazyListState()

    LaunchedEffect(Unit) {
        snapshotFlow {
            lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
        }.distinctUntilChanged().collect { lastVisibleIndex ->
            // Trigger load more when we're close to the end (within 3 items)
            // Skip if lastVisibleIndex is 0 (initial loading state) to prevent
            // double loading (the viewmodel will call it after filtering is set up)
            if (lastVisibleIndex != null && lastVisibleIndex != 0 && lastVisibleIndex >= state.students.lastIndex - 3) {
                onAction(SearchStudentAction.LoadMoreStudents)
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
                onQueryChange = { onAction(SearchStudentAction.UpdateSearchQuery(it)) },
                onFilterClick = onFilterClick,
                placeholder = "Search students...",
                onSearchClick = { onAction(SearchStudentAction.Search) }
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
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = state.students,
                        key = { it.id }
                    ) { student ->
                        val studentYear =
                            student.studentSemesters?.firstOrNull()?.semester?.semesterNumber?.toAcademicYear()
                        val studentName =
                            "${student.firstName} ${student.middleName?.let { student.middleName + " " } ?: ""}${student.lastName}"
                        val studentBranch = student.branch?.abbreviation ?: "N/A"
                        StudentListItem(
                            studentName = studentName,
                            studentBranch = studentBranch,
                            studentYear = studentYear,
                            studentImageUrl = student.studentImageUrl,
                            feedback = state.studentFeedback[student.id],
                            trailingContent = if (state.intention != SearchStudentIntention.DEFAULT) {
                                val isLoading = state.loadingStudentIds.contains(student.id)

                                when (state.intention) {
                                    SearchStudentIntention.ASSIGN_UNASSIGN_STUDENT_TO_SEMESTER -> {
                                        val isAssigned =
                                            student.studentSemesters?.any { it.semester?.id == state.semesterId } == true
                                        {
                                            Column(
                                                verticalArrangement = Arrangement.Top,
                                                horizontalAlignment = Alignment.End,
                                            ) {
                                                Text(
                                                    text = if (isAssigned) "Assigned" else "Not Assigned",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = if (isAssigned)
                                                        MaterialTheme.colorScheme.primary
                                                    else
                                                        MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                                Switch(
                                                    checked = isAssigned,
                                                    onCheckedChange = {
                                                        onAction(SearchStudentAction.StudentActionButtonClick(student.id))
                                                    },
                                                    enabled = !isLoading,
                                                    thumbContent = if (isLoading) {
                                                        {
                                                            CircularProgressIndicator(
                                                                modifier = Modifier.size(SwitchDefaults.IconSize),
                                                                strokeWidth = 2.dp
                                                            )
                                                        }
                                                    } else null
                                                )
                                            }
                                        }
                                    }

                                    SearchStudentIntention.ASSIGN_UNASSIGN_STUDENT_TO_DIVISION -> {
                                        val isAssigned =
                                            student.studentDivisions?.any { it.division?.id == state.divisionId } == true
                                        {
                                            Column(
                                                verticalArrangement = Arrangement.Top,
                                                horizontalAlignment = Alignment.End,
                                            ) {
                                                Text(
                                                    text = if (isAssigned) "Assigned" else "Not Assigned",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = if (isAssigned)
                                                        MaterialTheme.colorScheme.primary
                                                    else
                                                        MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                                Switch(
                                                    checked = isAssigned,
                                                    onCheckedChange = {
                                                        onAction(SearchStudentAction.StudentActionButtonClick(student.id))
                                                    },
                                                    enabled = !isLoading,
                                                    thumbContent = if (isLoading) {
                                                        {
                                                            CircularProgressIndicator(
                                                                modifier = Modifier.size(SwitchDefaults.IconSize),
                                                                strokeWidth = 2.dp
                                                            )
                                                        }
                                                    } else null
                                                )
                                            }
                                        }
                                    }

                                    SearchStudentIntention.MODIFY_STUDENT_DIVISION -> {
                                        // Check if student is in the target division for this semester (endDate = null)
                                        val isInTargetDivision = student.studentDivisions?.any {
                                            it.division?.id == state.divisionId && it.endDate == null
                                        } == true
                                        {
                                            Column(
                                                verticalArrangement = Arrangement.Top,
                                                horizontalAlignment = Alignment.End,
                                            ) {
                                                Text(
                                                    text = if (isInTargetDivision) "In division" else "Not in division",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = if (isInTargetDivision)
                                                        MaterialTheme.colorScheme.primary
                                                    else
                                                        MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                                Switch(
                                                    checked = isInTargetDivision,
                                                    onCheckedChange = {
                                                        onAction(SearchStudentAction.StudentActionButtonClick(student.id))
                                                    },
                                                    enabled = !isLoading,
                                                    thumbContent = if (isLoading) {
                                                        {
                                                            CircularProgressIndicator(
                                                                modifier = Modifier.size(SwitchDefaults.IconSize),
                                                                strokeWidth = 2.dp
                                                            )
                                                        }
                                                    } else null
                                                )
                                            }
                                        }
                                    }

                                    SearchStudentIntention.ASSIGN_UNASSIGN_STUDENT_TO_BATCH -> {
                                        val isAssigned =
                                            student.studentBatches?.any { it.batch?.id == state.batchId } == true
                                        {
                                            Column(
                                                verticalArrangement = Arrangement.Top,
                                                horizontalAlignment = Alignment.End,
                                            ) {
                                                Text(
                                                    text = if (isAssigned) "Assigned" else "Not Assigned",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = if (isAssigned)
                                                        MaterialTheme.colorScheme.primary
                                                    else
                                                        MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                                Switch(
                                                    checked = isAssigned,
                                                    onCheckedChange = {
                                                        onAction(SearchStudentAction.StudentActionButtonClick(student.id))
                                                    },
                                                    enabled = !isLoading,
                                                    thumbContent = if (isLoading) {
                                                        {
                                                            CircularProgressIndicator(
                                                                modifier = Modifier.size(SwitchDefaults.IconSize),
                                                                strokeWidth = 2.dp
                                                            )
                                                        }
                                                    } else null
                                                )
                                            }
                                        }
                                    }

                                    SearchStudentIntention.MODIFY_STUDENT_BATCH -> {
                                        // Check if student is in the target batch (endDate = null)
                                        val isInTargetBatch = student.studentBatches?.any {
                                            it.batch?.id == state.batchId && it.endDate == null
                                        } == true
                                        {
                                            Column(
                                                verticalArrangement = Arrangement.Top,
                                                horizontalAlignment = Alignment.End,
                                            ) {
                                                Text(
                                                    text = if (isInTargetBatch) "In batch" else "Not in batch",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = if (isInTargetBatch)
                                                        MaterialTheme.colorScheme.primary
                                                    else
                                                        MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                                Switch(
                                                    checked = isInTargetBatch,
                                                    onCheckedChange = {
                                                        onAction(SearchStudentAction.StudentActionButtonClick(student.id))
                                                    },
                                                    enabled = !isLoading,
                                                    thumbContent = if (isLoading) {
                                                        {
                                                            CircularProgressIndicator(
                                                                modifier = Modifier.size(SwitchDefaults.IconSize),
                                                                strokeWidth = 2.dp
                                                            )
                                                        }
                                                    } else null
                                                )
                                            }
                                        }
                                    }

                                    SearchStudentIntention.DEFAULT -> null
                                }
                            } else null,
                            onClick = { onAction(SearchStudentAction.StudentCardClick(student.id)) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        when {
                            state.isLoadingMore || (state.students.isEmpty() && state.isLoadingStudents) -> {
                                PresencifyDefaultLoadingScreen()
                            }


                            state.students.isEmpty() && !state.isLoadingStudents -> {
                                PresencifyNoResultsIndicator(
                                    text = "No students found"
                                )
                            }
                        }
                    }
                }
                if (state.isRefreshing) {
                    PullRefreshIndicator(
                        refreshing = true,
                        state = pullRefreshState,
                        modifier = Modifier.align(Alignment.TopCenter)
                    )
                }
            }


        }
    }
}
