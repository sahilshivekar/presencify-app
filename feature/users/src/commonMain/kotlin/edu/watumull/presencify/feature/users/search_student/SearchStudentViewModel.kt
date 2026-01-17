package edu.watumull.presencify.feature.users.search_student

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import edu.watumull.presencify.core.design.systems.components.dialog.DialogType
import edu.watumull.presencify.core.domain.onError
import edu.watumull.presencify.core.domain.onSuccess
import edu.watumull.presencify.core.domain.repository.academics.BatchRepository
import edu.watumull.presencify.core.domain.repository.academics.BranchRepository
import edu.watumull.presencify.core.domain.repository.academics.DivisionRepository
import edu.watumull.presencify.core.domain.repository.academics.SchemeRepository
import edu.watumull.presencify.core.domain.repository.student.StudentRepository
import edu.watumull.presencify.core.presentation.pagination.Paginator
import edu.watumull.presencify.core.presentation.toUiText
import edu.watumull.presencify.core.presentation.utils.BaseViewModel
import edu.watumull.presencify.feature.users.navigation.SearchStudentIntention
import edu.watumull.presencify.feature.users.navigation.UsersRoutes
import edu.watumull.presencify.feature.users.search_student.SearchStudentEvent.*
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SearchStudentViewModel(
    private val studentRepository: StudentRepository,
    private val branchRepository: BranchRepository,
    private val schemeRepository: SchemeRepository,
    private val divisionRepository: DivisionRepository,
    private val batchRepository: BatchRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<SearchStudentState, SearchStudentEvent, SearchStudentAction>(
    initialState = SearchStudentState()
) {

    private val routeParams = savedStateHandle.toRoute<UsersRoutes.SearchStudent>()

    private val paginator = Paginator<Int, edu.watumull.presencify.core.domain.model.student.StudentListWithTotalCount>(
        initialKey = 1,
        onLoadUpdated = { isLoading ->
            updateState { it.copy(isLoadingMore = isLoading) }
        },
        onRequest = { page ->
            val state = stateFlow.value
            val dropoutYears = state.selectedDropoutYear?.split("-")?.map { it.trim().toInt() }
            val academicYears = state.selectedAcademicYearOfSemester?.split("-")?.map { it.trim().toInt() }

            studentRepository.getStudents(
                searchQuery = state.searchQuery.ifBlank { null },
                branchIds = state.selectedBranches.map { it.id }.ifEmpty { null },
                semesterNumbers = state.selectedSemesters.map { it.value }.ifEmpty { null },
                academicStartYearOfSemester = academicYears?.getOrNull(0),
                academicEndYearOfSemester = academicYears?.getOrNull(1),
                admissionTypes = state.selectedAdmissionTypes.ifEmpty { null },
                admissionYear = state.selectedAdmissionYear?.toIntOrNull(),
                dropoutAcademicStartYear = dropoutYears?.getOrNull(0),
                dropoutAcademicEndYear = dropoutYears?.getOrNull(1),
                schemeId = state.selectedScheme?.id,
                divisionId = state.selectedDivision?.id,
                batchId = state.selectedBatch?.id,
                currentBatch = routeParams.currentBatch,
                currentDivision = routeParams.currentDivision,
                currentSemester = routeParams.currentSemester,
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
                    dialogState = SearchStudentState.DialogState(
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
                    students = if (stateFlow.value.currentPage == 1) response.students.toPersistentList() else it.students.addAll(
                        response.students.toPersistentList()
                    ),
                    currentPage = stateFlow.value.currentPage + 1,
                    isRefreshing = false,
                    isLoadingStudents = false
                )
            }
        },
        endReached = { currentPage, response ->
            // End reached when we have loaded all students
            val totalLoadedStudents = currentPage * 20
            totalLoadedStudents >= response.totalStudents
        }
    )

    init {
        // Set intention from route params
        val intention = try {
            SearchStudentIntention.valueOf(routeParams.intention)
        } catch (_: IllegalArgumentException) {
            SearchStudentIntention.DEFAULT
        }

        val isSelectable = intention != SearchStudentIntention.DEFAULT

        updateState {
            it.copy(
                intention = intention,
                isSelectable = isSelectable,
                searchQuery = routeParams.searchQuery ?: ""
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

    private fun loadNextStudents() {
        viewModelScope.launch {
            paginator.loadNextItems()
        }
    }

    private fun refreshSearch() {
        updateState { it.copy(students = persistentListOf(), currentPage = 1) }
        paginator.reset()
        loadNextStudents()
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
                        dialogState = SearchStudentState.DialogState(
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
                        dialogState = SearchStudentState.DialogState(
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
        val academicYear = state.selectedAcademicYearOfSemester

        if (semester != null && branchId != null && academicYear != null) {
            val years = academicYear.split("-").map { it.trim().toInt() }
            val startYear = years[0]
            val endYear = years[1]

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
                            dialogState = SearchStudentState.DialogState(
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
                            dialogState = SearchStudentState.DialogState(
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


    override fun handleAction(action: SearchStudentAction) {
        when (action) {

            is SearchStudentAction.BackButtonClick -> {
                sendEvent(NavigateBack)
            }

            is SearchStudentAction.DismissDialog -> {
                updateState { it.copy(dialogState = null) }
            }

            is SearchStudentAction.UpdateSearchQuery -> {
                updateState { it.copy(searchQuery = action.query) }
            }

            is SearchStudentAction.Search -> {
                refreshSearch()
            }

            is SearchStudentAction.Refresh -> {
                updateState { it.copy(isRefreshing = true) }
                refreshSearch()
            }


            is SearchStudentAction.ToggleBranch -> {
                val currentBranches = stateFlow.value.selectedBranches
                val newBranches = if (currentBranches.contains(action.branch)) {
                    currentBranches - action.branch
                } else {
                    currentBranches + action.branch
                }
                updateState { it.copy(selectedBranches = newBranches.toPersistentList()) }
                viewModelScope.launch {
                    loadDivisionsAndBatches()
                }
            }

            is SearchStudentAction.ToggleSemester -> {
                val currentSemesters = stateFlow.value.selectedSemesters
                val newSemesters = if (currentSemesters.contains(action.semester)) {
                    currentSemesters - action.semester
                } else {
                    currentSemesters + action.semester
                }
                updateState { it.copy(selectedSemesters = newSemesters.toPersistentList()) }
                viewModelScope.launch {
                    loadDivisionsAndBatches()
                }
            }

            is SearchStudentAction.SelectAcademicYearOfSemester -> {
                updateState { it.copy(selectedAcademicYearOfSemester = action.year) }
                viewModelScope.launch {
                    loadDivisionsAndBatches()
                }
            }

            is SearchStudentAction.ToggleAdmissionType -> {
                val currentTypes = stateFlow.value.selectedAdmissionTypes
                val newTypes = if (currentTypes.contains(action.admissionType)) {
                    currentTypes - action.admissionType
                } else {
                    currentTypes + action.admissionType
                }
                updateState { it.copy(selectedAdmissionTypes = newTypes.toPersistentList()) }
            }

            is SearchStudentAction.SelectAdmissionYear -> {
                updateState { it.copy(selectedAdmissionYear = action.year) }
            }

            is SearchStudentAction.SelectDropoutYear -> {
                updateState { it.copy(selectedDropoutYear = action.year) }
            }

            is SearchStudentAction.SelectScheme -> {
                updateState { it.copy(selectedScheme = action.scheme) }
            }

            is SearchStudentAction.SelectDivision -> {
                updateState { it.copy(selectedDivision = action.division) }
            }

            is SearchStudentAction.SelectBatch -> {
                updateState { it.copy(selectedBatch = action.batch) }
            }

            is SearchStudentAction.ResetFilters -> {
                updateState {
                    it.copy(
                        selectedBranches = persistentListOf(),
                        selectedSemesters = persistentListOf(),
                        selectedAcademicYearOfSemester = null,
                        selectedAdmissionTypes = persistentListOf(),
                        selectedAdmissionYear = null,
                        selectedDropoutYear = null,
                        selectedScheme = null,
                        selectedDivision = null,
                        selectedBatch = null
                    )
                }
            }

            is SearchStudentAction.ApplyFilters -> {
                refreshSearch()
            }

            is SearchStudentAction.ToggleStudentSelection -> {
                val currentSelections = stateFlow.value.selectedStudentIds
                val newSelections = if (currentSelections.contains(action.studentId)) {
                    currentSelections - action.studentId
                } else {
                    currentSelections + action.studentId
                }
                updateState { it.copy(selectedStudentIds = newSelections) }
            }

            is SearchStudentAction.StudentCardClick -> {
                if (stateFlow.value.isSelectable) {
                    // In selection mode, toggle selection
                    val currentSelections = stateFlow.value.selectedStudentIds
                    val newSelections = if (currentSelections.contains(action.studentId)) {
                        currentSelections - action.studentId
                    } else {
                        currentSelections + action.studentId
                    }
                    updateState { it.copy(selectedStudentIds = newSelections) }
                } else {
                    // Navigate to student details
                    sendEvent(NavigateToStudentDetails(action.studentId))
                }
            }

            is SearchStudentAction.LoadMoreStudents -> {
                loadNextStudents()
            }

            is SearchStudentAction.DoneButtonClick -> {
                sendEvent(NavigateBackWithSelection)
            }

            SearchStudentAction.HideBottomSheet -> {
                updateState { it.copy(isBottomSheetVisible = false) }
            }

            SearchStudentAction.ShowBottomSheet -> {
                updateState {
                    it.copy(isBottomSheetVisible = true)
                }
            }

            SearchStudentAction.ClickFloatingActionButton -> {
                sendEvent(NavigateToAddEditStudent)
            }
        }
    }
}


