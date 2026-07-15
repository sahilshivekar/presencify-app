package edu.watumull.presencify.feature.users.search_student


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
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
import edu.watumull.presencify.core.designsystem.components.PresencifyBottomSheetScaffold
import edu.watumull.presencify.core.designsystem.components.PresencifyDefaultLoadingScreen
import edu.watumull.presencify.core.designsystem.components.PresencifyNoResultsIndicator
import edu.watumull.presencify.core.designsystem.components.PresencifySearchBar
import edu.watumull.presencify.core.designsystem.components.PresencifyTextButton
import edu.watumull.presencify.core.designsystem.theme.DesignToken
import edu.watumull.presencify.core.domain.model.auth.UserRole
import edu.watumull.presencify.core.presentation.UiConstants
import edu.watumull.presencify.core.presentation.components.StudentListItem
import edu.watumull.presencify.core.presentation.composition_locals.LocalUserRole
import edu.watumull.presencify.feature.users.navigation.SearchStudentIntention
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

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
        SearchStudentIntention.MARK_UNMARK_STUDENT_AS_DROPOUT -> "Mark/Unmark Student as Dropout"
        SearchStudentIntention.VIEW_ATTENDANCE -> "Select Student"
        SearchStudentIntention.ADD_STUDENT_BIOMETRIC -> "Select Student"
    }

    PresencifyBottomSheetScaffold(
        backPress = { onAction(SearchStudentAction.NavigateBack) },
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
            if (LocalUserRole.current == UserRole.ADMIN) {
                FloatingActionButton(
                    onClick = { onAction(SearchStudentAction.ClickFloatingActionButton) },
                    modifier = Modifier.padding(DesignToken.spacing.lg)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add student"
                    )
                }
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

    LaunchedEffect(state.students) {
        snapshotFlow {
            lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
        }.distinctUntilChanged().collect { lastVisibleIndex ->
            // If lastVisibleIndex == 0 then it means the list is empty and the loading indicator is an inside item{} taking index 0
            // initial load should only be trigger within init block of the view model, so that we can apply pre-filtering before loading students for the first time
            if (lastVisibleIndex != null && lastVisibleIndex != 0 && lastVisibleIndex >= state.students.lastIndex - 10) {
                onAction(SearchStudentAction.LoadMoreStudents)
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
                onQueryChange = { onAction(SearchStudentAction.UpdateSearchQuery(it)) },
                onFilterClick = onFilterClick,
                placeholder = "Search students...",
                onSearchClick = { onAction(SearchStudentAction.Search) }
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
                    verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.sm)
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
                                                                strokeWidth = DesignToken.strokes.md
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
                                                                strokeWidth = DesignToken.strokes.md
                                                            )
                                                        }
                                                    } else null
                                                )
                                            }
                                        }
                                    }

                                    SearchStudentIntention.ADD_STUDENT_BIOMETRIC -> {
                                        {
                                            edu.watumull.presencify.core.designsystem.components.PresencifyOutlinedButton(
                                                text = "Select",
                                                onClick = {
                                                    onAction(SearchStudentAction.StudentActionButtonClick(student.id))
                                                }
                                            )
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
                                                                strokeWidth = DesignToken.strokes.md
                                                            )
                                                        }
                                                    } else null
                                                )
                                            }
                                        }
                                    }

                                    SearchStudentIntention.ASSIGN_UNASSIGN_STUDENT_TO_BATCH -> {
                                        val isAssigned =
                                            student.studentBatches?.any { it.batch?.id == state.batchId && it.endDate == null } == true
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
                                                                strokeWidth = DesignToken.strokes.md
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
                                                                strokeWidth = DesignToken.strokes.md
                                                            )
                                                        }
                                                    } else null
                                                )
                                            }
                                        }
                                    }

                                    SearchStudentIntention.MARK_UNMARK_STUDENT_AS_DROPOUT -> {
                                        val isDropout = state.studentDropoutStatus[student.id] ?: false
                                        {
                                            Column(
                                                verticalArrangement = Arrangement.Top,
                                                horizontalAlignment = Alignment.End,
                                            ) {
                                                Text(
                                                    text = if (isDropout) "Dropout" else "Active",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = if (isDropout)
                                                        MaterialTheme.colorScheme.error
                                                    else
                                                        MaterialTheme.colorScheme.primary
                                                )
                                                Switch(
                                                    checked = isDropout,
                                                    onCheckedChange = {
                                                        onAction(
                                                            SearchStudentAction.ToggleStudentDropout(
                                                                student.id,
                                                                isDropout
                                                            )
                                                        )
                                                    },
                                                    enabled = !isLoading,
                                                    thumbContent = if (isLoading) {
                                                        {
                                                            CircularProgressIndicator(
                                                                modifier = Modifier.size(SwitchDefaults.IconSize),
                                                                strokeWidth = DesignToken.strokes.md
                                                            )
                                                        }
                                                    } else null
                                                )
                                            }
                                        }
                                    }

                                    SearchStudentIntention.VIEW_ATTENDANCE -> {
                                        {
                                            PresencifyTextButton(
                                                onClick = {
                                                    onAction(SearchStudentAction.StudentActionButtonClick(student.id))
                                                }
                                            ) {
                                                Row (
                                                    verticalAlignment = Alignment.CenterVertically,
                                                ) {
                                                    Text("View", color = MaterialTheme.colorScheme.primary)
                                                    Icon(
                                                        imageVector = Icons.Default.ChevronRight,
                                                        contentDescription = null,
                                                        tint = MaterialTheme.colorScheme.primary
                                                    )
                                                }
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
