package edu.watumull.presencify.feature.academics.search_branch

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
import edu.watumull.presencify.core.designsystem.components.PresencifyDefaultLoadingScreen
import edu.watumull.presencify.core.designsystem.components.PresencifyNoResultsIndicator
import edu.watumull.presencify.core.designsystem.components.PresencifyScaffold
import edu.watumull.presencify.core.designsystem.components.PresencifySearchBar
import edu.watumull.presencify.core.designsystem.components.dialog.PresencifyAlertDialog
import edu.watumull.presencify.core.designsystem.theme.DesignToken
import edu.watumull.presencify.core.domain.model.auth.UserRole
import edu.watumull.presencify.core.presentation.UiConstants
import edu.watumull.presencify.core.presentation.components.BranchListItem
import edu.watumull.presencify.core.presentation.composition_locals.LocalUserRole

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBranchScreen(
    state: SearchBranchState,
    onAction: (SearchBranchAction) -> Unit,
) {
    PresencifyScaffold(
        backPress = { onAction(SearchBranchAction.BackButtonClick) },
        topBarTitle = "Search Branches",
        floatingActionButton = {
            if (LocalUserRole.current == UserRole.ADMIN) {
                FloatingActionButton(
                    onClick = { onAction(SearchBranchAction.ClickFloatingActionButton) },
                    modifier = Modifier.padding(DesignToken.spacing.lg)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add branch"
                    )
                }
            }
        }
    ) { paddingValues ->
        when (state.viewState) {
            is SearchBranchState.ViewState.Loading -> {
                PresencifyDefaultLoadingScreen()
            }

            is SearchBranchState.ViewState.Error -> {
                PresencifyNoResultsIndicator(
                    text = state.viewState.message.asString()
                )
            }

            is SearchBranchState.ViewState.Content -> {
                SearchBranchScreenContent(
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
                onAction(SearchBranchAction.DismissDialog)
            }
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun SearchBranchScreenContent(
    state: SearchBranchState,
    onAction: (SearchBranchAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pullRefreshState = rememberPullRefreshState(
        refreshing = state.isRefreshing,
        onRefresh = { onAction(SearchBranchAction.Refresh) }
    )

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
                onQueryChange = { onAction(SearchBranchAction.UpdateSearchQuery(it)) },
                placeholder = "Search branches...",
                onSearchClick = { onAction(SearchBranchAction.Search) },
                showFilterIcon = false
            )

            Spacer(modifier = Modifier.height(DesignToken.spacing.lg))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .pullRefresh(pullRefreshState)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.md)
                ) {
                    items(
                        items = state.branches,
                        key = { it.id }
                    ) { branch ->
                        BranchListItem(
                            name = branch.name,
                            abbreviation = branch.abbreviation,
                            onClick = { onAction(SearchBranchAction.BranchCardClick(branch.id)) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        when {
                            state.branches.isEmpty() && state.isLoadingBranches -> {
                                PresencifyDefaultLoadingScreen()
                            }

                            state.branches.isEmpty() && !state.isLoadingBranches -> {
                                PresencifyNoResultsIndicator(
                                    text = "No branches found"
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

