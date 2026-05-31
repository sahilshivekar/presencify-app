package edu.watumull.presencify.feature.academics.search_semester

import androidx.lifecycle.viewModelScope
import edu.watumull.presencify.core.domain.onError
import edu.watumull.presencify.core.domain.onSuccess
import edu.watumull.presencify.core.domain.repository.academics.BranchRepository
import edu.watumull.presencify.core.domain.repository.academics.SchemeRepository
import edu.watumull.presencify.core.domain.repository.academics.SemesterRepository
import edu.watumull.presencify.core.presentation.pagination.Paginator
import edu.watumull.presencify.core.presentation.toUiText
import edu.watumull.presencify.core.presentation.utils.BaseViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SearchSemesterViewModel(
    private val semesterRepository: SemesterRepository,
    private val branchRepository: BranchRepository,
    private val schemeRepository: SchemeRepository,
) : BaseViewModel<SearchSemesterState, SearchSemesterEvent, SearchSemesterAction>(
    initialState = SearchSemesterState()
) {

    private val paginator = Paginator<Int, edu.watumull.presencify.core.domain.model.academics.SemesterListWithTotalCount>(
        initialKey = 1,
        onLoadUpdated = { isLoading ->
            updateState { it.copy(isLoadingMore = isLoading) }
        },
        onRequest = { page ->
            val state = stateFlow.value
            val startYear = state.academicStartYear.toIntOrNull()
            val endYear = state.academicEndYear.toIntOrNull()

            semesterRepository.getSemesters(
                semesterNumber = state.selectedSemesterNumber,
                academicStartYear = startYear,
                academicEndYear = endYear,
                branchId = state.selectedBranches.firstOrNull()?.id,
                schemeId = state.selectedScheme?.id,
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
                    viewState = SearchSemesterState.ViewState.Error(error.toUiText()),
                    isLoadingSemesters = false
                )
            }
        },
        onSuccess = { response, _ ->
            updateState {
                it.copy(
                    semesters = if (stateFlow.value.currentPage == 1) response.semesters.toPersistentList() else it.semesters.addAll(
                        response.semesters.toPersistentList()
                    ),
                    currentPage = stateFlow.value.currentPage + 1,
                    isRefreshing = false,
                    isLoadingSemesters = false
                )
            }
        },
        endReached = { currentPage, response ->
            // End reached when we have loaded all semesters
            val totalLoadedSemesters = currentPage * 20
            totalLoadedSemesters >= response.totalCount
        }
    )

    init {
        viewModelScope.launch {
            val task1 = async { loadBranches() }
            val task2 = async { loadSchemes() }
            awaitAll(task1, task2)
            setupDebouncedSearch()
        }
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

    private fun loadNextSemesters() {
        viewModelScope.launch {
            paginator.loadNextItems()
        }
    }

    private fun refreshSearch() {
        updateState { it.copy(semesters = persistentListOf(), currentPage = 1) }
        paginator.reset()
        loadNextSemesters()
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
                        viewState = SearchSemesterState.ViewState.Error(error.toUiText())
                    )
                }
            }
    }

    private suspend fun loadSchemes() {
        updateState { it.copy(areSchemesLoading = true) }
        schemeRepository.getSchemes(searchQuery = null)
            .onSuccess { schemes ->
                updateState {
                    it.copy(
                        schemeOptions = schemes.toPersistentList(),
                        areSchemesLoading = false
                    )
                }
            }
            .onError { error ->
                updateState {
                    it.copy(
                        areSchemesLoading = false,
                        viewState = SearchSemesterState.ViewState.Error(error.toUiText())
                    )
                }
            }
    }

    override fun handleAction(action: SearchSemesterAction) {
        when (action) {

            is SearchSemesterAction.NavigateBack -> {
                sendEvent(SearchSemesterEvent.NavigateBack)
            }


            is SearchSemesterAction.UpdateSearchQuery -> {
                updateState { it.copy(searchQuery = action.query) }
            }

            is SearchSemesterAction.Search -> {
                refreshSearch()
            }

            is SearchSemesterAction.Refresh -> {
                updateState { it.copy(isRefreshing = true) }
                refreshSearch()
            }

            is SearchSemesterAction.SelectSemesterNumber -> {
                updateState { it.copy(selectedSemesterNumber = action.semesterNumber) }
            }

            is SearchSemesterAction.UpdateAcademicStartYear -> {
                updateState { it.copy(academicStartYear = action.year) }
            }

            is SearchSemesterAction.UpdateAcademicEndYear -> {
                updateState { it.copy(academicEndYear = action.year) }
            }

            is SearchSemesterAction.ToggleBranch -> {
                val currentBranches = stateFlow.value.selectedBranches
                val newBranches = if (currentBranches.contains(action.branch)) {
                    currentBranches - action.branch
                } else {
                    currentBranches + action.branch
                }
                updateState { it.copy(selectedBranches = newBranches.toPersistentList()) }
            }

            is SearchSemesterAction.SelectScheme -> {
                updateState { it.copy(selectedScheme = action.scheme) }
            }

            is SearchSemesterAction.ResetFilters -> {
                updateState {
                    it.copy(
                        selectedSemesterNumber = null,
                        academicStartYear = "",
                        academicEndYear = "",
                        selectedBranches = persistentListOf(),
                        selectedScheme = null
                    )
                }
            }

            is SearchSemesterAction.ApplyFilters -> {
                refreshSearch()
            }

            is SearchSemesterAction.SemesterCardClick -> {
                sendEvent(SearchSemesterEvent.NavigateToSemesterDetails(action.semesterId))
            }

            is SearchSemesterAction.LoadMoreSemesters -> {
                loadNextSemesters()
            }

            SearchSemesterAction.ClickFloatingActionButton -> {
                sendEvent(SearchSemesterEvent.NavigateToAddEditSemester)
            }
        }
    }
}

