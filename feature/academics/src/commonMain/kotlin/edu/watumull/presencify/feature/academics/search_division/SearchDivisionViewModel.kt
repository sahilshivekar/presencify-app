package edu.watumull.presencify.feature.academics.search_division

import androidx.lifecycle.viewModelScope
import edu.watumull.presencify.core.design.systems.components.dialog.DialogType
import edu.watumull.presencify.core.domain.onError
import edu.watumull.presencify.core.domain.onSuccess
import edu.watumull.presencify.core.domain.repository.academics.BranchRepository
import edu.watumull.presencify.core.domain.repository.academics.DivisionRepository
import edu.watumull.presencify.core.presentation.pagination.Paginator
import edu.watumull.presencify.core.presentation.toUiText
import edu.watumull.presencify.core.presentation.utils.BaseViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SearchDivisionViewModel(
    private val divisionRepository: DivisionRepository,
    private val branchRepository: BranchRepository,
) : BaseViewModel<SearchDivisionState, SearchDivisionEvent, SearchDivisionAction>(
    initialState = SearchDivisionState()
) {

    private val paginator = Paginator<Int, edu.watumull.presencify.core.domain.model.academics.DivisionListWithTotalCount>(
        initialKey = 1,
        onLoadUpdated = { isLoading ->
            updateState { it.copy(isLoadingMore = isLoading) }
        },
        onRequest = { page ->
            val state = stateFlow.value
            val academicYears = state.selectedAcademicYear?.split("-")?.map { it.trim().toInt() }

            divisionRepository.getDivisions(
                searchQuery = state.searchQuery.ifBlank { null },
                semesterNumber = state.selectedSemesterNumber,
                branchId = state.selectedBranch?.id,
                academicStartYear = academicYears?.getOrNull(0),
                academicEndYear = academicYears?.getOrNull(1),
                page = page,
                limit = 20
            )
        },
        getNextKey = { currentPage, _ ->
            currentPage + 1
        },
        onError = { error ->
            updateState {
                it.copy(
                    dialogState = SearchDivisionState.DialogState(
                        dialogType = DialogType.ERROR,
                        title = "Error",
                        message = error.toUiText(),
                        dialogIntention = DialogIntention.GENERIC
                    )
                )
            }
        },
        onSuccess = { response, _ ->
            updateState {
                it.copy(
                    divisions = if (stateFlow.value.currentPage == 1) response.divisions.toPersistentList() else it.divisions.addAll(
                        response.divisions.toPersistentList()
                    ),
                    currentPage = stateFlow.value.currentPage + 1,
                    isRefreshing = false,
                    isLoadingDivisions = false
                )
            }
        },
        endReached = { currentPage, response ->
            // End reached when we have loaded all divisions
            val totalLoadedDivisions = currentPage * 20
            totalLoadedDivisions >= response.totalCount
        }
    )

    init {
        // Load initial data
        viewModelScope.launch {
            loadBranches()
        }

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
                    refreshSearch()
                }
        }
    }

    private fun loadNextDivisions() {
        viewModelScope.launch {
            paginator.loadNextItems()
        }
    }

    private fun refreshSearch() {
        updateState { it.copy(divisions = persistentListOf(), currentPage = 1) }
        paginator.reset()
        loadNextDivisions()
    }

    private suspend fun loadBranches() {
        updateState { it.copy(areBranchesLoading = true) }
        branchRepository.getBranches(searchQuery = null)
            .onSuccess { branches ->
                updateState {
                    it.copy(
                        branchOptions = branches.toPersistentList(),
                        areBranchesLoading = false
                    )
                }
            }
            .onError { error ->
                updateState {
                    it.copy(
                        areBranchesLoading = false,
                        dialogState = SearchDivisionState.DialogState(
                            dialogType = DialogType.ERROR,
                            title = "Error",
                            message = error.toUiText(),
                            dialogIntention = DialogIntention.GENERIC
                        )
                    )
                }
            }
    }

    override fun handleAction(action: SearchDivisionAction) {
        when (action) {

            is SearchDivisionAction.BackButtonClick -> {
                sendEvent(SearchDivisionEvent.NavigateBack)
            }

            is SearchDivisionAction.DismissDialog -> {
                updateState { it.copy(dialogState = null) }
            }

            is SearchDivisionAction.UpdateSearchQuery -> {
                updateState { it.copy(searchQuery = action.query) }
            }

            is SearchDivisionAction.Search -> {
                refreshSearch()
            }

            is SearchDivisionAction.Refresh -> {
                updateState { it.copy(isRefreshing = true) }
                refreshSearch()
            }

            is SearchDivisionAction.SelectSemesterNumber -> {
                updateState { it.copy(selectedSemesterNumber = action.semesterNumber) }
            }

            is SearchDivisionAction.SelectAcademicYear -> {
                updateState { it.copy(selectedAcademicYear = action.year) }
            }

            is SearchDivisionAction.SelectBranch -> {
                updateState { it.copy(selectedBranch = action.branch) }
            }

            is SearchDivisionAction.ResetFilters -> {
                updateState {
                    it.copy(
                        selectedSemesterNumber = null,
                        selectedAcademicYear = null,
                        selectedBranch = null
                    )
                }
            }

            is SearchDivisionAction.ApplyFilters -> {
                refreshSearch()
            }

            is SearchDivisionAction.DivisionCardClick -> {
                sendEvent(SearchDivisionEvent.NavigateToDivisionDetails(action.divisionId))
            }

            is SearchDivisionAction.LoadMoreDivisions -> {
                loadNextDivisions()
            }

            SearchDivisionAction.HideBottomSheet -> {
                updateState { it.copy(isBottomSheetVisible = false) }
            }

            SearchDivisionAction.ShowBottomSheet -> {
                updateState {
                    it.copy(isBottomSheetVisible = true)
                }
            }

            SearchDivisionAction.ClickFloatingActionButton -> {
                sendEvent(SearchDivisionEvent.NavigateToAddEditDivision)
            }
        }
    }
}

