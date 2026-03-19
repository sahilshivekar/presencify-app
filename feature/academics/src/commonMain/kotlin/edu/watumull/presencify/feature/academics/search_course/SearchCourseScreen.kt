package edu.watumull.presencify.feature.academics.search_course

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
import edu.watumull.presencify.core.design.systems.components.PresencifyBottomSheetScaffold
import edu.watumull.presencify.core.design.systems.components.PresencifyDefaultLoadingScreen
import edu.watumull.presencify.core.design.systems.components.PresencifyNoResultsIndicator
import edu.watumull.presencify.core.design.systems.components.PresencifySearchBar
import edu.watumull.presencify.core.design.systems.components.dialog.PresencifyAlertDialog
import edu.watumull.presencify.core.domain.model.auth.UserRole
import edu.watumull.presencify.core.presentation.UiConstants
import edu.watumull.presencify.core.presentation.components.CourseListItem
import edu.watumull.presencify.core.presentation.composition_locals.LocalUserRole
import edu.watumull.presencify.feature.academics.navigation.SearchCourseIntention
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchCourseScreen(
    state: SearchCourseState,
    onAction: (SearchCourseAction) -> Unit,
) {
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            skipHiddenState = false,
            initialValue = SheetValue.Hidden
        )
    )

    val scope = rememberCoroutineScope()

    val topBarTitle = when (state.intention) {
        SearchCourseIntention.DEFAULT -> "Search Courses"
        SearchCourseIntention.LINK_UNLINK_COURSE_TO_SEMESTER_NUMBER_BRANCH -> "Link/Unlink Courses"
        SearchCourseIntention.ASSIGN_UNASSIGN_COURSE_TO_TEACHER -> "Assign/Unassign Courses"
    }

    PresencifyBottomSheetScaffold(
        backPress = { onAction(SearchCourseAction.BackButtonClick) },
        topBarTitle = topBarTitle,
        scaffoldState = scaffoldState,
        sheetContent = {
            SearchCourseBottomSheetContent(
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
                    onClick = { onAction(SearchCourseAction.ClickFloatingActionButton) },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add course"
                    )
                }
            }
        }
    ) { paddingValues ->
        when (state.viewState) {
            is SearchCourseState.ViewState.Loading -> {
                PresencifyDefaultLoadingScreen()
            }

            is SearchCourseState.ViewState.Error -> {
                PresencifyNoResultsIndicator(
                    text = state.viewState.message.asString()
                )
            }

            is SearchCourseState.ViewState.Content -> {
                SearchCourseScreenContent(
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
                onAction(SearchCourseAction.DismissDialog)
            }
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun SearchCourseScreenContent(
    state: SearchCourseState,
    onAction: (SearchCourseAction) -> Unit,
    onFilterClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val pullRefreshState = rememberPullRefreshState(
        refreshing = state.isRefreshing,
        onRefresh = { onAction(SearchCourseAction.Refresh) }
    )
    val lazyListState = rememberLazyListState()

    LaunchedEffect(state.courses) {
        snapshotFlow {
            lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
        }.distinctUntilChanged().collect { lastVisibleIndex ->
            // If lastVisibleIndex == 0 then it means the list is empty and the loading indicator is an inside item{} taking index 0
            // initial load should only be trigger within init block of the view model, so that we can apply pre-filtering before loading students for the first time
            if (lastVisibleIndex != null && lastVisibleIndex != 0 && lastVisibleIndex >= state.courses.lastIndex - 10) {
                onAction(SearchCourseAction.LoadMoreCourses)
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
                onQueryChange = { onAction(SearchCourseAction.UpdateSearchQuery(it)) },
                onFilterClick = onFilterClick,
                placeholder = "Search courses...",
                onSearchClick = { onAction(SearchCourseAction.Search) }
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
                        items = state.courses,
                        key = { it.id }
                    ) { course ->
                        val schemeName = course.scheme?.name ?: "N/A"

                        CourseListItem(
                            name = course.name,
                            code = course.code,
                            schemeName = schemeName,
                            optionalCourse = course.optionalCourse,
                            feedback = state.courseFeedback[course.id],
                            trailingContent = if (state.intention != SearchCourseIntention.DEFAULT) {
                                val isLoading = state.loadingCourseIds.contains(course.id)

                                when (state.intention) {
                                    SearchCourseIntention.LINK_UNLINK_COURSE_TO_SEMESTER_NUMBER_BRANCH -> {
                                        // Check if course is linked to the branch+semester combination
                                        val isLinked = state.branchId?.let { bId ->
                                            state.semesterNumber?.let { semNum ->
                                                val semesterEnum =
                                                    edu.watumull.presencify.core.domain.enums.SemesterNumber.fromValue(
                                                        semNum
                                                    )
                                                course.branchCourseSemesters?.any {
                                                    it.branchId == bId && it.semesterNumber == semesterEnum
                                                } == true
                                            }
                                        } ?: false
                                        {
                                            Column(
                                                verticalArrangement = Arrangement.Top,
                                                horizontalAlignment = Alignment.End,
                                            ) {
                                                Text(
                                                    text = if (isLinked) "Linked" else "Not Linked",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = if (isLinked)
                                                        MaterialTheme.colorScheme.primary
                                                    else
                                                        MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                                Switch(
                                                    checked = isLinked,
                                                    onCheckedChange = {
                                                        onAction(SearchCourseAction.CourseActionButtonClick(course.id))
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

                                    SearchCourseIntention.ASSIGN_UNASSIGN_COURSE_TO_TEACHER -> {
                                        // Check if course is assigned to the teacher
                                        val isAssigned = state.teacherId?.let { tId ->
                                            course.teacherTeachesCourses?.any { it.teacherId == tId } == true
                                        } ?: false
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
                                                        onAction(SearchCourseAction.CourseActionButtonClick(course.id))
                                                    },
                                                    enabled = !isLoading,
                                                    thumbContent = if (isLoading) {
                                                        {
                                                            CircularProgressIndicator(
                                                                modifier = Modifier.size(
                                                                    SwitchDefaults.IconSize
                                                                ),
                                                                strokeWidth = 1.dp,
                                                                color = MaterialTheme.colorScheme.primary
                                                            )
                                                        }
                                                    } else null,
                                                    colors = SwitchDefaults.colors(
                                                        checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                                                        checkedTrackColor = MaterialTheme.colorScheme.primary,
                                                        uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                                                        uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                                                        uncheckedBorderColor = MaterialTheme.colorScheme.outline
                                                    )
                                                )
                                            }
                                        }
                                    }

                                    SearchCourseIntention.DEFAULT -> null
                                }
                            } else null,
                            onClick = { onAction(SearchCourseAction.CourseCardClick(course.id)) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        when {
                            state.isLoadingMore || (state.courses.isEmpty() && state.isLoadingCourses) -> {
                                PresencifyDefaultLoadingScreen()
                            }

                            state.courses.isEmpty() && !state.isLoadingCourses -> {
                                PresencifyNoResultsIndicator(
                                    text = "No courses found"
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
