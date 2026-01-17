package edu.watumull.presencify.feature.academics.search_scheme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.watumull.presencify.core.design.systems.components.PresencifyDefaultLoadingScreen
import edu.watumull.presencify.core.design.systems.components.PresencifyNoResultsIndicator
import edu.watumull.presencify.core.design.systems.components.PresencifyScaffold
import edu.watumull.presencify.core.design.systems.components.PresencifySearchBar
import edu.watumull.presencify.core.design.systems.components.dialog.PresencifyAlertDialog
import edu.watumull.presencify.core.presentation.UiConstants
import edu.watumull.presencify.core.presentation.components.SchemeListItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchSchemeScreen(
    state: SearchSchemeState,
    onAction: (SearchSchemeAction) -> Unit,
) {
    PresencifyScaffold(
        backPress = { onAction(SearchSchemeAction.BackButtonClick) },
        topBarTitle = "Search Schemes",
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onAction(SearchSchemeAction.ClickFloatingActionButton) },
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add scheme"
                )
            }
        }
    ) { paddingValues ->
        when (state.viewState) {
            is SearchSchemeState.ViewState.Loading -> {
                PresencifyDefaultLoadingScreen()
            }

            is SearchSchemeState.ViewState.Error -> {
                PresencifyNoResultsIndicator(
                    text = state.viewState.message.asString()
                )
            }

            is SearchSchemeState.ViewState.Content -> {
                SearchSchemeScreenContent(
                    state = state,
                    onAction = onAction,
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
                onAction(SearchSchemeAction.DismissDialog)
            }
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun SearchSchemeScreenContent(
    state: SearchSchemeState,
    onAction: (SearchSchemeAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pullRefreshState = rememberPullRefreshState(
        refreshing = state.isRefreshing,
        onRefresh = { onAction(SearchSchemeAction.Refresh) }
    )

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
                onQueryChange = { onAction(SearchSchemeAction.UpdateSearchQuery(it)) },
                placeholder = "Search schemes...",
                onSearchClick = { onAction(SearchSchemeAction.Search) },
                showFilterIcon = false
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .pullRefresh(pullRefreshState)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = state.schemes,
                        key = { it.id }
                    ) { scheme ->
                        SchemeListItem(
                            name = scheme.name,
                            onClick = { onAction(SearchSchemeAction.SchemeCardClick(scheme.id)) },
                            modifier = Modifier.fillMaxWidth(),
                            universityName = scheme.university?.name ?: "N/A",
                        )
                    }

                    item {
                        when {
                            state.schemes.isEmpty() && state.isLoadingSchemes -> {
                                PresencifyDefaultLoadingScreen()
                            }

                            state.schemes.isEmpty() && !state.isLoadingSchemes -> {
                                PresencifyNoResultsIndicator(
                                    text = "No schemes found"
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

