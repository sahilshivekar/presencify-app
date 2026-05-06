package edu.watumull.presencify.feature.schedule.search_class

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import edu.watumull.presencify.core.designsystem.components.dialog.DialogType
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
                classType = state.selectedClassTypes.firstOrNull(),
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
                    dialogState = SearchClassState.DialogState(
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
                        dialogState = SearchClassState.DialogState(
                            dialogType = DialogType.ERROR,
                            title = "Error",
                            message = error.toUiText(),
                            dialogIntention = DialogIntention.GENERIC
                        )
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
                            dialogState = SearchClassState.DialogState(
                                dialogType = DialogType.ERROR,
                                title = "Error",
                                message = error.toUiText(),
                                dialogIntention = DialogIntention.GENERIC
                            )
                        )
                    }
                }

            // Load batches
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
                            dialogState = SearchClassState.DialogState(
                                dialogType = DialogType.ERROR,
                                title = "Error",
                                message = error.toUiText(),
                                dialogIntention = DialogIntention.GENERIC
                            )
                        )
                    }
                }
        } else {
            // Clear divisions and batches if prerequisites are not met
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

            // Load all rooms
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

            // Load all teachers
            val teachersResult = teacherRepository.getTeachers(getAll = true)
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

    private fun validateFilters(): Boolean {
        val state = stateFlow.value
        var isValid: Boolean

        // Validate time range
        var startTimeError: String? = null
        var endTimeError: String? = null

        if (state.startTime != null && state.endTime == null) {
            endTimeError = "End time is required when start time is selected"
        } else if (state.startTime == null && state.endTime != null) {
            startTimeError = "Start time is required when end time is selected"
        } else if (state.startTime != null && state.endTime != null) {
            if (state.startTime >= state.endTime) {
                endTimeError = "End time must be after start time"
            }
        }

        // Validate date range
        var activeFromError: String? = null
        var activeTillError: String? = null

        if (state.activeFrom != null && state.activeTill == null) {
            activeTillError = "Active till date is required when active from date is selected"
        } else if (state.activeFrom == null && state.activeTill != null) {
            activeFromError = "Active from date is required when active till date is selected"
        } else if (state.activeFrom != null && state.activeTill != null) {
            if (state.activeFrom > state.activeTill) {
                activeTillError = "Active till date must be after active from date"
            }
        }

        updateState {
            it.copy(
                startTimeError = startTimeError,
                endTimeError = endTimeError,
                activeFromError = activeFromError,
                activeTillError = activeTillError
            )
        }

        isValid = startTimeError == null && endTimeError == null &&
                  activeFromError == null && activeTillError == null

        if (!isValid) {
            val errorMessage = listOfNotNull(
                startTimeError, endTimeError, activeFromError, activeTillError
            ).firstOrNull() ?: "Invalid filter values"

            updateState {
                it.copy(
                    dialogState = SearchClassState.DialogState(
                        dialogType = DialogType.ERROR,
                        title = "Validation Error",
                        message = UiText.DynamicString(errorMessage),
                        dialogIntention = DialogIntention.GENERIC
                    )
                )
            }
        }

        return isValid
    }

    override fun handleAction(action: SearchClassAction) {
        when (action) {
            SearchClassAction.BackButtonClick -> {
                sendEvent(NavigateBack)
            }

            SearchClassAction.DismissDialog -> {
                updateState { it.copy(dialogState = null) }
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
                    // Server accepts only single value, so clear others
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
                    // Server accepts only single value, so clear others
                    persistentListOf(action.semester)
                }
                updateState { it.copy(selectedSemesters = newSemesters.toPersistentList()) }
                viewModelScope.launch {
                    loadDivisionsAndBatches()
                }
            }

            is SearchClassAction.UpdateAcademicStartYear -> {
                updateState { it.copy(academicStartYear = action.year) }
                viewModelScope.launch {
                    loadDivisionsAndBatches()
                }
            }

            is SearchClassAction.UpdateAcademicEndYear -> {
                updateState { it.copy(academicEndYear = action.year) }
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

            is SearchClassAction.ToggleClassType -> {
                updateState {
                    val currentTypes = it.selectedClassTypes
                    val newTypes = if (currentTypes.contains(action.classType)) {
                        currentTypes.remove(action.classType)
                    } else {
                        // Server accepts only single value, so clear others
                        persistentListOf(action.classType)
                    }
                    it.copy(selectedClassTypes = newTypes)
                }
            }

            is SearchClassAction.ToggleRoom -> {
                updateState {
                    val currentRooms = it.selectedRoomIds
                    val newRooms = if (currentRooms.contains(action.roomId)) {
                        currentRooms.remove(action.roomId)
                    } else {
                        // Server accepts only single value, so clear others
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
                        // Server accepts only single value, so clear others
                        persistentListOf(action.teacherId)
                    }
                    it.copy(selectedTeacherIds = newTeachers)
                }
            }

            is SearchClassAction.UpdateIsExtraClass -> {
                updateState { it.copy(isExtraClass = action.isExtraClass) }
            }

            is SearchClassAction.UpdateStartTime -> {
                updateState {
                    it.copy(
                        startTime = action.time,
                        startTimeError = null
                    )
                }
            }

            is SearchClassAction.UpdateEndTime -> {
                updateState {
                    it.copy(
                        endTime = action.time,
                        endTimeError = null
                    )
                }
            }

            is SearchClassAction.UpdateActiveFrom -> {
                updateState {
                    it.copy(
                        activeFrom = action.date,
                        activeFromError = null
                    )
                }
            }

            is SearchClassAction.UpdateActiveTill -> {
                updateState {
                    it.copy(
                        activeTill = action.date,
                        activeTillError = null
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
                        selectedDivision = null,
                        selectedBatch = null,
                        selectedClassTypes = persistentListOf(),
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
                if (validateFilters()) {
                    refreshSearch()
                }
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
