package edu.watumull.presencify.feature.academics.search_branch

import androidx.lifecycle.viewModelScope
import edu.watumull.presencify.core.design.systems.components.dialog.DialogType
import edu.watumull.presencify.core.domain.onError
import edu.watumull.presencify.core.domain.onSuccess
import edu.watumull.presencify.core.domain.repository.academics.BranchRepository
import edu.watumull.presencify.core.presentation.toUiText
import edu.watumull.presencify.core.presentation.utils.BaseViewModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SearchBranchViewModel(
    private val branchRepository: BranchRepository,
) : BaseViewModel<SearchBranchState, SearchBranchEvent, SearchBranchAction>(
    initialState = SearchBranchState()
) {

    init {
        // Load initial data
        loadBranches()

        // Setup debounced search
        setupDebouncedSearch()
    }

    @OptIn(FlowPreview::class)
    private fun setupDebouncedSearch() {
        viewModelScope.launch {
            stateFlow
                .map { it.searchQuery }
                .debounce(300)
                .distinctUntilChanged()
                .collectLatest { _ ->
                    loadBranches()
                }
        }
    }

    private fun loadBranches() {
        viewModelScope.launch {
            updateState { it.copy(isLoadingBranches = true) }

            branchRepository.getBranches(
                searchQuery = stateFlow.value.searchQuery.ifBlank { null }
            )
                .onSuccess { branches ->
                    updateState {
                        it.copy(
                            branches = branches.toPersistentList(),
                            isLoadingBranches = false,
                            isRefreshing = false
                        )
                    }
                }
                .onError { error ->
                    updateState {
                        it.copy(
                            isLoadingBranches = false,
                            isRefreshing = false,
                            dialogState = SearchBranchState.DialogState(
                                dialogType = DialogType.ERROR,
                                title = "Error",
                                message = error.toUiText(),
                                dialogIntention = DialogIntention.GENERIC
                            )
                        )
                    }
                }
        }
    }

    override fun handleAction(action: SearchBranchAction) {
        when (action) {

            is SearchBranchAction.BackButtonClick -> {
                sendEvent(SearchBranchEvent.NavigateBack)
            }

            is SearchBranchAction.DismissDialog -> {
                updateState { it.copy(dialogState = null) }
            }

            is SearchBranchAction.UpdateSearchQuery -> {
                updateState { it.copy(searchQuery = action.query) }
            }

            is SearchBranchAction.Search -> {
                loadBranches()
            }

            is SearchBranchAction.Refresh -> {
                updateState { it.copy(isRefreshing = true) }
                loadBranches()
            }

            is SearchBranchAction.BranchCardClick -> {
                sendEvent(SearchBranchEvent.NavigateToBranchDetails(action.branchId))
            }

            SearchBranchAction.ClickFloatingActionButton -> {
                sendEvent(SearchBranchEvent.NavigateToAddEditBranch)
            }
        }
    }
}

