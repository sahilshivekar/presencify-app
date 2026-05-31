package edu.watumull.presencify.feature.attendance.search_attendance

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import edu.watumull.presencify.core.domain.onError
import edu.watumull.presencify.core.domain.onSuccess
import edu.watumull.presencify.core.domain.repository.academics.BatchRepository
import edu.watumull.presencify.core.domain.repository.academics.BranchRepository
import edu.watumull.presencify.core.domain.repository.academics.CourseRepository
import edu.watumull.presencify.core.domain.repository.academics.DivisionRepository
import edu.watumull.presencify.core.domain.repository.academics.SemesterRepository
import edu.watumull.presencify.core.domain.repository.attendance.AttendanceRepository
import edu.watumull.presencify.core.presentation.pagination.Paginator
import edu.watumull.presencify.core.presentation.toUiText
import edu.watumull.presencify.core.presentation.utils.BaseViewModel
import edu.watumull.presencify.feature.attendance.navigation.AttendanceRoutes
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SearchAttendanceViewModel(
    private val attendanceRepository: AttendanceRepository,
    private val branchRepository: BranchRepository,
    private val semesterRepository: SemesterRepository,
    private val divisionRepository: DivisionRepository,
    private val batchRepository: BatchRepository,
    private val courseRepository: CourseRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<SearchAttendanceState, SearchAttendanceEvent, SearchAttendanceAction>(
    initialState = run {
        val routeParams = savedStateHandle.toRoute<AttendanceRoutes.SearchAttendance>()
        SearchAttendanceState(
            routeCourseId = routeParams.courseId,
            studentId = routeParams.studentId,
            semesterId = routeParams.semesterId,
            divisionId = routeParams.divisionId,
            batchId = routeParams.batchId,
            startDate = routeParams.startDate,
            endDate = routeParams.endDate
        )
    }
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

            attendanceRepository.getAttendances(
                date = state.selectedDate,
                classId = state.classId,
                studentId = state.studentId,
                courseId = state.selectedCourse?.id ?: state.routeCourseId,
                semesterId = state.semesterId,
                divisionId = state.selectedDivision?.id ?: state.divisionId,
                batchId = state.selectedBatch?.id ?: state.batchId,
                semesterNumber = state.selectedSemesters.firstOrNull(),
                academicStartYear = startYear,
                academicEndYear = endYear,
                branchId = state.selectedBranch?.id,
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
                    viewState = SearchAttendanceState.ViewState.Error(error.toUiText()),
                    isLoadingAttendances = false
                )
            }
        },
        onSuccess = { items, newKey ->

            updateState {
                it.copy(
                    attendances = (it.attendances + items.attendances).toPersistentList(),
                    currentPage = newKey,
                    isLoadingAttendances = false
                )
            }
        },
        endReached = { _, result ->
            result.attendances.isEmpty()
        }
    )

    init {
        loadInitialData()
        observeSearchQuery()
    }

    override fun handleAction(action: SearchAttendanceAction) {
        when (action) {
            SearchAttendanceAction.NavigateBack -> sendEvent(SearchAttendanceEvent.NavigateBack)

            // Search & Refresh
            is SearchAttendanceAction.UpdateSearchQuery -> updateState { it.copy(searchQuery = action.query) }
            SearchAttendanceAction.Search -> loadAttendances()
            SearchAttendanceAction.Refresh -> refreshAttendances()

            // Filters
            is SearchAttendanceAction.SelectDate -> updateState { it.copy(selectedDate = action.date) }
            is SearchAttendanceAction.ToggleBranch -> toggleBranch(action.branch)
            is SearchAttendanceAction.ToggleSemester -> toggleSemester(action.semester)
            is SearchAttendanceAction.UpdateAcademicStartYear -> {
                updateState { it.copy(academicStartYear = action.year) }
                loadDivisionsAndBatches()
                fetchCoursesForSelectedSemester()
            }
            is SearchAttendanceAction.UpdateAcademicEndYear -> {
                updateState { it.copy(academicEndYear = action.year) }
                loadDivisionsAndBatches()
                fetchCoursesForSelectedSemester()
            }
            is SearchAttendanceAction.SelectDivision -> updateState { it.copy(selectedDivision = action.division) }
            is SearchAttendanceAction.SelectBatch -> updateState { it.copy(selectedBatch = action.batch) }
            is SearchAttendanceAction.SelectCourse -> updateState { it.copy(selectedCourse = action.course) }

            SearchAttendanceAction.ResetFilters -> resetFilters()
            SearchAttendanceAction.ApplyFilters -> applyFilters()

            // Attendance Actions
            is SearchAttendanceAction.AttendanceCardClick -> sendEvent(
                SearchAttendanceEvent.NavigateToAttendanceDetails(action.attendanceId)
            )

            // Pagination
            SearchAttendanceAction.LoadMoreAttendances -> loadMoreAttendances()
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            branchRepository.getBranches()
                .onSuccess { branchList ->
                    updateState {
                        it.copy(
                            branchOptions = branchList.toPersistentList(),
                            areBranchesLoading = false
                        )
                    }
                }
                .onError { _ ->
                    updateState { it.copy(areBranchesLoading = false) }
                }
        }
        state.routeCourseId?.let { courseId ->
            updateState {
                it.copy(isRouteCourseLoading = true)
            }
            viewModelScope.launch {
                courseRepository.getCourseById(courseId)
                    .onSuccess { course ->
                        updateState {
                            it.copy(
                                selectedCourse = course,
                                isRouteCourseLoading = false
                            )
                        }
                    }
                    .onError {
                        updateState { it.copy(isRouteCourseLoading = false) }
                    }
            }
        }
        // Initial load with empty filters
        loadAttendances()
    }

    @OptIn(FlowPreview::class)
    private fun observeSearchQuery() {
        viewModelScope.launch {
            stateFlow.map { it.searchQuery }
                .distinctUntilChanged()
                .debounce(300)
                .collectLatest {
                    loadAttendances()
                }
        }
    }

    private fun loadAttendances() {
        updateState {
            it.copy(
                attendances = persistentListOf(),
                currentPage = 1,
                isLoadingAttendances = true
            )
        }
        paginator.reset()
        viewModelScope.launch {
            paginator.loadNextItems()
        }
    }

    private fun refreshAttendances() {
        updateState { it.copy(isRefreshing = true) }
        loadAttendances()
        updateState { it.copy(isRefreshing = false) }
    }

    private fun loadMoreAttendances() {
        viewModelScope.launch {
            paginator.loadNextItems()
        }
    }

    private fun toggleBranch(branch: edu.watumull.presencify.core.domain.model.academics.Branch) {
        updateState {
            val newSelection = if (it.selectedBranch == branch) null else branch
            it.copy(selectedBranch = newSelection)
        }
    }

    private fun toggleSemester(semester: edu.watumull.presencify.core.domain.enums.SemesterNumber) {
        updateState {
            val newSelection = if (it.selectedSemesters.contains(semester)) {
                it.selectedSemesters.remove(semester)
            } else {
                it.selectedSemesters.add(semester)
            }
            it.copy(selectedSemesters = newSelection)
        }
        // Load divisions, batches, and courses when semester is selected
        loadDivisionsAndBatches()
        fetchCoursesForSelectedSemester()
    }


    private fun loadDivisionsAndBatches() {
        val state = stateFlow.value
        val selectedSemester = state.selectedSemesters.firstOrNull()
        val startYear = state.academicStartYear.toIntOrNull()
        val endYear = state.academicEndYear.toIntOrNull()

        // Early return if required filters are not selected
        if (selectedSemester == null || startYear == null || endYear == null) {
            updateState {
                it.copy(
                    divisionOptions = persistentListOf(),
                    batchOptions = persistentListOf(),
                    selectedDivision = null,
                    selectedBatch = null
                )
            }
            return
        }

        updateState { it.copy(areDivisionsLoading = true, areBatchesLoading = true) }

        viewModelScope.launch {
            divisionRepository.getDivisions(
                branchId = state.selectedBranch?.id,
                semesterNumber = state.selectedSemesters.firstOrNull(),
                academicStartYear = startYear,
                academicEndYear = endYear,
                getAll = true
            ).onSuccess { divisionResult ->
                updateState {
                    it.copy(
                        divisionOptions = divisionResult.divisions.toPersistentList(),
                        areDivisionsLoading = false
                    )
                }
            }.onError { _ ->
                updateState { it.copy(areDivisionsLoading = false) }
            }

            batchRepository.getBatches(
                branchId = state.selectedBranch?.id,
                semesterNumber = state.selectedSemesters.firstOrNull(),
                academicStartYear = startYear,
                academicEndYear = endYear,
                getAll = true
            ).onSuccess { batchResult ->
                updateState {
                    it.copy(
                        batchOptions = batchResult.batches.toPersistentList(),
                        areBatchesLoading = false
                    )
                }
            }.onError { _ ->
                updateState { it.copy(areBatchesLoading = false) }
            }
        }
    }

    private fun fetchCoursesForSelectedSemester() {
        val state = stateFlow.value
        val selectedSemester = state.selectedSemesters.firstOrNull()
        val startYear = state.academicStartYear.toIntOrNull()
        val endYear = state.academicEndYear.toIntOrNull()

        if (selectedSemester == null || startYear == null || endYear == null) {
            updateState { it.copy(courseOptions = persistentListOf(), selectedCourse = null) }
            return
        }

        updateState { it.copy(areCoursesLoading = true) }

        viewModelScope.launch {
            // First get semester ID
            semesterRepository.getSemesters(
                semesterNumber = selectedSemester,
                academicStartYear = startYear,
                academicEndYear = endYear,
                branchId = state.selectedBranch?.id,
                schemeId = null,
                getAll = true
            ).onSuccess { semesterList ->
                val semester = semesterList.semesters.firstOrNull()
                if (semester != null) {
                    // Fetch courses for this semester
                    semesterRepository.getCoursesOfSemester(semester.id)
                        .onSuccess { courses ->
                            updateState {
                                it.copy(
                                    courseOptions = courses.toPersistentList(),
                                    areCoursesLoading = false
                                )
                            }
                        }
                        .onError {
                            updateState {
                                it.copy(
                                    courseOptions = persistentListOf(),
                                    areCoursesLoading = false
                                )
                            }
                        }
                } else {
                    updateState {
                        it.copy(
                            courseOptions = persistentListOf(),
                            areCoursesLoading = false
                        )
                    }
                }
            }.onError {
                updateState {
                    it.copy(
                        courseOptions = persistentListOf(),
                        areCoursesLoading = false
                    )
                }
            }
        }
    }

    private fun resetFilters() {
        updateState {
            it.copy(
                selectedDate = null,
                selectedBranch = null,
                selectedSemesters = persistentListOf(),
                academicStartYear = "",
                academicEndYear = "",
                selectedDivision = null,
                selectedBatch = null,
                selectedCourse = null,
                divisionOptions = persistentListOf(),
                batchOptions = persistentListOf(),
                courseOptions = persistentListOf()
            )
        }
    }

    private fun applyFilters() {
        loadAttendances()
    }
}
