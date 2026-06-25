package edu.watumull.presencify.feature.users.search_student

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import edu.watumull.presencify.core.domain.enums.SemesterNumber
import edu.watumull.presencify.core.domain.onError
import edu.watumull.presencify.core.domain.onSuccess
import edu.watumull.presencify.core.domain.repository.academics.BatchRepository
import edu.watumull.presencify.core.domain.repository.academics.BranchRepository
import edu.watumull.presencify.core.domain.repository.academics.DivisionRepository
import edu.watumull.presencify.core.domain.repository.academics.SchemeRepository
import edu.watumull.presencify.core.domain.repository.academics.SemesterRepository
import edu.watumull.presencify.core.domain.repository.student.StudentDropoutRepository
import edu.watumull.presencify.core.domain.repository.student.StudentRepository
import edu.watumull.presencify.core.presentation.UiText
import edu.watumull.presencify.core.presentation.components.ListItemFeedback
import edu.watumull.presencify.core.presentation.global_snackbar.SnackbarController
import edu.watumull.presencify.core.presentation.global_snackbar.SnackbarEvent
import edu.watumull.presencify.core.presentation.pagination.Paginator
import edu.watumull.presencify.core.presentation.toUiText
import edu.watumull.presencify.core.presentation.utils.BaseViewModel
import edu.watumull.presencify.feature.users.navigation.SearchStudentIntention
import edu.watumull.presencify.feature.users.navigation.UsersRoutes
import edu.watumull.presencify.feature.users.search_student.SearchStudentEvent.NavigateBack
import edu.watumull.presencify.feature.users.search_student.SearchStudentEvent.NavigateToAddEditStudent
import edu.watumull.presencify.feature.users.search_student.SearchStudentEvent.NavigateToStudentDetails
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

