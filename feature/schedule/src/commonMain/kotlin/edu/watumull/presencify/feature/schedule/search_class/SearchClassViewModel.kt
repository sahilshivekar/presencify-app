package edu.watumull.presencify.feature.schedule.search_class

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import edu.watumull.presencify.core.domain.onError
import edu.watumull.presencify.core.domain.onSuccess
import edu.watumull.presencify.core.domain.repository.academics.BatchRepository
import edu.watumull.presencify.core.domain.repository.academics.BranchRepository
import edu.watumull.presencify.core.domain.repository.academics.DivisionRepository
import edu.watumull.presencify.core.domain.repository.schedule.ClassSessionRepository
import edu.watumull.presencify.core.domain.repository.schedule.RoomRepository
import edu.watumull.presencify.core.domain.repository.teacher.TeacherRepository
import edu.watumull.presencify.core.presentation.UiText
import edu.watumull.presencify.core.presentation.pagination.Paginator
import edu.watumull.presencify.core.presentation.toUiText
import edu.watumull.presencify.core.presentation.utils.BaseViewModel
import edu.watumull.presencify.feature.schedule.navigation.ScheduleRoutes
import edu.watumull.presencify.feature.schedule.navigation.SearchClassIntention
import edu.watumull.presencify.feature.schedule.search_class.SearchClassEvent.NavigateBack
import edu.watumull.presencify.feature.schedule.search_class.SearchClassEvent.NavigateToClassDetails
import edu.watumull.presencify.feature.schedule.search_class.SearchClassEvent.NavigateToCreateAttendanceSheet
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
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

