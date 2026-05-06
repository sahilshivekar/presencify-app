package edu.watumull.presencify.feature.schedule.search_timetable

import androidx.lifecycle.viewModelScope
import edu.watumull.presencify.core.designsystem.components.dialog.DialogType
import edu.watumull.presencify.core.domain.onError
import edu.watumull.presencify.core.domain.onSuccess
import edu.watumull.presencify.core.domain.repository.academics.BranchRepository
import edu.watumull.presencify.core.domain.repository.schedule.TimetableRepository
import edu.watumull.presencify.core.presentation.pagination.Paginator
import edu.watumull.presencify.core.presentation.toUiText
import edu.watumull.presencify.core.presentation.utils.BaseViewModel
import edu.watumull.presencify.feature.schedule.search_timetable.SearchTimetableEvent.NavigateBack
import edu.watumull.presencify.feature.schedule.search_timetable.SearchTimetableEvent.NavigateToAddTimetable
import edu.watumull.presencify.feature.schedule.search_timetable.SearchTimetableEvent.NavigateToTimetableDetails
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SearchTimetableViewModel(
    private val timetableRepository: TimetableRepository,
    private val branchRepository: BranchRepository
) : BaseViewModel<SearchTimetableState, SearchTimetableEvent, SearchTimetableAction>(
    initialState = SearchTimetableState()
) {

    private val paginator = Paginator(
        initialKey = 1,
        onLoadUpdated = { isLoading ->
            updateState { it.copy(isLoadingMore = isLoading) }
        },
        onRequest = { page ->
            val state = stateFlow.value
            val startYear = state.academicStartYear.toIntOrNull()
            val endYear = state.academicEndYear.toIntOrNull()

            timetableRepository.getTimetables(
                semesterNumber = state.selectedSemesters.firstOrNull(),
                academicStartYearOfSemester = startYear,
                academicEndYearOfSemester = endYear,
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
                    dialogState = SearchTimetableState.DialogState(
                        dialogType = DialogType.ERROR,
                        title = "Error",
                        message = error.toUiText(),
                        dialogIntention = DialogIntention.GENERIC
                    )
                )
            }
        },
        onSuccess = { timetables, _ ->
            updateState {
                it.copy(
                    timetables = if (stateFlow.value.currentPage == 1) timetables.toPersistentList() else it.timetables.addAll(
                        timetables.toPersistentList()
                    ),
                    currentPage = stateFlow.value.currentPage + 1,
                    isRefreshing = false,
                    isLoadingTimetables = false
                )
            }
        },
        endReached = { currentPage, response ->
            val totalLoadedTimetables = currentPage * 20
            totalLoadedTimetables >= response.size
        }
    )

    init {
        viewModelScope.launch {
            loadBranches()
        }
        setupDebouncedSearch()
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
                        dialogState = SearchTimetableState.DialogState(
                            dialogType = DialogType.ERROR,
                            title = "Error",
                            message = error.toUiText(),
                            dialogIntention = DialogIntention.GENERIC
                        )
                    )
                }
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

    private fun loadNextTimetables() {
        viewModelScope.launch {
            paginator.loadNextItems()
        }
    }

    private fun refreshSearch() {
        updateState { it.copy(timetables = persistentListOf(), currentPage = 1) }
        paginator.reset()
        loadNextTimetables()
    }

    override fun handleAction(action: SearchTimetableAction) {
        when (action) {
            SearchTimetableAction.BackButtonClick -> {
                sendEvent(NavigateBack)
            }

            SearchTimetableAction.DismissDialog -> {
                updateState { it.copy(dialogState = null) }
            }

            is SearchTimetableAction.UpdateSearchQuery -> {
                updateState { it.copy(searchQuery = action.query) }
            }

            SearchTimetableAction.Search -> {
                refreshSearch()
            }

            SearchTimetableAction.Refresh -> {
                updateState { it.copy(isRefreshing = true) }
                refreshSearch()
            }

            is SearchTimetableAction.ToggleBranch -> {
                val currentBranches = stateFlow.value.selectedBranches
                val newBranches = if (currentBranches.contains(action.branch)) {
                    currentBranches - action.branch
                } else {
                    // Server accepts only single value, so clear others
                    persistentListOf(action.branch)
                }
                updateState { it.copy(selectedBranches = newBranches.toPersistentList()) }
            }

            is SearchTimetableAction.ToggleSemester -> {
                val currentSemesters = stateFlow.value.selectedSemesters
                val newSemesters = if (currentSemesters.contains(action.semester)) {
                    currentSemesters - action.semester
                } else {
                    // Server accepts only single value, so clear others
                    persistentListOf(action.semester)
                }
                updateState { it.copy(selectedSemesters = newSemesters.toPersistentList()) }
            }

            is SearchTimetableAction.UpdateAcademicStartYear -> {
                updateState { it.copy(academicStartYear = action.year) }
            }

            is SearchTimetableAction.UpdateAcademicEndYear -> {
                updateState { it.copy(academicEndYear = action.year) }
            }

            SearchTimetableAction.ResetFilters -> {
                updateState {
                    it.copy(
                        selectedBranches = persistentListOf(),
                        selectedSemesters = persistentListOf(),
                        academicStartYear = "",
                        academicEndYear = ""
                    )
                }
            }

            SearchTimetableAction.ApplyFilters -> {
                refreshSearch()
            }

            is SearchTimetableAction.TimetableCardClick -> {
                sendEvent(NavigateToTimetableDetails(action.timetableId))
            }

            SearchTimetableAction.LoadMoreTimetables -> {
                loadNextTimetables()
            }

            SearchTimetableAction.ClickFloatingActionButton -> {
                sendEvent(NavigateToAddTimetable)
            }
        }
    }
}