class SearchStudentViewModel(
    private val studentRepository: StudentRepository,
    private val branchRepository: BranchRepository,
    private val schemeRepository: SchemeRepository,
    private val semesterRepository: SemesterRepository,
    private val divisionRepository: DivisionRepository,
    private val batchRepository: BatchRepository,
    private val studentDropoutRepository: StudentDropoutRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<SearchStudentState, SearchStudentEvent, SearchStudentAction>(
    initialState = run {
        val routeParams = savedStateHandle.toRoute<UsersRoutes.SearchStudent>()
        val intention = try {
            SearchStudentIntention.valueOf(routeParams.intention)
        } catch (_: IllegalArgumentException) {
            SearchStudentIntention.DEFAULT
        }

        // Validate required parameters based on intention
        when (intention) {
            SearchStudentIntention.ASSIGN_UNASSIGN_STUDENT_TO_SEMESTER -> {
                if (routeParams.semesterId == null) {
                    throw IllegalArgumentException("Semester ID is required for assigning/unassigning students to semester")
                }
                // Expect branchId
                if (routeParams.branchId == null) {
                    throw IllegalArgumentException("Branch ID is required for assigning/unassigning students to semester")
                }
            }

            SearchStudentIntention.ASSIGN_UNASSIGN_STUDENT_TO_DIVISION,
            SearchStudentIntention.MODIFY_STUDENT_DIVISION -> {
                if (routeParams.divisionId == null) {
                    throw IllegalArgumentException("Division ID is required for division operations")
                }
                // Expect branchId, academicStartYear, academicEndYear, semesterNumber
                if (routeParams.branchId == null || routeParams.academicStartYear == null ||
                    routeParams.academicEndYear == null || routeParams.semesterNumber == null
                ) {
                    throw IllegalArgumentException("Branch ID, academic years, and semester number are required for division operations")
                }
            }

            SearchStudentIntention.ASSIGN_UNASSIGN_STUDENT_TO_BATCH,
            SearchStudentIntention.MODIFY_STUDENT_BATCH -> {
                if (routeParams.batchId == null) {
                    throw IllegalArgumentException("Batch ID is required for batch operations")
                }
                // Expect branchId, academicStartYear, academicEndYear, semesterNumber
                if (routeParams.branchId == null || routeParams.academicStartYear == null ||
                    routeParams.academicEndYear == null || routeParams.semesterNumber == null
                ) {
                    throw IllegalArgumentException("Branch ID, academic years, and semester number are required for batch operations")
                }
            }

            SearchStudentIntention.MARK_UNMARK_STUDENT_AS_DROPOUT -> {
                if (routeParams.dropoutAcademicStartYear == null || routeParams.dropoutAcademicEndYear == null) {
                    throw IllegalArgumentException("Dropout academic years are required for mark/unmark dropout operations")
                }
            }

            SearchStudentIntention.VIEW_ATTENDANCE -> {
                // No validation needed
            }

            SearchStudentIntention.DEFAULT -> {
                // No validation needed
            }

            SearchStudentIntention.ADD_STUDENT_BIOMETRIC -> {
                // No validation needed
            }
        }

        SearchStudentState(
            intention = intention,
            searchQuery = routeParams.searchQuery ?: "",
            semesterId = routeParams.semesterId,
            divisionId = routeParams.divisionId,
            batchId = routeParams.batchId,
            newStartDate = routeParams.newStartDate,
            dropoutAcademicStartYear = routeParams.dropoutAcademicStartYear,
            dropoutAcademicEndYear = routeParams.dropoutAcademicEndYear
        )
    }
) {

    private val routeParams = savedStateHandle.toRoute<UsersRoutes.SearchStudent>()

    private val paginator = Paginator(
        initialKey = 1,
        onLoadUpdated = { isLoading ->
            updateState { it.copy(isLoadingMore = isLoading) }
        },
        onRequest = { page ->
            val state = stateFlow.value
            val academicStartYear = state.academicStartYear.toIntOrNull()
            val academicEndYear = state.academicEndYear.toIntOrNull()
            val dropoutStartYear = state.dropoutStartYear.toIntOrNull()
            val dropoutEndYear = state.dropoutEndYear.toIntOrNull()

            studentRepository.getStudents(
                searchQuery = state.searchQuery.ifBlank { null },
                branchIds = state.selectedBranches.map { it.id }.ifEmpty { null },
                semesterNumbers = state.selectedSemesters.map { it.value }.ifEmpty { null },
                academicStartYearOfSemester = academicStartYear,
                academicEndYearOfSemester = academicEndYear,
                admissionTypes = state.selectedAdmissionTypes.ifEmpty { null },
                admissionYear = state.admissionYear?.toIntOrNull(),
                dropoutAcademicStartYear = dropoutStartYear,
                dropoutAcademicEndYear = dropoutEndYear,
                biometricVerificationStatus = state.selectedBiometricVerificationStatus,
                schemeId = state.selectedScheme?.id,
                divisionId = state.selectedDivision?.id,
                batchId = state.selectedBatch?.id,
                currentBatch = routeParams.currentBatch,
                currentDivision = routeParams.currentDivision,
                currentSemester = routeParams.currentSemester,
                page = page,
                limit = 20,
                intention = routeParams.intention
            )
        },
        getNextKey = { currentPage, _ ->
            currentPage + 1
        },
        onError = { error ->
            updateState {
                it.copy(
                    viewState = SearchStudentState.ViewState.Error(error.toUiText()),
                    isRefreshing = false,
                    isLoadingStudents = false
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
        viewModelScope.launch {
            val task1 = async {loadBranches()}
            val task2 = async {loadSchemes()}
            awaitAll(task1, task2)
            initializeFilterSelections()
            setupDebouncedSearch()
        }
    }

    private suspend fun initializeFilterSelections() {
        if (routeParams.branchId != null) {
            val selectedBranch = stateFlow.value.branchOptions.find { it.id == routeParams.branchId }
            if (selectedBranch != null) {
                updateState { it.copy(selectedBranches = persistentListOf(selectedBranch)) }
            }
        }
        if (routeParams.semesterNumber != null) {
            val semester: SemesterNumber? = SemesterNumber.entries.find { it.value == routeParams.semesterNumber }
            if (semester != null) {
                updateState { it.copy(selectedSemesters = persistentListOf(semester)) }
            }
        }
        if (routeParams.academicStartYear != null && routeParams.academicEndYear != null) {
            updateState {
                it.copy(
                    academicStartYear = routeParams.academicStartYear.toString(),
                    academicEndYear = routeParams.academicEndYear.toString()
                )
            }
        }
        val currentState = stateFlow.value
        if (currentState.selectedSemesters.isNotEmpty() &&
            currentState.selectedBranches.isNotEmpty() &&
            currentState.academicStartYear.isNotEmpty() &&
            currentState.academicEndYear.isNotEmpty()
        ) {
            loadDivisionsAndBatches()
            if (
                routeParams.batchId != null &&
                SearchStudentIntention.valueOf(routeParams.intention) in listOf(
                    SearchStudentIntention.ASSIGN_UNASSIGN_STUDENT_TO_BATCH,
                    SearchStudentIntention.MODIFY_STUDENT_BATCH
                )
            ) {
                val batchDetails = stateFlow.value.batchOptions.find { it.id == routeParams.batchId }
                batchDetails?.divisionId?.let { divisionId ->
                    val selectedDivision = stateFlow.value.divisionOptions.find { it.id == divisionId }
                    updateState { it.copy(selectedDivision = selectedDivision) }
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
                        viewState = SearchStudentState.ViewState.Error(error.toUiText())
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
                        viewState = SearchStudentState.ViewState.Error(error.toUiText())
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
                            viewState = SearchStudentState.ViewState.Error(error.toUiText())
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
                            viewState = SearchStudentState.ViewState.Error(error.toUiText())
                        )
                    }
                }
        }
    }

    private suspend fun handleStudentActionButton(studentId: String) {
        val state = stateFlow.value

        when (state.intention) {
            SearchStudentIntention.ASSIGN_UNASSIGN_STUDENT_TO_SEMESTER -> {
                handleAssignUnassignStudentToSemester(studentId)
            }

            SearchStudentIntention.ASSIGN_UNASSIGN_STUDENT_TO_DIVISION -> {
                handleAssignUnassignStudentToDivision(studentId)
            }

            SearchStudentIntention.MODIFY_STUDENT_DIVISION -> {
                handleModifyStudentDivision(studentId)
            }

            SearchStudentIntention.ASSIGN_UNASSIGN_STUDENT_TO_BATCH -> {
                handleAssignUnassignStudentToBatch(studentId)
            }

            SearchStudentIntention.MODIFY_STUDENT_BATCH -> {
                handleModifyStudentBatch(studentId)
            }

            SearchStudentIntention.MARK_UNMARK_STUDENT_AS_DROPOUT -> {
                // For dropout intention, the action button is handled via ToggleStudentDropout
                // This case should not be triggered
            }

            SearchStudentIntention.VIEW_ATTENDANCE -> {
                sendEvent(SearchStudentEvent.NavigateToStudentAttendanceAnalytics(studentId))
            }

            SearchStudentIntention.ADD_STUDENT_BIOMETRIC -> {
                sendEvent(SearchStudentEvent.NavigateToAddStudentBiometrics(studentId))
            }

            SearchStudentIntention.DEFAULT -> {
                // Should not happen
            }
        }
    }

    private suspend fun handleToggleStudentDropout(studentId: String, isCurrentlyDropout: Boolean) {
        val state = stateFlow.value
        val dropoutStartYear = state.dropoutAcademicStartYear ?: return
        val dropoutEndYear = state.dropoutAcademicEndYear ?: return

        // Prevent duplicate clicks
        if (state.loadingStudentIds.contains(studentId)) return

        // Mark student as loading
        updateState {
            it.copy(
                loadingStudentIds = it.loadingStudentIds + studentId
            )
        }

        if (isCurrentlyDropout) {
            // Remove from dropout
            studentDropoutRepository.removeStudentFromDropout(studentId, dropoutStartYear, dropoutEndYear)
                .onSuccess {
                    updateState {
                        it.copy(
                            loadingStudentIds = it.loadingStudentIds - studentId,
                            studentDropoutStatus = it.studentDropoutStatus + (studentId to false)
                        )
                    }
                    // Show success snackbar
                    viewModelScope.launch {
                        SnackbarController.sendEvent(
                            SnackbarEvent(
                                message = "Student removed from dropout successfully"
                            )
                        )
                    }
                }
                .onError { error ->
                    updateState {
                        it.copy(
                            loadingStudentIds = it.loadingStudentIds - studentId,
                            studentFeedback = it.studentFeedback + (studentId to ListItemFeedback.Error(error.toUiText()))
                        )
                    }
                }
        } else {
            // Add to dropout
            studentDropoutRepository.addStudentToDropout(studentId, dropoutStartYear, dropoutEndYear)
                .onSuccess {
                    updateState {
                        it.copy(
                            loadingStudentIds = it.loadingStudentIds - studentId,
                            studentDropoutStatus = it.studentDropoutStatus + (studentId to true)
                        )
                    }
                    // Show success snackbar
                    viewModelScope.launch {
                        SnackbarController.sendEvent(
                            SnackbarEvent(
                                message = "Student marked as dropout successfully"
                            )
                        )
                    }
                }
                .onError { error ->
                    updateState {
                        it.copy(
                            loadingStudentIds = it.loadingStudentIds - studentId,
                            studentFeedback = it.studentFeedback + (studentId to ListItemFeedback.Error(error.toUiText()))
                        )
                    }
                }
        }
    }

    private suspend fun handleAssignUnassignStudentToSemester(studentId: String) {
        val state = stateFlow.value
        val semesterId = state.semesterId ?: return

        // Prevent duplicate clicks
        if (state.loadingStudentIds.contains(studentId)) return

        // Find the student in the list
        val student = state.students.find { it.id == studentId } ?: return

        // Check if student is already assigned to this semester
        val studentSemester = student.studentSemesters?.find { it.semester?.id == semesterId }
        val isAssigned = studentSemester != null

        // Mark student as loading
        updateState {
            it.copy(
                loadingStudentIds = it.loadingStudentIds + studentId,
                studentFeedback = it.studentFeedback - studentId
            )
        }

        if (isAssigned) {
            // Unassign student from semester - need the studentSemesterId
            val studentSemesterId = studentSemester.id

            studentRepository.removeStudentFromSemester(studentSemesterId)
                .onSuccess {
                    // Update the student in the list
                    val updatedStudents = state.students.map { s ->
                        if (s.id == studentId) {
                            s.copy(
                                studentSemesters = s.studentSemesters?.filter { it.semester?.id != semesterId }
                            )
                        } else {
                            s
                        }
                    }.toPersistentList()

                    updateState {
                        it.copy(
                            students = updatedStudents,
                            loadingStudentIds = it.loadingStudentIds - studentId,
                            studentFeedback = it.studentFeedback + (studentId to ListItemFeedback.Success(
                                UiText.DynamicString(
                                    "Student removed from semester successfully"
                                )
                            ))
                        )
                    }
                }
                .onError { error ->
                    updateState {
                        it.copy(
                            loadingStudentIds = it.loadingStudentIds - studentId,
                            studentFeedback = it.studentFeedback + (studentId to ListItemFeedback.Error(error.toUiText()))
                        )
                    }
                }
        } else {
            // Assign student to semester
            studentRepository.addStudentToSemester(studentId, semesterId)
                .onSuccess {
                    // Fetch updated student to get the new semester info
                    viewModelScope.launch {
                        studentRepository.getStudentById(studentId)
                            .onSuccess { updatedStudent ->
                                val updatedStudents = state.students.map { s ->
                                    if (s.id == studentId) {
                                        updatedStudent
                                    } else {
                                        s
                                    }
                                }.toPersistentList()

                                updateState {
                                    it.copy(
                                        students = updatedStudents,
                                        loadingStudentIds = it.loadingStudentIds - studentId,
                                        studentFeedback = it.studentFeedback + (studentId to ListItemFeedback.Success(
                                            UiText.DynamicString("Student added to semester successfully")
                                        ))
                                    )
                                }
                            }
                            .onError { error ->
                                updateState {
                                    it.copy(
                                        loadingStudentIds = it.loadingStudentIds - studentId,
                                        studentFeedback = it.studentFeedback + (studentId to ListItemFeedback.Error(
                                            error.toUiText()
                                        ))
                                    )
                                }
                            }
                    }
                }
                .onError { error ->
                    updateState {
                        it.copy(
                            loadingStudentIds = it.loadingStudentIds - studentId,
                            studentFeedback = it.studentFeedback + (studentId to ListItemFeedback.Error(error.toUiText()))
                        )
                    }
                }
        }
    }

    private suspend fun handleAssignUnassignStudentToDivision(studentId: String) {
        val state = stateFlow.value
        val divisionId = state.divisionId ?: return

        // Prevent duplicate clicks
        if (state.loadingStudentIds.contains(studentId)) return

        // Find the student in the list
        val student = state.students.find { it.id == studentId } ?: return

        // Check if student is already assigned to this division
        val studentDivision = student.studentDivisions?.find { it.division?.id == divisionId && it.endDate == null }
        val isAssigned = studentDivision != null

        // Mark student as loading
        updateState {
            it.copy(
                loadingStudentIds = it.loadingStudentIds + studentId,
                studentFeedback = it.studentFeedback - studentId
            )
        }

        if (isAssigned) {
            // Unassign student from division - use revert method
            val studentDivisionId = studentDivision.id

            studentRepository.revertAddStudentToDivision(studentDivisionId)
                .onSuccess {
                    // Update the student in the list
                    val updatedStudents = state.students.map { s ->
                        if (s.id == studentId) {
                            s.copy(
                                studentDivisions = s.studentDivisions?.filter { it.division?.id != divisionId }
                            )
                        } else {
                            s
                        }
                    }.toPersistentList()

                    updateState {
                        it.copy(
                            students = updatedStudents,
                            loadingStudentIds = it.loadingStudentIds - studentId,
                            studentFeedback = it.studentFeedback + (studentId to ListItemFeedback.Success(
                                UiText.DynamicString(
                                    "Student removed from division successfully"
                                )
                            ))
                        )
                    }
                }
                .onError { error ->
                    updateState {
                        it.copy(
                            loadingStudentIds = it.loadingStudentIds - studentId,
                            studentFeedback = it.studentFeedback + (studentId to ListItemFeedback.Error(error.toUiText()))
                        )
                    }
                }
        } else {
            // Assign student to division
            studentRepository.addStudentToDivision(studentId, divisionId)
                .onSuccess {
                    // Fetch updated student
                    viewModelScope.launch {
                        studentRepository.getStudentById(studentId)
                            .onSuccess { updatedStudent ->
                                val updatedStudents = state.students.map { s ->
                                    if (s.id == studentId) {
                                        updatedStudent
                                    } else {
                                        s
                                    }
                                }.toPersistentList()

                                updateState {
                                    it.copy(
                                        students = updatedStudents,
                                        loadingStudentIds = it.loadingStudentIds - studentId,
                                        studentFeedback = it.studentFeedback + (studentId to ListItemFeedback.Success(
                                            UiText.DynamicString("Student added to division successfully")
                                        ))
                                    )
                                }
                            }
                            .onError { error ->
                                updateState {
                                    it.copy(
                                        loadingStudentIds = it.loadingStudentIds - studentId,
                                        studentFeedback = it.studentFeedback + (studentId to ListItemFeedback.Error(
                                            error.toUiText()
                                        ))
                                    )
                                }
                            }
                    }
                }
                .onError { error ->
                    updateState {
                        it.copy(
                            loadingStudentIds = it.loadingStudentIds - studentId,
                            studentFeedback = it.studentFeedback + (studentId to ListItemFeedback.Error(error.toUiText()))
                        )
                    }
                }
        }
    }

    private suspend fun handleModifyStudentDivision(studentId: String) {
        val state = stateFlow.value
        val targetDivisionId = state.divisionId ?: return
        val newStartDateString = state.newStartDate ?: return

        // Parse the date from ISO format (yyyy-MM-dd)
        val newStartDate = try {
            LocalDate.parse(newStartDateString)
        } catch (e: Exception) {
            updateState {
                it.copy(
                    viewState = SearchStudentState.ViewState.Error(
                        UiText.DynamicString("Invalid start date format: ${e.message}")
                    )
                )
            }
            return
        }

        // Get semester details from route params
        val branchId = routeParams.branchId ?: return
        val academicStartYear = routeParams.academicStartYear ?: return
        val academicEndYear = routeParams.academicEndYear ?: return
        val semesterNumber = routeParams.semesterNumber ?: return

        // Convert Int to SemesterNumber enum
        val semesterNumberEnum = SemesterNumber.entries.find { it.value == semesterNumber }
        if (semesterNumberEnum == null) {
            updateState {
                it.copy(
                    viewState = SearchStudentState.ViewState.Error(
                        UiText.DynamicString("Invalid semester number")
                    )
                )
            }
            return
        }

        // Prevent duplicate clicks
        if (state.loadingStudentIds.contains(studentId)) return

        // Mark student as loading
        updateState {
            it.copy(
                loadingStudentIds = it.loadingStudentIds + studentId,
                studentFeedback = it.studentFeedback - studentId
            )
        }

        // Fetch the semester to get semesterId
        semesterRepository.getSemesters(
            semesterNumber = semesterNumberEnum,
            academicStartYear = academicStartYear,
            academicEndYear = academicEndYear,
            branchId = branchId,
            getAll = true
        )
            .onSuccess { semesterListWithTotalCount ->
                val semester = semesterListWithTotalCount.semesters.firstOrNull()
                if (semester == null) {
                    updateState {
                        it.copy(
                            loadingStudentIds = it.loadingStudentIds - studentId,
                            studentFeedback = it.studentFeedback + (studentId to ListItemFeedback.Error(
                                UiText.DynamicString("Semester not found")
                            ))
                        )
                    }
                    return@onSuccess
                }

                val semesterId = semester.id

                // Find the student in the list
                val student = state.students.find { it.id == studentId }
                if (student == null) {
                    updateState {
                        it.copy(
                            loadingStudentIds = it.loadingStudentIds - studentId,
                            studentFeedback = it.studentFeedback + (studentId to ListItemFeedback.Error(
                                UiText.DynamicString("Student not found")
                            ))
                        )
                    }
                    return@onSuccess
                }

                // Check if student is already in the target division for this semester (with endDate = null)
                val targetDivisionRecord = student.studentDivisions?.find {
                    it.division?.id == targetDivisionId &&
                    it.division?.semesterId == semesterId &&
                    it.endDate == null
                }

                if (targetDivisionRecord != null) {
                    // Student is already in target division, revert the change
                    viewModelScope.launch {
                        studentRepository.revertChangeStudentDivision(targetDivisionRecord.id)
                            .onSuccess {
                                // Fetch updated student
                                studentRepository.getStudentById(studentId)
                                    .onSuccess { updatedStudent ->
                                        val updatedStudents = state.students.map { s ->
                                            if (s.id == studentId) {
                                                updatedStudent
                                            } else {
                                                s
                                            }
                                        }.toPersistentList()

                                        updateState {
                                            it.copy(
                                                students = updatedStudents,
                                                loadingStudentIds = it.loadingStudentIds - studentId,
                                                studentFeedback = it.studentFeedback + (studentId to ListItemFeedback.Success(
                                                    UiText.DynamicString("Division change reverted successfully")
                                                ))
                                            )
                                        }
                                    }
                                    .onError { error ->
                                        updateState {
                                            it.copy(
                                                loadingStudentIds = it.loadingStudentIds - studentId,
                                                studentFeedback = it.studentFeedback + (studentId to ListItemFeedback.Error(
                                                    error.toUiText()
                                                ))
                                            )
                                        }
                                    }
                            }
                            .onError { error ->
                                updateState {
                                    it.copy(
                                        loadingStudentIds = it.loadingStudentIds - studentId,
                                        studentFeedback = it.studentFeedback + (studentId to ListItemFeedback.Error(error.toUiText()))
                                    )
                                }
                            }
                    }
                } else {
                    // Find the current active division for this semester (where endDate is null)
                    val currentDivisionRecord = student.studentDivisions?.find {
                        it.division?.semesterId == semesterId && it.endDate == null
                    }

                    if (currentDivisionRecord == null) {
                        updateState {
                            it.copy(
                                loadingStudentIds = it.loadingStudentIds - studentId,
                                studentFeedback = it.studentFeedback + (studentId to ListItemFeedback.Error(
                                    UiText.DynamicString("Student is not currently assigned to any division in this semester")
                                ))
                            )
                        }
                        return@onSuccess
                    }

                    // Change student division
                    viewModelScope.launch {
                        studentRepository.changeStudentDivision(
                            studentDivisionId = currentDivisionRecord.id,
                            divisionId = targetDivisionId,
                            newDivisionStartDate = newStartDate
                        )
                            .onSuccess {
                                // Fetch updated student
                                studentRepository.getStudentById(studentId)
                                    .onSuccess { updatedStudent ->
                                        val updatedStudents = state.students.map { s ->
                                            if (s.id == studentId) {
                                                updatedStudent
                                            } else {
                                                s
                                            }
                                        }.toPersistentList()

                                        updateState {
                                            it.copy(
                                                students = updatedStudents,
                                                loadingStudentIds = it.loadingStudentIds - studentId,
                                                studentFeedback = it.studentFeedback + (studentId to ListItemFeedback.Success(
                                                    UiText.DynamicString("Student division changed successfully")
                                                ))
                                            )
                                        }
                                    }
                                    .onError { error ->
                                        updateState {
                                            it.copy(
                                                loadingStudentIds = it.loadingStudentIds - studentId,
                                                studentFeedback = it.studentFeedback + (studentId to ListItemFeedback.Error(
                                                    error.toUiText()
                                                ))
                                            )
                                        }
                                    }
                            }
                            .onError { error ->
                                updateState {
                                    it.copy(
                                        loadingStudentIds = it.loadingStudentIds - studentId,
                                        studentFeedback = it.studentFeedback + (studentId to ListItemFeedback.Error(error.toUiText()))
                                    )
                                }
                            }
                    }
                }
            }
            .onError { error ->
                updateState {
                    it.copy(
                        loadingStudentIds = it.loadingStudentIds - studentId,
                        studentFeedback = it.studentFeedback + (studentId to ListItemFeedback.Error(error.toUiText()))
                    )
                }
            }
    }

    private suspend fun handleAssignUnassignStudentToBatch(studentId: String) {
        val state = stateFlow.value
        val batchId = state.batchId ?: return

        // Prevent duplicate clicks
        if (state.loadingStudentIds.contains(studentId)) return

        // Find the student in the list
        val student = state.students.find { it.id == studentId } ?: return

        // Check if student is already assigned to this batch
        val studentBatch = student.studentBatches?.find { it.batch?.id == batchId && it.endDate == null }
        val isAssigned = studentBatch != null

        // Mark student as loading
        updateState {
            it.copy(
                loadingStudentIds = it.loadingStudentIds + studentId,
                studentFeedback = it.studentFeedback - studentId
            )
        }

        if (isAssigned) {
            // Unassign student from batch - use revert method
            val studentBatchId = studentBatch.id

            studentRepository.revertAddStudentToBatch(studentBatchId)
                .onSuccess {
                    // Update the student in the list
                    val updatedStudents = state.students.map { s ->
                        if (s.id == studentId) {
                            s.copy(
                                studentBatches = s.studentBatches?.filter { it.batch?.id != batchId }
                            )
                        } else {
                            s
                        }
                    }.toPersistentList()

                    updateState {
                        it.copy(
                            students = updatedStudents,
                            loadingStudentIds = it.loadingStudentIds - studentId,
                            studentFeedback = it.studentFeedback + (studentId to ListItemFeedback.Success(
                                UiText.DynamicString(
                                    "Student removed from batch successfully"
                                )
                            ))
                        )
                    }
                }
                .onError { error ->
                    updateState {
                        it.copy(
                            loadingStudentIds = it.loadingStudentIds - studentId,
                            studentFeedback = it.studentFeedback + (studentId to ListItemFeedback.Error(error.toUiText()))
                        )
                    }
                }
        } else {
            // Assign student to batch
            studentRepository.addStudentToBatch(studentId, batchId)
                .onSuccess {
                    // Fetch updated student
                    viewModelScope.launch {
                        studentRepository.getStudentById(studentId)
                            .onSuccess { updatedStudent ->
                                val updatedStudents = state.students.map { s ->
                                    if (s.id == studentId) {
                                        updatedStudent
                                    } else {
                                        s
                                    }
                                }.toPersistentList()

                                updateState {
                                    it.copy(
                                        students = updatedStudents,
                                        loadingStudentIds = it.loadingStudentIds - studentId,
                                        studentFeedback = it.studentFeedback + (studentId to ListItemFeedback.Success(
                                            UiText.DynamicString("Student added to batch successfully")
                                        ))
                                    )
                                }
                            }
                            .onError { error ->
                                updateState {
                                    it.copy(
                                        loadingStudentIds = it.loadingStudentIds - studentId,
                                        studentFeedback = it.studentFeedback + (studentId to ListItemFeedback.Error(
                                            error.toUiText()
                                        ))
                                    )
                                }
                            }
                    }
                }
                .onError { error ->
                    updateState {
                        it.copy(
                            loadingStudentIds = it.loadingStudentIds - studentId,
                            studentFeedback = it.studentFeedback + (studentId to ListItemFeedback.Error(error.toUiText()))
                        )
                    }
                }
        }
    }

    private suspend fun handleModifyStudentBatch(studentId: String) {
        val state = stateFlow.value
        val targetBatchId = state.batchId ?: return
        val newStartDateString = state.newStartDate ?: return

        // Parse the date from ISO format (yyyy-MM-dd)
        val newStartDate = try {
            LocalDate.parse(newStartDateString)
        } catch (e: Exception) {
            updateState {
                it.copy(
                    viewState = SearchStudentState.ViewState.Error(
                        UiText.DynamicString("Invalid start date format: ${e.message}")
                    )
                )
            }
            return
        }

        // Get semester details from route params
        val branchId = routeParams.branchId ?: return
        val academicStartYear = routeParams.academicStartYear ?: return
        val academicEndYear = routeParams.academicEndYear ?: return
        val semesterNumber = routeParams.semesterNumber ?: return

        // Convert Int to SemesterNumber enum
        val semesterNumberEnum = SemesterNumber.entries.find { it.value == semesterNumber }
        if (semesterNumberEnum == null) {
            updateState {
                it.copy(
                    viewState = SearchStudentState.ViewState.Error(
                        UiText.DynamicString("Invalid semester number")
                    )
                )
            }
            return
        }

        // Prevent duplicate clicks
        if (state.loadingStudentIds.contains(studentId)) return

        // Mark student as loading
        updateState {
            it.copy(
                loadingStudentIds = it.loadingStudentIds + studentId,
                studentFeedback = it.studentFeedback - studentId
            )
        }

        // Fetch the semester to get semesterId
        semesterRepository.getSemesters(
            semesterNumber = semesterNumberEnum,
            academicStartYear = academicStartYear,
            academicEndYear = academicEndYear,
            branchId = branchId,
            getAll = true
        )
            .onSuccess { semesterListWithTotalCount ->
                val semester = semesterListWithTotalCount.semesters.firstOrNull()
                if (semester == null) {
                    updateState {
                        it.copy(
                            loadingStudentIds = it.loadingStudentIds - studentId,
                            studentFeedback = it.studentFeedback + (studentId to ListItemFeedback.Error(
                                UiText.DynamicString("Semester not found")
                            ))
                        )
                    }
                    return@onSuccess
                }

                val semesterId = semester.id

                // Find the student in the list
                val student = state.students.find { it.id == studentId }
                if (student == null) {
                    updateState {
                        it.copy(
                            loadingStudentIds = it.loadingStudentIds - studentId,
                            studentFeedback = it.studentFeedback + (studentId to ListItemFeedback.Error(
                                UiText.DynamicString("Student not found")
                            ))
                        )
                    }
                    return@onSuccess
                }

                // Find divisions that belong to this semester (with endDate = null)
                val semesterDivisionIds = student.studentDivisions
                    ?.filter { it.division?.semesterId == semesterId && it.endDate == null }
                    ?.mapNotNull { it.division?.id }
                    ?: emptyList()

                if (semesterDivisionIds.isEmpty()) {
                    updateState {
                        it.copy(
                            loadingStudentIds = it.loadingStudentIds - studentId,
                            studentFeedback = it.studentFeedback + (studentId to ListItemFeedback.Error(
                                UiText.DynamicString("Student is not assigned to the same division as the batch")
                            ))
                        )
                    }
                    return@onSuccess
                }

                // Check if student is already in the target batch for this semester (with endDate = null)
                // Filter batches where batch's divisionId is in semesterDivisionIds
                val targetBatchRecord = student.studentBatches?.find {
                    it.batch?.id == targetBatchId &&
                    it.batch?.divisionId in semesterDivisionIds &&
                    it.endDate == null
                }

                if (targetBatchRecord != null) {
                    // Student is already in target batch, revert the change
                    viewModelScope.launch {
                        studentRepository.revertChangeStudentBatch(targetBatchRecord.id)
                            .onSuccess {
                                // Fetch updated student
                                studentRepository.getStudentById(studentId)
                                    .onSuccess { updatedStudent ->
                                        val updatedStudents = state.students.map { s ->
                                            if (s.id == studentId) {
                                                updatedStudent
                                            } else {
                                                s
                                            }
                                        }.toPersistentList()

                                        updateState {
                                            it.copy(
                                                students = updatedStudents,
                                                loadingStudentIds = it.loadingStudentIds - studentId,
                                                studentFeedback = it.studentFeedback + (studentId to ListItemFeedback.Success(
                                                    UiText.DynamicString("Batch change reverted successfully")
                                                ))
                                            )
                                        }
                                    }
                                    .onError { error ->
                                        updateState {
                                            it.copy(
                                                loadingStudentIds = it.loadingStudentIds - studentId,
                                                studentFeedback = it.studentFeedback + (studentId to ListItemFeedback.Error(
                                                    error.toUiText()
                                                ))
                                            )
                                        }
                                    }
                            }
                            .onError { error ->
                                updateState {
                                    it.copy(
                                        loadingStudentIds = it.loadingStudentIds - studentId,
                                        studentFeedback = it.studentFeedback + (studentId to ListItemFeedback.Error(error.toUiText()))
                                    )
                                }
                            }
                    }
                } else {
                    // Find the current active batch for this semester (where endDate is null and divisionId in semesterDivisionIds)
                    val currentBatchRecord = student.studentBatches?.find {
                        it.batch?.divisionId in semesterDivisionIds && it.endDate == null
                    }

                    if (currentBatchRecord == null) {
                        updateState {
                            it.copy(
                                loadingStudentIds = it.loadingStudentIds - studentId,
                                studentFeedback = it.studentFeedback + (studentId to ListItemFeedback.Error(
                                    UiText.DynamicString("Student is not currently assigned to any batch in this semester")
                                ))
                            )
                        }
                        return@onSuccess
                    }

                    // Change student batch
                    viewModelScope.launch {
                        studentRepository.changeStudentBatch(
                            studentBatchId = currentBatchRecord.id,
                            batchId = targetBatchId,
                            newBatchStartDate = newStartDate
                        )
                            .onSuccess {
                                // Fetch updated student
                                studentRepository.getStudentById(studentId)
                                    .onSuccess { updatedStudent ->
                                        val updatedStudents = state.students.map { s ->
                                            if (s.id == studentId) {
                                                updatedStudent
                                            } else {
                                                s
                                            }
                                        }.toPersistentList()

                                        updateState {
                                            it.copy(
                                                students = updatedStudents,
                                                loadingStudentIds = it.loadingStudentIds - studentId,
                                                studentFeedback = it.studentFeedback + (studentId to ListItemFeedback.Success(
                                                    UiText.DynamicString("Student batch changed successfully")
                                                ))
                                            )
                                        }
                                    }
                                    .onError { error ->
                                        updateState {
                                            it.copy(
                                                loadingStudentIds = it.loadingStudentIds - studentId,
                                                studentFeedback = it.studentFeedback + (studentId to ListItemFeedback.Error(
                                                    error.toUiText()
                                                ))
                                            )
                                        }
                                    }
                            }
                            .onError { error ->
                                updateState {
                                    it.copy(
                                        loadingStudentIds = it.loadingStudentIds - studentId,
                                        studentFeedback = it.studentFeedback + (studentId to ListItemFeedback.Error(error.toUiText()))
                                    )
                                }
                            }
                    }
                }
            }
            .onError { error ->
                updateState {
                    it.copy(
                        loadingStudentIds = it.loadingStudentIds - studentId,
                        studentFeedback = it.studentFeedback + (studentId to ListItemFeedback.Error(error.toUiText()))
                    )
                }
            }
    }

    override fun handleAction(action: SearchStudentAction) {
        when (action) {

            is SearchStudentAction.NavigateBack -> {
                sendEvent(NavigateBack)
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

            is SearchStudentAction.UpdateAcademicStartYear -> {
                updateState { it.copy(academicStartYear = action.year) }
                viewModelScope.launch {
                    loadDivisionsAndBatches()
                }
            }

            is SearchStudentAction.UpdateAcademicEndYear -> {
                updateState { it.copy(academicEndYear = action.year) }
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
                updateState { it.copy(admissionYear = action.year) }
            }

            is SearchStudentAction.UpdateDropoutStartYear -> {
                updateState { it.copy(dropoutStartYear = action.year) }
            }

            is SearchStudentAction.UpdateDropoutEndYear -> {
                updateState { it.copy(dropoutEndYear = action.year) }
            }

            is SearchStudentAction.SelectBiometricVerificationStatus -> {
                updateState { it.copy(selectedBiometricVerificationStatus = action.status) }
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
                        academicStartYear = "",
                        academicEndYear = "",
                        selectedAdmissionTypes = persistentListOf(),
                        admissionYear = null,
                        dropoutStartYear = "",
                        dropoutEndYear = "",
                        selectedBiometricVerificationStatus = null,
                        selectedScheme = null,
                        selectedDivision = null,
                        selectedBatch = null
                    )
                }
            }

            is SearchStudentAction.ApplyFilters -> {
                refreshSearch()
            }

            is SearchStudentAction.StudentCardClick -> {
                sendEvent(NavigateToStudentDetails(action.studentId))
            }

            is SearchStudentAction.StudentActionButtonClick -> {
                viewModelScope.launch {
                    handleStudentActionButton(action.studentId)
                }
            }

            is SearchStudentAction.ToggleStudentDropout -> {
                viewModelScope.launch {
                    handleToggleStudentDropout(action.studentId, action.isCurrentlyDropout)
                }
            }

            is SearchStudentAction.LoadMoreStudents -> {
                loadNextStudents()
            }


            SearchStudentAction.ClickFloatingActionButton -> {
                sendEvent(NavigateToAddEditStudent)
            }
        }
    }
}
