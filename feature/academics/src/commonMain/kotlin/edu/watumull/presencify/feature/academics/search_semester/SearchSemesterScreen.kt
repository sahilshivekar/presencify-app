package edu.watumull.presencify.feature.academics.search_semester

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
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
import edu.watumull.presencify.core.presentation.components.SemesterListItem
import edu.watumull.presencify.core.presentation.composition_locals.LocalUserRole
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchSemesterScreen(
    state: SearchSemesterState,
    onAction: (SearchSemesterAction) -> Unit,
) {
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            skipHiddenState = false,
            initialValue = SheetValue.Hidden
        )
    )

    val scope = rememberCoroutineScope()

    PresencifyBottomSheetScaffold(
        backPress = { onAction(SearchSemesterAction.BackButtonClick) },
        topBarTitle = "Search Semesters",
        scaffoldState = scaffoldState,
        sheetContent = {
            SearchSemesterBottomSheetContent(
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
                    onClick = { onAction(SearchSemesterAction.ClickFloatingActionButton) },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add semester"
                    )
                }
            }
        }
    ) { paddingValues ->
        when (state.viewState) {
            is SearchSemesterState.ViewState.Loading -> {
                PresencifyDefaultLoadingScreen()
            }

            is SearchSemesterState.ViewState.Error -> {
                PresencifyNoResultsIndicator(
                    text = state.viewState.message.asString()
                )
            }

            is SearchSemesterState.ViewState.Content -> {
                SearchSemesterScreenContent(
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
                onAction(SearchSemesterAction.DismissDialog)
            }
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun SearchSemesterScreenContent(
    state: SearchSemesterState,
    onAction: (SearchSemesterAction) -> Unit,
    onFilterClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val pullRefreshState = rememberPullRefreshState(
        refreshing = state.isRefreshing,
        onRefresh = { onAction(SearchSemesterAction.Refresh) }
    )
    val lazyListState = rememberLazyListState()

    LaunchedEffect(state.semesters) {
        snapshotFlow {
            lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
        }.distinctUntilChanged().collect { lastVisibleIndex ->
            // If lastVisibleIndex == 0 then it means the list is empty and the loading indicator is an inside item{} taking index 0
            // initial load should only be trigger within init block of the view model, so that we can apply pre-filtering before loading students for the first time
            if (lastVisibleIndex != null && lastVisibleIndex != 0 && lastVisibleIndex >= state.semesters.lastIndex - 10) {
                onAction(SearchSemesterAction.LoadMoreSemesters)
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
                onQueryChange = { onAction(SearchSemesterAction.UpdateSearchQuery(it)) },
                onFilterClick = onFilterClick,
                placeholder = "Search semesters...",
                onSearchClick = { onAction(SearchSemesterAction.Search) }
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
                        items = state.semesters,
                        key = { it.id }
                    ) { semester ->
                        val divisionCodes = semester.divisions?.map { it.divisionCode }?.toPersistentList()
                            ?: kotlinx.collections.immutable.persistentListOf()
                        val batchCodes = semester.divisions?.flatMap { division ->
                            division.batches?.map { it.batchCode } ?: emptyList()
                        }?.distinct()?.toPersistentList() ?: kotlinx.collections.immutable.persistentListOf()
                        val branchAbbreviation = semester.branch?.abbreviation ?: "N/A"

                        SemesterListItem(
                            semesterNumber = semester.semesterNumber,
                            semesterAcademicStartYear = semester.academicStartYear,
                            semesterAcademicEndYear = semester.academicEndYear,
                            divisionCodes = divisionCodes,
                            batchCodes = batchCodes,
                            branchAbbreviation = branchAbbreviation,
                            onClick = { onAction(SearchSemesterAction.SemesterCardClick(semester.id)) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        when {
                            state.isLoadingMore || (state.semesters.isEmpty() && state.isLoadingSemesters) -> {
                                PresencifyDefaultLoadingScreen()
                            }

                            state.semesters.isEmpty() && !state.isLoadingSemesters -> {
                                PresencifyNoResultsIndicator(
                                    text = "No semesters found"
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

