package edu.watumull.presencify.feature.schedule.search_timetable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
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
import androidx.compose.material3.HorizontalDivider
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
import edu.watumull.presencify.core.designsystem.theme.DesignToken
import edu.watumull.presencify.core.domain.model.auth.UserRole
import edu.watumull.presencify.core.presentation.UiConstants
import edu.watumull.presencify.core.presentation.components.TimetableListItem
import edu.watumull.presencify.core.presentation.composition_locals.LocalUserRole
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTimetableScreen(
    state: SearchTimetableState,
    onAction: (SearchTimetableAction) -> Unit,
) {
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            skipHiddenState = false,
            initialValue = SheetValue.Hidden
        )
    )

    val scope = rememberCoroutineScope()

    PresencifyBottomSheetScaffold(
        backPress = { onAction(SearchTimetableAction.NavigateBack) },
        topBarTitle = "Search Timetables",
        scaffoldState = scaffoldState,
        sheetContent = {
            SearchTimetableBottomSheetContent(
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
                    onClick = { onAction(SearchTimetableAction.ClickFloatingActionButton) },
                    modifier = Modifier.padding(DesignToken.spacing.lg)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add timetable"
                    )
                }
            }
        }
    ) { paddingValues ->
        when (state.viewState) {
            is SearchTimetableState.ViewState.Loading -> {
                PresencifyDefaultLoadingScreen()
            }

            is SearchTimetableState.ViewState.Error -> {
                PresencifyNoResultsIndicator(
                    text = state.viewState.message.asString()
                )
            }

            is SearchTimetableState.ViewState.Content -> {
                SearchTimetableScreenContent(
                    state = state,
                    onAction = onAction,
                    onFilterClick = {
                        scope.launch { scaffoldState.bottomSheetState.expand() }
                    },
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun SearchTimetableScreenContent(
    state: SearchTimetableState,
    onAction: (SearchTimetableAction) -> Unit,
    onFilterClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val pullRefreshState = rememberPullRefreshState(
        refreshing = state.isRefreshing,
        onRefresh = { onAction(SearchTimetableAction.Refresh) }
    )
    val lazyListState = rememberLazyListState()

    LaunchedEffect(state.timetables) {
        snapshotFlow {
            lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
        }.distinctUntilChanged().collect { lastVisibleIndex ->
            if (lastVisibleIndex != null && lastVisibleIndex != 0 && lastVisibleIndex >= state.filteredTimetables.lastIndex - 10) {
                onAction(SearchTimetableAction.LoadMoreTimetables)
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
                onQueryChange = { onAction(SearchTimetableAction.UpdateSearchQuery(it)) },
                onFilterClick = onFilterClick,
                placeholder = "Search timetables...",
                onSearchClick = { onAction(SearchTimetableAction.Search) }
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
                        items = state.filteredTimetables,
                        key = { it.id }
                    ) { timetable ->
                        val division = timetable.division
                        val semester = division?.semester
                        val branch = semester?.branch

                        val year = semester?.semesterNumber?.let { semNum ->
                            when (semNum.value) {
                                1, 2 -> "FE"
                                3, 4 -> "SE"
                                5, 6 -> "TE"
                                7, 8 -> "BE"
                                else -> "Unknown"
                            }
                        } ?: "Unknown"

                        TimetableListItem(
                            branchAbbreviation = branch?.abbreviation ?: "Unknown Branch",
                            year = year,
                            semesterNumber = semester?.semesterNumber
                                ?: edu.watumull.presencify.core.domain.enums.SemesterNumber.SEMESTER_1,
                            semesterAcademicStartYear = semester?.academicStartYear ?: 0,
                            semesterAcademicEndYear = semester?.academicEndYear ?: 0,
                            divisionCode = division?.divisionCode ?: "Unknown Division",
                            onClick = { onAction(SearchTimetableAction.TimetableCardClick(timetable.id)) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        when {
                            state.isLoadingMore || (state.filteredTimetables.isEmpty() && state.isLoadingTimetables) -> {
                                PresencifyDefaultLoadingScreen()
                            }

                            state.filteredTimetables.isEmpty() && !state.isLoadingTimetables -> {
                                PresencifyNoResultsIndicator(
                                    text = "No timetables found"
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun SearchTimetableBottomSheetContent(
    state: SearchTimetableState,
    onAction: (SearchTimetableAction) -> Unit,
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Filter Timetables",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Reset",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable {
                    onAction(SearchTimetableAction.ResetFilters)
                }
            )
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = DesignToken.spacing.sm))

        FilterSection(
            title = "Branch",
            isLoading = state.areBranchesLoading
        ) {
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(DesignToken.spacing.sm)
            ) {
                state.branchOptions.forEach { branch ->
                    FilterChip(
                        selected = state.selectedBranches.contains(branch),
                        onClick = { onAction(SearchTimetableAction.ToggleBranch(branch)) },
                        label = { Text(branch.abbreviation) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }
        }

        FilterSection(title = "Semester") {
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(DesignToken.spacing.sm)
            ) {
                state.semesterOptions.forEach { semester ->
                    FilterChip(
                        selected = state.selectedSemesters.contains(semester),
                        onClick = { onAction(SearchTimetableAction.ToggleSemester(semester)) },
                        label = { Text("Sem ${semester.value}") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }
        }

        FilterSection(title = "Academic Year of Semester") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(DesignToken.spacing.sm)
            ) {
                PresencifyTextField(
                    value = state.academicStartYear,
                    onValueChange = { onAction(SearchTimetableAction.UpdateAcademicStartYear(it)) },
                    label = "Start Year *",
                    modifier = Modifier.weight(1f)
                )
                PresencifyTextField(
                    value = state.academicEndYear,
                    onValueChange = { onAction(SearchTimetableAction.UpdateAcademicEndYear(it)) },
                    label = "End Year *",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = DesignToken.spacing.sm))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(DesignToken.spacing.md)
        ) {
            PresencifyOutlinedButton(
                text = "Cancel",
                onClick = { onDismiss() },
                modifier = Modifier.weight(1f)
            )
            PresencifyButton(
                text = "Apply",
                onClick = {
                    onAction(SearchTimetableAction.ApplyFilters)
                    onDismiss()
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun FilterSection(
    title: String,
    isLoading: Boolean = false,
    emptyMessage: String? = null,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.sm)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold
        )

        when {
            isLoading -> {
                Text(
                    text = "Loading...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            emptyMessage != null -> {
                Text(
                    text = emptyMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            else -> {
                content()
            }
        }
    }
}
