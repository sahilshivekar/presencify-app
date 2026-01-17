package edu.watumull.presencify.feature.academics.search_course

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import edu.watumull.presencify.core.design.systems.components.dialog.DialogType
import edu.watumull.presencify.core.domain.onError
import edu.watumull.presencify.core.domain.onSuccess
import edu.watumull.presencify.core.domain.repository.academics.BranchRepository
import edu.watumull.presencify.core.domain.repository.academics.CourseRepository
import edu.watumull.presencify.core.domain.repository.academics.SchemeRepository
import edu.watumull.presencify.core.presentation.pagination.Paginator
import edu.watumull.presencify.core.presentation.toUiText
import edu.watumull.presencify.core.presentation.utils.BaseViewModel
import edu.watumull.presencify.feature.academics.navigation.AcademicsRoutes
import edu.watumull.presencify.feature.academics.navigation.SearchCourseIntention
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SearchCourseViewModel(
    private val courseRepository: CourseRepository,
    private val branchRepository: BranchRepository,
    private val schemeRepository: SchemeRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<SearchCourseState, SearchCourseEvent, SearchCourseAction>(
    initialState = SearchCourseState()
) {

    private val routeParams = savedStateHandle.toRoute<AcademicsRoutes.SearchCourse>()

    private val paginator = Paginator<Int, edu.watumull.presencify.core.domain.model.academics.CourseListWithTotalCount>(
        initialKey = 1,
        onLoadUpdated = { isLoading ->
            updateState { it.copy(isLoadingMore = isLoading) }
        },
        onRequest = { page ->
            val state = stateFlow.value

            courseRepository.getCourses(
                searchQuery = state.searchQuery.ifBlank { null },
                branchId = state.selectedBranch?.id,
                semesterNumber = state.selectedSemesterNumber,
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
                    dialogState = SearchCourseState.DialogState(
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
                    courses = if (stateFlow.value.currentPage == 1) response.courses.toPersistentList() else it.courses.addAll(
                        response.courses.toPersistentList()
                    ),
                    currentPage = stateFlow.value.currentPage + 1,
                    isRefreshing = false,
                    isLoadingCourses = false
                )
            }
        },
        endReached = { currentPage, response ->
            // End reached when we have loaded all courses
            val totalLoadedCourses = currentPage * 20
            totalLoadedCourses >= response.totalCount
        }
    )

    init {
        // Set intention from route params
        val intention = try {
            SearchCourseIntention.valueOf(routeParams.intention)
        } catch (_: IllegalArgumentException) {
            SearchCourseIntention.DEFAULT
        }

        val isSelectable = intention != SearchCourseIntention.DEFAULT

        updateState {
            it.copy(
                intention = intention,
                isSelectable = isSelectable,
                searchQuery = ""
            )
        }

        // Load initial data
        viewModelScope.launch {
            loadBranches()
            loadSchemes()
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

    private fun loadNextCourses() {
        viewModelScope.launch {
            paginator.loadNextItems()
        }
    }

    private fun refreshSearch() {
        updateState { it.copy(courses = persistentListOf(), currentPage = 1) }
        paginator.reset()
        loadNextCourses()
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
                        dialogState = SearchCourseState.DialogState(
                            dialogType = DialogType.ERROR,
                            title = "Error",
                            message = error.toUiText(),
                            dialogIntention = DialogIntention.GENERIC
                        )
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
                        dialogState = SearchCourseState.DialogState(
                            dialogType = DialogType.ERROR,
                            title = "Error",
                            message = error.toUiText(),
                            dialogIntention = DialogIntention.GENERIC
                        )
                    )
                }
            }
    }

    override fun handleAction(action: SearchCourseAction) {
        when (action) {

            is SearchCourseAction.BackButtonClick -> {
                sendEvent(SearchCourseEvent.NavigateBack)
            }

            is SearchCourseAction.DismissDialog -> {
                updateState { it.copy(dialogState = null) }
            }

            is SearchCourseAction.UpdateSearchQuery -> {
                updateState { it.copy(searchQuery = action.query) }
            }

            is SearchCourseAction.Search -> {
                refreshSearch()
            }

            is SearchCourseAction.Refresh -> {
                updateState { it.copy(isRefreshing = true) }
                refreshSearch()
            }

            is SearchCourseAction.SelectSemesterNumber -> {
                updateState { it.copy(selectedSemesterNumber = action.semesterNumber) }
            }

            is SearchCourseAction.SelectBranch -> {
                updateState { it.copy(selectedBranch = action.branch) }
            }

            is SearchCourseAction.SelectScheme -> {
                updateState { it.copy(selectedScheme = action.scheme) }
            }

            is SearchCourseAction.ResetFilters -> {
                updateState {
                    it.copy(
                        selectedSemesterNumber = null,
                        selectedBranch = null,
                        selectedScheme = null
                    )
                }
            }

            is SearchCourseAction.ApplyFilters -> {
                refreshSearch()
            }

            is SearchCourseAction.ToggleCourseSelection -> {
                val currentSelections = stateFlow.value.selectedCourseIds
                val newSelections = if (currentSelections.contains(action.courseId)) {
                    currentSelections - action.courseId
                } else {
                    currentSelections + action.courseId
                }
                updateState { it.copy(selectedCourseIds = newSelections) }
            }

            is SearchCourseAction.CourseCardClick -> {
                if (stateFlow.value.isSelectable) {
                    // In selection mode, toggle selection
                    val currentSelections = stateFlow.value.selectedCourseIds
                    val newSelections = if (currentSelections.contains(action.courseId)) {
                        currentSelections - action.courseId
                    } else {
                        currentSelections + action.courseId
                    }
                    updateState { it.copy(selectedCourseIds = newSelections) }
                } else {
                    // Navigate to course details
                    sendEvent(SearchCourseEvent.NavigateToCourseDetails(action.courseId))
                }
            }

            is SearchCourseAction.LoadMoreCourses -> {
                loadNextCourses()
            }

            is SearchCourseAction.DoneButtonClick -> {
                sendEvent(SearchCourseEvent.NavigateBackWithSelection)
            }

            SearchCourseAction.HideBottomSheet -> {
                updateState { it.copy(isBottomSheetVisible = false) }
            }

            SearchCourseAction.ShowBottomSheet -> {
                updateState {
                    it.copy(isBottomSheetVisible = true)
                }
            }

            SearchCourseAction.ClickFloatingActionButton -> {
                sendEvent(SearchCourseEvent.NavigateToAddEditCourse)
            }
        }
    }
}

