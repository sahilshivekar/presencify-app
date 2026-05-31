package edu.watumull.presencify.feature.academics.search_batch

import androidx.lifecycle.viewModelScope
import edu.watumull.presencify.core.designsystem.components.dialog.DialogType
import edu.watumull.presencify.core.domain.onError
import edu.watumull.presencify.core.domain.onSuccess
import edu.watumull.presencify.core.domain.repository.academics.BatchRepository
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

class SearchBatchViewModel(
    private val batchRepository: BatchRepository,
    private val branchRepository: BranchRepository,
    private val divisionRepository: DivisionRepository,
) : BaseViewModel<SearchBatchState, SearchBatchEvent, SearchBatchAction>(
    initialState = SearchBatchState()
) {

    private val paginator = Paginator<Int, edu.watumull.presencify.core.domain.model.academics.BatchListWithTotalCount>(
        initialKey = 1,
        onLoadUpdated = { isLoading ->
            updateState { it.copy(isLoadingMore = isLoading) }
        },
        onRequest = { page ->
            val state = stateFlow.value
            val startYear = state.academicStartYear.toIntOrNull()
            val endYear = state.academicEndYear.toIntOrNull()

            batchRepository.getBatches(
                semesterNumber = state.selectedSemesterNumber,
                branchId = state.selectedBranch?.id,
                divisionId = state.selectedDivision?.id,
                academicStartYear = startYear,
                academicEndYear = endYear,
                searchQuery = state.searchQuery.ifBlank { null },
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
                    viewState = SearchBatchState.ViewState.Error(error.toUiText()),
                    isLoadingBatches = false
                )
            }
        },
        onSuccess = { response, _ ->
            updateState {
                it.copy(
                    batches = if (stateFlow.value.currentPage == 1) response.batches.toPersistentList() else it.batches.addAll(
                        response.batches.toPersistentList()
                    ),
                    currentPage = stateFlow.value.currentPage + 1,
                    isRefreshing = false,
                    isLoadingBatches = false
                )
            }
        },
        endReached = { currentPage, response ->
            // End reached when we have loaded all batches
            val totalLoadedBatches = currentPage * 20
            totalLoadedBatches >= response.totalCount
        }
    )

    init {
        viewModelScope.launch {
            loadBranches()
        }
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

    private fun loadNextBatches() {
        viewModelScope.launch {
            paginator.loadNextItems()
        }
    }

    private fun refreshSearch() {
        updateState { it.copy(batches = persistentListOf(), currentPage = 1) }
        paginator.reset()
        loadNextBatches()
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
                        viewState = SearchBatchState.ViewState.Error(error.toUiText())
                    )
                }
            }
    }

    private suspend fun loadDivisions() {
        val state = stateFlow.value

        val semester = state.selectedSemesterNumber
        val branchId = state.selectedBranch?.id
        val startYear = state.academicStartYear.toIntOrNull()
        val endYear = state.academicEndYear.toIntOrNull()

        if (semester != null && branchId != null && startYear != null && endYear != null) {

            // Load divisions
            updateState { it.copy(areDivisionsLoading = true) }
            divisionRepository.getDivisions(
                semesterNumber = semester,
                branchId = branchId,
                academicStartYear = startYear,
                academicEndYear = endYear,
                searchQuery = null,
                getAll = true
            )
                .onSuccess { divisionsResponse ->
                    updateState {
                        it.copy(
                            divisionOptions = divisionsResponse.divisions.toPersistentList(),
                            areDivisionsLoading = false
                        )
                    }
                }
                .onError { error ->
                    updateState {
                        it.copy(
                            areDivisionsLoading = false,
                            viewState = SearchBatchState.ViewState.Error(error.toUiText())
                        )
                    }
                }
        } else {
            // Clear divisions if prerequisites are not met
            updateState {
                it.copy(
                    divisionOptions = persistentListOf(),
                    selectedDivision = null
                )
            }
        }
    }

    override fun handleAction(action: SearchBatchAction) {
        when (action) {

            is SearchBatchAction.NavigateBack -> {
                sendEvent(SearchBatchEvent.NavigateBack)
            }


            is SearchBatchAction.UpdateSearchQuery -> {
                updateState { it.copy(searchQuery = action.query) }
            }

            is SearchBatchAction.Search -> {
                refreshSearch()
            }

            is SearchBatchAction.Refresh -> {
                updateState { it.copy(isRefreshing = true) }
                refreshSearch()
            }

            is SearchBatchAction.SelectSemesterNumber -> {
                updateState { it.copy(selectedSemesterNumber = action.semesterNumber) }
                viewModelScope.launch {
                    loadDivisions()
                }
            }

            is SearchBatchAction.UpdateAcademicStartYear -> {
                updateState { it.copy(academicStartYear = action.year) }
                viewModelScope.launch {
                    loadDivisions()
                }
            }

            is SearchBatchAction.UpdateAcademicEndYear -> {
                updateState { it.copy(academicEndYear = action.year) }
                viewModelScope.launch {
                    loadDivisions()
                }
            }

            is SearchBatchAction.SelectBranch -> {
                updateState { it.copy(selectedBranch = action.branch) }
                viewModelScope.launch {
                    loadDivisions()
                }
            }

            is SearchBatchAction.SelectDivision -> {
                updateState { it.copy(selectedDivision = action.division) }
            }

            is SearchBatchAction.ResetFilters -> {
                updateState {
                    it.copy(
                        selectedSemesterNumber = null,
                        academicStartYear = "",
                        academicEndYear = "",
                        selectedBranch = null,
                        selectedDivision = null
                    )
                }
            }

            is SearchBatchAction.ApplyFilters -> {
                refreshSearch()
            }

            is SearchBatchAction.BatchCardClick -> {
                sendEvent(SearchBatchEvent.NavigateToBatchDetails(action.batchId))
            }

            is SearchBatchAction.LoadMoreBatches -> {
                loadNextBatches()
            }

            SearchBatchAction.ClickFloatingActionButton -> {
                sendEvent(SearchBatchEvent.NavigateToAddEditBatch)
            }
        }
    }
}