class SearchClassViewModel(
    private val classSessionRepository: ClassSessionRepository,
    private val roomRepository: RoomRepository,
    private val teacherRepository: TeacherRepository,
    private val branchRepository: BranchRepository,
    private val divisionRepository: DivisionRepository,
    private val batchRepository: BatchRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<SearchClassState, SearchClassEvent, SearchClassAction>(
    initialState = SearchClassState(
        intention = runCatching {
            SearchClassIntention.valueOf(
                savedStateHandle.toRoute<ScheduleRoutes.SearchClass>().intention
            )
        }.getOrDefault(SearchClassIntention.DEFAULT)
    )
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

            classSessionRepository.getClasses(
                searchQuery = state.searchQuery.ifBlank { null },
                courseType = state.selectedCourseTypes.firstOrNull(),
                isExtraClass = state.isExtraClass,
                startTime = state.startTime,
                endTime = state.endTime,
                activeFrom = state.activeFrom,
                activeTill = state.activeTill,
                roomId = state.selectedRoomIds.firstOrNull(),
                teacherId = state.selectedTeacherIds.firstOrNull(),
                branchId = state.selectedBranches.firstOrNull()?.id,
                semesterNumber = state.selectedSemesters.firstOrNull()?.value,
                academicStartYearOfSemester = startYear,
                academicEndYearOfSemester = endYear,
                divisionId = state.selectedDivision?.id,
                batchId = state.selectedBatch?.id,
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
                    viewState = SearchClassState.ViewState.Error(error.toUiText()),
                    isRefreshing = false,
                    isLoadingClasses = false
                )
            }
        },
        onSuccess = { response, _ ->
            updateState {
                it.copy(
                    classes = if (stateFlow.value.currentPage == 1) response.classes.toPersistentList() else it.classes.addAll(
                        response.classes.toPersistentList()
                    ),
                    currentPage = stateFlow.value.currentPage + 1,
                    isRefreshing = false,
                    isLoadingClasses = false
                )
            }
        },
        endReached = { currentPage, response ->
            val totalLoadedClasses = currentPage * 20
            totalLoadedClasses >= response.totalCount
        }
    )

    init {
        viewModelScope.launch {
            val task1 = async { loadBranches() }
            val task2 = async { loadRoomsAndTeachers() }
            awaitAll(task1, task2)
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
                        viewState = SearchClassState.ViewState.Error(error.toUiText())
                    )
                }
            }
    }

    private suspend fun loadDivisionsAndBatches() {
        val state = stateFlow.value

        val semester = state.selectedSemesters.firstOrNull()
        val branchId = state.selectedBranches.firstOrNull()?.id
        val startYear = state.academicStartYear.toIntOrNull()
        val endYear = state.academicEndYear.toIntOrNull()

        if (semester != null && branchId != null && startYear != null && endYear != null && state.academicStartYearError == null && state.academicEndYearError == null) {

            updateState { it.copy(areDivisionsLoading = true) }
            divisionRepository.getDivisions(
                semesterNumber = semester,
                branchId = branchId,
                academicStartYear = startYear,
                academicEndYear = endYear,
                searchQuery = null,
                getAll = true
            )
                .onSuccess { divisionsWithTotalCount ->
                    updateState {
                        it.copy(
                            divisionOptions = divisionsWithTotalCount.divisions.toPersistentList(),
                            areDivisionsLoading = false
                        )
                    }
                }
                .onError { error ->
                    updateState {
                        it.copy(
                            areDivisionsLoading = false,
                            viewState = SearchClassState.ViewState.Error(error.toUiText())
                        )
                    }
                }

            updateState { it.copy(areBatchesLoading = true) }
            batchRepository.getBatches(
                semesterNumber = semester,
                branchId = branchId,
                academicStartYear = startYear,
                academicEndYear = endYear,
                searchQuery = null,
                getAll = true
            )
                .onSuccess { batchesWithTotalCount ->
                    updateState {
                        it.copy(
                            batchOptions = batchesWithTotalCount.batches.toPersistentList(),
                            areBatchesLoading = false
                        )
                    }
                }
                .onError { error ->
                    updateState {
                        it.copy(
                            areBatchesLoading = false,
                            viewState = SearchClassState.ViewState.Error(error.toUiText())
                        )
                    }
                }
        } else {
            updateState {
                it.copy(
                    divisionOptions = persistentListOf(),
                    batchOptions = persistentListOf(),
                    selectedDivision = null,
                    selectedBatch = null,
                    areDivisionsLoading = false,
                    areBatchesLoading = false
                )
            }
        }
    }

    private fun loadRoomsAndTeachers() {
        viewModelScope.launch {
            updateState { it.copy(isLoadingRooms = true, isLoadingTeachers = true) }

            val roomsResult = roomRepository.getRooms(getAll = true)
            when (roomsResult) {
                is edu.watumull.presencify.core.domain.Result.Success -> {
                    updateState {
                        it.copy(
                            availableRooms = roomsResult.data.rooms.toPersistentList(),
                            isLoadingRooms = false
                        )
                    }
                }
                is edu.watumull.presencify.core.domain.Result.Error -> {
                    updateState { it.copy(isLoadingRooms = false) }
                }
            }

            val teachersResult = teacherRepository.getTeachers(getAll = true, isActive = true)
            when (teachersResult) {
                is edu.watumull.presencify.core.domain.Result.Success -> {
                    updateState {
                        it.copy(
                            availableTeachers = teachersResult.data.teachers.toPersistentList(),
                            isLoadingTeachers = false
                        )
                    }
                }
                is edu.watumull.presencify.core.domain.Result.Error -> {
                    updateState { it.copy(isLoadingTeachers = false) }
                }
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

    private fun loadNextClasses() {
        viewModelScope.launch {
            paginator.loadNextItems()
        }
    }

    private fun refreshSearch() {
        updateState { it.copy(classes = persistentListOf(), currentPage = 1) }
        paginator.reset()
        loadNextClasses()
    }

    private fun validateAcademicYears(startYear: String, endYear: String): Pair<String?, String?> {
        var startError: String? = null
        var endError: String? = null

        val startInt = startYear.toIntOrNull()
        val endInt = endYear.toIntOrNull()

        if (startYear.isNotBlank()) {
            if (startInt == null) startError = "Must be a valid number"
            else if (startInt < 2000) startError = "Must be 2000 or later"
        }

        if (endYear.isNotBlank()) {
            if (endInt == null) endError = "Must be a valid number"
            else if (endInt < 2000) endError = "Must be 2000 or later"
        }

        if (startYear.isNotBlank() && endYear.isBlank()) {
            endError = "End year is required"
        } else if (endYear.isNotBlank() && startYear.isBlank()) {
            startError = "Start year is required"
        }

        if (startError == null && endError == null && startInt != null && endInt != null) {
            if (startInt >= endInt) {
                startError = "Must be before end year"
                endError = "Must be after start year"
            }
        }

        return Pair(startError, endError)
    }

    private fun validateTimeRange(startTime: LocalTime?, endTime: LocalTime?): Pair<String?, String?> {
        var startError: String? = null
        var endError: String? = null

        if (startTime != null && endTime == null) {
            endError = "End time is required"
        } else if (endTime != null && startTime == null) {
            startError = "Start time is required"
        } else if (startTime != null && endTime != null) {
            if (startTime >= endTime) {
                endError = "Must be after start time"
            }
        }

        return Pair(startError, endError)
    }

    private fun validateDateRange(activeFrom: LocalDate?, activeTill: LocalDate?): Pair<String?, String?> {
        var fromError: String? = null
        var tillError: String? = null

        if (activeFrom != null && activeTill == null) {
            tillError = "Active till is required"
        } else if (activeTill != null && activeFrom == null) {
            fromError = "Active from is required"
        } else if (activeFrom != null && activeTill != null) {
            if (activeFrom > activeTill) {
                tillError = "Must be after active from"
            }
        }

        return Pair(fromError, tillError)
    }

    override fun handleAction(action: SearchClassAction) {
        when (action) {
            SearchClassAction.NavigateBack -> {
                sendEvent(NavigateBack)
            }

            is SearchClassAction.UpdateSearchQuery -> {
                updateState { it.copy(searchQuery = action.query) }
            }

            SearchClassAction.Search -> {
                refreshSearch()
            }

            SearchClassAction.Refresh -> {
                updateState { it.copy(isRefreshing = true) }
                refreshSearch()
            }

            is SearchClassAction.ToggleBranch -> {
                val currentBranches = stateFlow.value.selectedBranches
                val newBranches = if (currentBranches.contains(action.branch)) {
                    currentBranches - action.branch
                } else {
                    persistentListOf(action.branch)
                }
                updateState { it.copy(selectedBranches = newBranches.toPersistentList()) }
                viewModelScope.launch {
                    loadDivisionsAndBatches()
                }
            }

            is SearchClassAction.ToggleSemester -> {
                val currentSemesters = stateFlow.value.selectedSemesters
                val newSemesters = if (currentSemesters.contains(action.semester)) {
                    currentSemesters - action.semester
                } else {
                    persistentListOf(action.semester)
                }
                updateState { it.copy(selectedSemesters = newSemesters.toPersistentList()) }
                viewModelScope.launch {
                    loadDivisionsAndBatches()
                }
            }

            is SearchClassAction.UpdateAcademicStartYear -> {
                val startYear = action.year
                val endYear = stateFlow.value.academicEndYear
                val (startError, endError) = validateAcademicYears(startYear, endYear)

                updateState {
                    it.copy(
                        academicStartYear = startYear,
                        academicStartYearError = startError,
                        academicEndYearError = endError
                    )
                }
                viewModelScope.launch {
                    loadDivisionsAndBatches()
                }
            }

            is SearchClassAction.UpdateAcademicEndYear -> {
                val startYear = stateFlow.value.academicStartYear
                val endYear = action.year
                val (startError, endError) = validateAcademicYears(startYear, endYear)

                updateState {
                    it.copy(
                        academicEndYear = endYear,
                        academicStartYearError = startError,
                        academicEndYearError = endError
                    )
                }
                viewModelScope.launch {
                    loadDivisionsAndBatches()
                }
            }

            is SearchClassAction.SelectDivision -> {
                updateState { it.copy(selectedDivision = action.division) }
            }

            is SearchClassAction.SelectBatch -> {
                updateState { it.copy(selectedBatch = action.batch) }
            }

            is SearchClassAction.ToggleCourseType -> {
                updateState {
                    val currentTypes = it.selectedCourseTypes
                    val newTypes = if (currentTypes.contains(action.courseType)) {
                        currentTypes.remove(action.courseType)
                    } else {
                        persistentListOf(action.courseType)
                    }
                    it.copy(selectedCourseTypes = newTypes)
                }
            }

            is SearchClassAction.ToggleRoom -> {
                updateState {
                    val currentRooms = it.selectedRoomIds
                    val newRooms = if (currentRooms.contains(action.roomId)) {
                        currentRooms.remove(action.roomId)
                    } else {
                        persistentListOf(action.roomId)
                    }
                    it.copy(selectedRoomIds = newRooms)
                }
            }

            is SearchClassAction.ToggleTeacher -> {
                updateState {
                    val currentTeachers = it.selectedTeacherIds
                    val newTeachers = if (currentTeachers.contains(action.teacherId)) {
                        currentTeachers.remove(action.teacherId)
                    } else {
                        persistentListOf(action.teacherId)
                    }
                    it.copy(selectedTeacherIds = newTeachers)
                }
            }

            is SearchClassAction.UpdateIsExtraClass -> {
                updateState { it.copy(isExtraClass = action.isExtraClass) }
            }

            is SearchClassAction.UpdateStartTime -> {
                val startTime = action.time
                val endTime = stateFlow.value.endTime
                val (startError, endError) = validateTimeRange(startTime, endTime)

                updateState {
                    it.copy(
                        startTime = startTime,
                        startTimeError = startError,
                        endTimeError = endError
                    )
                }
            }

            is SearchClassAction.UpdateEndTime -> {
                val startTime = stateFlow.value.startTime
                val endTime = action.time
                val (startError, endError) = validateTimeRange(startTime, endTime)

                updateState {
                    it.copy(
                        endTime = endTime,
                        startTimeError = startError,
                        endTimeError = endError
                    )
                }
            }

            is SearchClassAction.UpdateActiveFrom -> {
                val activeFrom = action.date
                val activeTill = stateFlow.value.activeTill
                val (fromError, tillError) = validateDateRange(activeFrom, activeTill)

                updateState {
                    it.copy(
                        activeFrom = activeFrom,
                        activeFromError = fromError,
                        activeTillError = tillError
                    )
                }
            }

            is SearchClassAction.UpdateActiveTill -> {
                val activeFrom = stateFlow.value.activeFrom
                val activeTill = action.date
                val (fromError, tillError) = validateDateRange(activeFrom, activeTill)

                updateState {
                    it.copy(
                        activeTill = activeTill,
                        activeFromError = fromError,
                        activeTillError = tillError
                    )
                }
            }

            SearchClassAction.ResetFilters -> {
                updateState {
                    it.copy(
                        selectedBranches = persistentListOf(),
                        selectedSemesters = persistentListOf(),
                        academicStartYear = "",
                        academicEndYear = "",
                        academicStartYearError = null,
                        academicEndYearError = null,
                        selectedDivision = null,
                        selectedBatch = null,
                        selectedCourseTypes = persistentListOf(),
                        selectedRoomIds = persistentListOf(),
                        selectedTeacherIds = persistentListOf(),
                        isExtraClass = null,
                        startTime = null,
                        endTime = null,
                        startTimeError = null,
                        endTimeError = null,
                        activeFrom = null,
                        activeTill = null,
                        activeFromError = null,
                        activeTillError = null
                    )
                }
            }

            SearchClassAction.ApplyFilters -> {
                refreshSearch()
            }

            is SearchClassAction.ClassCardClick -> {
                when (stateFlow.value.intention) {
                    SearchClassIntention.DEFAULT -> {
                        sendEvent(NavigateToClassDetails(action.classId))
                    }
                    SearchClassIntention.CREATE_ATTENDANCE_SHEET -> {
                        sendEvent(NavigateToCreateAttendanceSheet(action.classId))
                    }
                }
            }

            SearchClassAction.LoadMoreClasses -> {
                loadNextClasses()
            }
        }
    }
}