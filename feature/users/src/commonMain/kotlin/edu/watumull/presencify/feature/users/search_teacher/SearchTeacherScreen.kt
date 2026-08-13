package edu.watumull.presencify.feature.users.search_teacher

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import edu.watumull.presencify.core.designsystem.components.PresencifyBottomSheetScaffold
import edu.watumull.presencify.core.designsystem.components.PresencifyButton
import edu.watumull.presencify.core.designsystem.components.PresencifyDefaultLoadingScreen
import edu.watumull.presencify.core.designsystem.components.PresencifyNoResultsIndicator
import edu.watumull.presencify.core.designsystem.components.PresencifySearchBar
import edu.watumull.presencify.core.designsystem.theme.DesignToken
import edu.watumull.presencify.core.domain.model.auth.UserRole
import edu.watumull.presencify.core.presentation.UiConstants
import edu.watumull.presencify.core.presentation.components.TeacherListItem
import edu.watumull.presencify.core.presentation.composition_locals.LocalUserRole
import kotlinx.coroutines.flow.distinctUntilChanged

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTeacherScreen(
    state: SearchTeacherState,
    onAction: (SearchTeacherAction) -> Unit,
) {
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            skipHiddenState = false,
            initialValue = SheetValue.Hidden
        )
    )
    PresencifyBottomSheetScaffold(
        backPress = { onAction(SearchTeacherAction.NavigateBack) },
        topBarTitle = "Search Teachers",
        scaffoldState = scaffoldState,
        sheetContent = {
        },
        floatingActionButton = {
            if (LocalUserRole.current == UserRole.ADMIN) {
                FloatingActionButton(
                    onClick = { onAction(SearchTeacherAction.ClickFloatingActionButton) },
                    modifier = Modifier.padding(DesignToken.spacing.lg)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add teacher"
                    )
                }
            }
        }
    ) { paddingValues ->
        when (state.viewState) {
            is SearchTeacherState.ViewState.Loading -> {
                PresencifyDefaultLoadingScreen()
            }

            is SearchTeacherState.ViewState.Error -> {
                PresencifyNoResultsIndicator(
                    text = state.viewState.message.asString()
                )
            }

            is SearchTeacherState.ViewState.Content -> {
                SearchTeacherScreenContent(
                    state = state,
                    onAction = onAction,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun SearchTeacherScreenContent(
    state: SearchTeacherState,
    onAction: (SearchTeacherAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pullRefreshState = rememberPullRefreshState(
        refreshing = state.isRefreshing,
        onRefresh = { onAction(SearchTeacherAction.Refresh) }
    )
    val lazyListState = rememberLazyListState()

    LaunchedEffect(state.teachers) {
        snapshotFlow {
            lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
        }.distinctUntilChanged().collect { lastVisibleIndex ->
            if (lastVisibleIndex != null && lastVisibleIndex != 0 && lastVisibleIndex >= state.teachers.lastIndex - 10) {
                onAction(SearchTeacherAction.LoadMoreTeachers)
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
                onQueryChange = { onAction(SearchTeacherAction.UpdateSearchQuery(it)) },
                placeholder = "Search teachers...",
                onSearchClick = { onAction(SearchTeacherAction.Search) },
                showFilterIcon = false
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
                        items = state.teachers,
                        key = { it.id }
                    ) { teacher ->
                        val teacherName =
                            "${teacher.firstName} ${teacher.middleName?.let { teacher.middleName + " " } ?: ""}${teacher.lastName}"
                        TeacherListItem(
                            teacherName = teacherName,
                            role = teacher.role,
                            teacherImageUrl = teacher.teacherImageUrl,
                            isActive = teacher.isActive,
                            trailingContent = if (state.isSelectable) {
                                {
                                    Icon(
                                        imageVector = if (state.selectedTeacherIds.contains(teacher.id)) {
                                            Icons.Filled.CheckCircle
                                        } else {
                                            Icons.Outlined.Circle
                                        },
                                        contentDescription = if (state.selectedTeacherIds.contains(teacher.id)) {
                                            "Selected"
                                        } else {
                                            "Not selected"
                                        },
                                        tint = if (state.selectedTeacherIds.contains(teacher.id)) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                        },
                                        modifier = Modifier.size(DesignToken.icons.md)
                                    )
                                }
                            } else null,
                            onClick = { onAction(SearchTeacherAction.TeacherCardClick(teacher.id)) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        when {
                            state.isLoadingMore -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(DesignToken.spacing.lg),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(DesignToken.components.progressMd),
                                        strokeWidth = DesignToken.strokes.md
                                    )
                                }
                            }

                            state.teachers.isEmpty() && state.isLoadingTeachers -> {
                                PresencifyDefaultLoadingScreen()
                            }

                            state.teachers.isEmpty() && !state.isLoadingTeachers -> {
                                PresencifyNoResultsIndicator(
                                    text = "No teachers found"
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

        if (state.isSelectable) {
            PresencifyButton(
                onClick = { onAction(SearchTeacherAction.DoneButtonClick) },
                text = "Done (${state.selectedTeacherIds.size} selected)",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = DesignToken.spacing.lg)
            )
        }
    }
}

