package edu.watumull.presencify.feature.attendance.aggregate_analytics

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import edu.watumull.presencify.core.design.systems.components.dialog.DialogType
import edu.watumull.presencify.core.domain.enums.SemesterNumber
import edu.watumull.presencify.core.domain.model.academics.Course
import edu.watumull.presencify.core.domain.model.attendance.AggregatedAttendance
import edu.watumull.presencify.core.domain.model.attendance.AttendanceRecord
import edu.watumull.presencify.core.domain.onError
import edu.watumull.presencify.core.domain.onSuccess
import edu.watumull.presencify.core.domain.repository.academics.BatchRepository
import edu.watumull.presencify.core.domain.repository.academics.BranchRepository
import edu.watumull.presencify.core.domain.repository.academics.DivisionRepository
import edu.watumull.presencify.core.domain.repository.academics.SemesterRepository
import edu.watumull.presencify.core.domain.repository.attendance.AttendanceRepository
import edu.watumull.presencify.core.presentation.toUiText
import edu.watumull.presencify.core.presentation.utils.BaseViewModel
import edu.watumull.presencify.feature.attendance.navigation.AttendanceRoutes
import kotlinx.coroutines.launch

class AggregateAttendanceAnalyticsViewModel(
    private val attendanceRepository: AttendanceRepository,
    private val semesterRepository: SemesterRepository,
    private val branchRepository: BranchRepository,
    private val divisionRepository: DivisionRepository,
    private val batchRepository: BatchRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<AggregateAttendanceAnalyticsState, AggregateAttendanceAnalyticsEvent, AggregateAttendanceAnalyticsAction>(
    initialState = AggregateAttendanceAnalyticsState()
) {

    private val routeParams = savedStateHandle.toRoute<AttendanceRoutes.AggregateAttendanceAnalytics>()

    init {
        viewModelScope.launch {
            loadBranches()
        }

        routeParams.semesterNumber?.let { semNum ->
            SemesterNumber.fromValue(semNum)?.let { sn ->
                updateState { it.copy(selectedSemesterNumber = sn) }
            }
        }
        routeParams.academicStartYear?.let { y ->
            updateState { it.copy(academicStartYear = y) }
        }
        routeParams.academicEndYear?.let { y ->
            updateState { it.copy(academicEndYear = y) }
        }
    }

    private suspend fun loadBranches() {
        updateState { it.copy(areBranchesLoading = true) }
        branchRepository.getBranches()
            .onSuccess { branches ->
                updateState { it.copy(branchOptions = branches, areBranchesLoading = false) }
                // If branchId was passed via route, pre-select it
                routeParams.branchId?.let { branchId ->
                    val branch = branches.firstOrNull { it.id == branchId }
                    if (branch != null) {
                        updateState { it.copy(selectedBranch = branch) }
                        tryResolveSemester()
                    }
                }
            }
            .onError { error ->
                updateState {
                    it.copy(
                        areBranchesLoading = false,
                        dialogState = AggregateAttendanceAnalyticsState.DialogState(
                            dialogType = DialogType.ERROR,
                            title = "Error Loading Branches",
                            message = error.toUiText(),
                            dialogIntention = DialogIntention.GENERIC
                        )
                    )
                }
            }
    }

    private fun tryResolveSemester() {
        val state = stateFlow.value
        val semesterNumber = state.selectedSemesterNumber ?: return
        val startYear = state.academicStartYear.toIntOrNull() ?: return
        val endYear = state.academicEndYear.toIntOrNull() ?: return
        val branch = state.selectedBranch ?: return

        viewModelScope.launch {
            resolveSemesterAndLoadData(semesterNumber, startYear, endYear, branch.id)
        }
    }

    private suspend fun resolveSemesterAndLoadData(
        semesterNumber: SemesterNumber,
        academicStartYear: Int,
        academicEndYear: Int,
        branchId: String,
    ) {
        updateState { it.copy(isLoadingAttendance = true, attendanceData = emptyList(), detailedAttendanceRecords = emptyMap()) }

        // Fetch semesters matching filters to resolve semesterId
        semesterRepository.getSemesters(
            semesterNumber = semesterNumber,
            academicStartYear = academicStartYear,
            academicEndYear = academicEndYear,
            branchId = branchId,
            getAll = true
        ).onSuccess { result ->
            val semester = result.semesters.firstOrNull()
            if (semester == null) {
                updateState {
                    it.copy(
                        isLoadingAttendance = false,
                        semester = null,
                        dialogState = AggregateAttendanceAnalyticsState.DialogState(
                            dialogType = DialogType.INFO,
                            title = "No Semester Found",
                            message = edu.watumull.presencify.core.presentation.UiText.DynamicString(
                                "No semester found for the selected filters."
                            ),
                            dialogIntention = DialogIntention.GENERIC
                        )
                    )
                }
                return
            }

            updateState { it.copy(semester = semester) }

            // Load divisions and batches for this semester
            loadDivisions(semester.id)
            loadBatches(semester.id)

            // Load attendance data
            loadAttendanceData(semester.id)
        }.onError { error ->
            updateState {
                it.copy(
                    isLoadingAttendance = false,
                    dialogState = AggregateAttendanceAnalyticsState.DialogState(
                        dialogType = DialogType.ERROR,
                        title = "Error Finding Semester",
                        message = error.toUiText(),
                        dialogIntention = DialogIntention.GENERIC
                    )
                )
            }
        }
    }

    private suspend fun loadDivisions(semesterId: String) {
        updateState { it.copy(areDivisionsLoading = true) }
        divisionRepository.getDivisions(
            semesterId = semesterId,
            getAll = true
        ).onSuccess { result ->
            updateState { it.copy(divisionOptions = result.divisions, areDivisionsLoading = false) }
        }.onError {
            updateState { it.copy(areDivisionsLoading = false) }
        }
    }

    private suspend fun loadBatches(semesterId: String) {
        updateState { it.copy(areBatchesLoading = true) }
        val state = stateFlow.value
        batchRepository.getBatches(
            semesterNumber = state.selectedSemesterNumber,
            branchId = state.selectedBranch?.id,
            academicStartYear = state.academicStartYear.toIntOrNull(),
            academicEndYear = state.academicEndYear.toIntOrNull(),
            getAll = true
        ).onSuccess { result ->
            updateState { it.copy(batchOptions = result.batches, areBatchesLoading = false) }
        }.onError {
            updateState { it.copy(areBatchesLoading = false) }
        }
    }

    private suspend fun loadAttendanceData(semesterId: String) {
        val state = stateFlow.value

        // First, fetch courses for the semester
        var courses: List<Course> = emptyList()
        var courseFetchFailed = false

        semesterRepository.getCoursesOfSemester(semesterId)
            .onSuccess { fetchedCourses -> courses = fetchedCourses }
            .onError { error ->
                courseFetchFailed = true
                updateState {
                    it.copy(
                        isLoadingAttendance = false,
                        dialogState = AggregateAttendanceAnalyticsState.DialogState(
                            dialogType = DialogType.ERROR,
                            title = "Error Loading Courses",
                            message = error.toUiText(),
                            dialogIntention = DialogIntention.GENERIC
                        )
                    )
                }
            }

        if (courseFetchFailed || courses.isEmpty()) {
            updateState { it.copy(isLoadingAttendance = false) }
            return
        }

        // For each course, call getAttendanceOfAllForSemesterDivisionBatchCourse
        val aggregatedList = mutableListOf<AggregatedAttendance>()
        val detailedMap = mutableMapOf<String, List<AttendanceRecord>>()

        courses.forEach { course ->
            attendanceRepository.getAttendanceOfAllForSemesterDivisionBatchCourse(
                semesterId = semesterId,
                divisionId = state.selectedDivision?.id,
                batchId = state.selectedBatch?.id,
                courseId = course.id,
                startDate = null,
                endDate = null,
                semesterNumber = state.selectedSemesterNumber,
                academicStartYear = state.academicStartYear.toIntOrNull(),
                academicEndYear = state.academicEndYear.toIntOrNull(),
                branchId = state.selectedBranch?.id,
                schemeId = null
            ).onSuccess { summaries ->
                // summaries is List<AttendanceSummary>, usually one per course
                val courseSummary = summaries.firstOrNull { it.courseId == course.id }
                    ?: summaries.firstOrNull()

                if (courseSummary != null) {
                    val records = courseSummary.attendanceSummary
                    val totalStudents = records.sumOf { it.totalStudents }
                    val presentStudents = records.sumOf { it.presentStudents }

                    aggregatedList.add(
                        AggregatedAttendance(
                            courseId = course.id,
                            courseName = course.name,
                            totalLectures = totalStudents,
                            attendedLectures = presentStudents
                        )
                    )
                    detailedMap[course.id] = records
                }
            }
            // Silently skip courses with errors
        }

        updateState {
            it.copy(
                isLoadingAttendance = false,
                attendanceData = aggregatedList.toList(),
                detailedAttendanceRecords = detailedMap.toMap()
            )
        }
    }

    override fun handleAction(action: AggregateAttendanceAnalyticsAction) {
        when (action) {
            is AggregateAttendanceAnalyticsAction.BackButtonClick -> {
                sendEvent(AggregateAttendanceAnalyticsEvent.NavigateBack)
            }
            is AggregateAttendanceAnalyticsAction.DismissDialog -> {
                updateState { it.copy(dialogState = null) }
            }
            is AggregateAttendanceAnalyticsAction.DonutCourseClick -> {
                sendEvent(AggregateAttendanceAnalyticsEvent.NavigateToSearchAttendanceForCourse(action.courseId, state.selectedDivision?.id))
            }
            is AggregateAttendanceAnalyticsAction.SelectSemesterNumber -> {
                updateState { it.copy(selectedSemesterNumber = action.semesterNumber) }
                tryResolveSemester()
            }
            is AggregateAttendanceAnalyticsAction.UpdateAcademicStartYear -> {
                updateState { it.copy(academicStartYear = action.year) }
                tryResolveSemester()
            }
            is AggregateAttendanceAnalyticsAction.UpdateAcademicEndYear -> {
                updateState { it.copy(academicEndYear = action.year) }
                tryResolveSemester()
            }
            is AggregateAttendanceAnalyticsAction.SelectBranch -> {
                updateState {
                    it.copy(
                        selectedBranch = action.branch,
                        selectedDivision = null,
                        selectedBatch = null,
                        divisionOptions = emptyList(),
                        batchOptions = emptyList()
                    )
                }
                tryResolveSemester()
            }
            is AggregateAttendanceAnalyticsAction.SelectDivision -> {
                updateState { it.copy(selectedDivision = action.division) }
                // Re-fetch attendance with division filter
                val semester = stateFlow.value.semester ?: return
                viewModelScope.launch {
                    updateState { it.copy(isLoadingAttendance = true) }
                    loadAttendanceData(semester.id)
                }
            }
            is AggregateAttendanceAnalyticsAction.SelectBatch -> {
                updateState { it.copy(selectedBatch = action.batch) }
                // Re-fetch attendance with batch filter
                val semester = stateFlow.value.semester ?: return
                viewModelScope.launch {
                    updateState { it.copy(isLoadingAttendance = true) }
                    loadAttendanceData(semester.id)
                }
            }
            is AggregateAttendanceAnalyticsAction.ChangeSemesterNumberDropDownVisibility -> {
                updateState { it.copy(isSemesterNumberDropdownOpen = action.isVisible) }
            }
            is AggregateAttendanceAnalyticsAction.ChangeBranchDropDownVisibility -> {
                updateState { it.copy(isBranchDropdownOpen = action.isVisible) }
            }
            is AggregateAttendanceAnalyticsAction.ChangeDivisionDropDownVisibility -> {
                updateState { it.copy(isDivisionDropdownOpen = action.isVisible) }
            }
            is AggregateAttendanceAnalyticsAction.ChangeBatchDropDownVisibility -> {
                updateState { it.copy(isBatchDropdownOpen = action.isVisible) }
            }
        }
    }
}
