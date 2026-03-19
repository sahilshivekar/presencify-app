package edu.watumull.presencify.feature.attendance.student_analytics

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import edu.watumull.presencify.core.design.systems.components.dialog.DialogType
import edu.watumull.presencify.core.domain.enums.SemesterNumber
import edu.watumull.presencify.core.domain.model.academics.Course
import edu.watumull.presencify.core.domain.model.auth.UserRole
import edu.watumull.presencify.core.domain.onError
import edu.watumull.presencify.core.domain.onSuccess
import edu.watumull.presencify.core.domain.repository.attendance.AttendanceRepository
import edu.watumull.presencify.core.domain.repository.auth.UserRepository
import edu.watumull.presencify.core.domain.repository.student.StudentRepository
import edu.watumull.presencify.core.presentation.toUiText
import edu.watumull.presencify.core.presentation.utils.BaseViewModel
import edu.watumull.presencify.feature.attendance.navigation.AttendanceRoutes
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class StudentAttendanceAnalyticsViewModel(
    private val studentRepository: StudentRepository,
    private val userRepository: UserRepository,
    private val attendanceRepository: AttendanceRepository,
    private val semesterRepository: edu.watumull.presencify.core.domain.repository.academics.SemesterRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<StudentAttendanceAnalyticsState, StudentAttendanceAnalyticsEvent, StudentAttendanceAnalyticsAction>(
    initialState = StudentAttendanceAnalyticsState()
) {

    private val routeParams = savedStateHandle.toRoute<AttendanceRoutes.StudentAttendanceAnalytics>()

    init {
        updateState {
            it.copy(
                studentId = routeParams.studentId
            )
        }
        viewModelScope.launch {
            val role = userRepository.getUserRole().firstOrNull()
            val studentId = userRepository.getUserId().firstOrNull()
            if (role == UserRole.STUDENT) {
                updateState {
                    it.copy(
                        studentId = studentId
                    )
                }
            }
            loadStudentData()
        }
    }

    private suspend fun loadStudentData() {
        updateState { it.copy(viewState = StudentAttendanceAnalyticsState.ViewState.Loading) }
        state.studentId?.let {
            studentRepository.getStudentById(it)
                .onSuccess { student ->
                    updateState {
                        it.copy(
                            viewState = StudentAttendanceAnalyticsState.ViewState.Content,
                            student = student
                        )
                    }
                }
                .onError { error ->
                    updateState {
                        it.copy(
                            viewState = StudentAttendanceAnalyticsState.ViewState.Error(
                                message = error.toUiText()
                            )
                        )
                    }
                }
        }
    }

    private suspend fun loadSemesterAttendance(semesterId: String) {
        val state = stateFlow.value
        val student = state.student ?: return

        // Mark this semester as loading
        updateState {
            it.copy(loadingSemesterIds = it.loadingSemesterIds + semesterId)
        }

        // Get the semester to extract course info
        val studentSemester = student.studentSemesters
            ?.firstOrNull { it.semester?.id == semesterId }
            ?: run {
                updateState {
                    it.copy(loadingSemesterIds = it.loadingSemesterIds - semesterId)
                }
                return
            }

        // Fetch courses for this semester from the repository
        val coursesResult = semesterRepository.getCoursesOfSemester(semesterId)

        var courses: List<Course> = emptyList()
        var courseFetchFailed = false

        coursesResult
            .onSuccess { fetchedCourses: List<Course> ->
                courses = fetchedCourses
            }
            .onError { error: edu.watumull.presencify.core.domain.DataError.Remote ->
                courseFetchFailed = true
                updateState {
                    it.copy(
                        loadingSemesterIds = it.loadingSemesterIds - semesterId,
                        dialogState = StudentAttendanceAnalyticsState.DialogState(
                            dialogType = DialogType.ERROR,
                            title = "Error Loading Courses",
                            message = error.toUiText(),
                            dialogIntention = DialogIntention.GENERIC
                        )
                    )
                }
            }

        if (courseFetchFailed) {
            return
        }

        if (courses.isEmpty()) {
            // Courses list is empty but no error - just no courses assigned
            updateState {
                it.copy(
                    loadingSemesterIds = it.loadingSemesterIds - semesterId,
                    semesterAttendanceData = it.semesterAttendanceData + (semesterId to emptyList())
                )
            }
            return
        }

        // Fetch attendance for each course in parallel
        val attendanceResults = courses.map { course ->
            viewModelScope.async {
                if (userRepository.getUserRole().firstOrNull() == UserRole.STUDENT) {
                    attendanceRepository.getAttendanceOfSelfForSpecificCourseInSemester(
                        courseId = course.id,
                        semesterId = semesterId,
                        divisionId = routeParams.divisionId,
                        batchId = routeParams.batchId,
                        startDate = routeParams.startDate?.let { kotlinx.datetime.LocalDate.parse(it) },
                        endDate = routeParams.endDate?.let { kotlinx.datetime.LocalDate.parse(it) },
                        semesterNumber = routeParams.semesterNumber?.let {
                            SemesterNumber.entries.find { s -> s.value == it }
                        },
                        academicStartYear = routeParams.academicStartYear?.toIntOrNull(),
                        academicEndYear = routeParams.academicEndYear?.toIntOrNull(),
                        branchId = routeParams.branchId,
                        schemeId = routeParams.schemeId
                    )
                } else {
                    attendanceRepository.getAttendanceOfAnyStudentForSpecificCourseInSemester(
                        studentId = student.id,
                        courseId = course.id,
                        semesterId = semesterId,
                        divisionId = routeParams.divisionId,
                        batchId = routeParams.batchId,
                        startDate = routeParams.startDate?.let { kotlinx.datetime.LocalDate.parse(it) },
                        endDate = routeParams.endDate?.let { kotlinx.datetime.LocalDate.parse(it) },
                        semesterNumber = routeParams.semesterNumber?.let {
                            SemesterNumber.entries.find { s -> s.value == it }
                        },
                        academicStartYear = routeParams.academicStartYear?.toIntOrNull(),
                        academicEndYear = routeParams.academicEndYear?.toIntOrNull(),
                        branchId = routeParams.branchId,
                        schemeId = routeParams.schemeId
                    )
                }
            }
        }.awaitAll()

        // Collect successful results
        val aggregatedAttendance =
            mutableListOf<edu.watumull.presencify.core.domain.model.attendance.AggregatedAttendance>()
        val detailedAttendanceMap =
            mutableMapOf<String, List<edu.watumull.presencify.core.domain.model.attendance.DetailedAttendanceRecord>>()

        attendanceResults.forEach { result ->
            result.onSuccess { attendanceData ->
                // Extract aggregated attendance
                attendanceData.aggregatedAttendance.forEach { aggregated ->
                    aggregatedAttendance.add(aggregated)
                    // Map detailed records to the course ID from aggregated
                    detailedAttendanceMap[aggregated.courseId] = attendanceData.detailedAttendanceRecord
                }
            }
            // Silently skip errors (courses with no attendance data)
        }

        // Update state with attendance data
        updateState {
            it.copy(
                loadingSemesterIds = it.loadingSemesterIds - semesterId,
                semesterAttendanceData = it.semesterAttendanceData + (semesterId to aggregatedAttendance.toList()),
                semesterDetailedAttendance = it.semesterDetailedAttendance + (semesterId to detailedAttendanceMap.toMap())
            )
        }
    }

    override fun handleAction(action: StudentAttendanceAnalyticsAction) {
        when (action) {
            is StudentAttendanceAnalyticsAction.BackButtonClick -> {
                sendEvent(StudentAttendanceAnalyticsEvent.NavigateBack)
            }

            is StudentAttendanceAnalyticsAction.DismissDialog -> {
                updateState { it.copy(dialogState = null) }
            }

            is StudentAttendanceAnalyticsAction.ToggleSemesterExpansion -> {
                val currentExpanded = stateFlow.value.expandedSemesterIds
                val newExpanded = if (currentExpanded.contains(action.semesterId)) {
                    currentExpanded - action.semesterId
                } else {
                    currentExpanded + action.semesterId
                }
                updateState { it.copy(expandedSemesterIds = newExpanded) }

                // If expanding and we don't have data yet, load it
                if (!currentExpanded.contains(action.semesterId)) {
                    viewModelScope.launch {
                        loadSemesterAttendance(action.semesterId)
                    }
                }
            }

            is StudentAttendanceAnalyticsAction.DonutCourseClick -> {
                state.studentId?.let {
                    sendEvent(
                        StudentAttendanceAnalyticsEvent.NavigateToSearchAttendanceForCourse(
                            courseId = action.courseId,
                            studentId = it,
                        )
                    )
                }
            }
        }
    }
}
