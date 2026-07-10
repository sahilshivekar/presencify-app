package edu.watumull.presencify.feature.attendance.student_attendance_dashboard

import androidx.lifecycle.viewModelScope
import edu.watumull.presencify.core.designsystem.components.dialog.DialogType
import edu.watumull.presencify.core.domain.model.academics.Course
import edu.watumull.presencify.core.domain.model.auth.UserRole
import edu.watumull.presencify.core.domain.model.student.StudentSemester
import edu.watumull.presencify.core.domain.onError
import edu.watumull.presencify.core.domain.onSuccess
import edu.watumull.presencify.core.domain.repository.academics.SemesterRepository
import edu.watumull.presencify.core.domain.repository.attendance.AttendanceRepository
import edu.watumull.presencify.core.domain.repository.auth.UserRepository
import edu.watumull.presencify.core.domain.repository.student.StudentRepository
import edu.watumull.presencify.core.presentation.UiText
import edu.watumull.presencify.core.presentation.components.dialog.DialogState
import edu.watumull.presencify.core.presentation.toUiText
import edu.watumull.presencify.core.presentation.utils.BaseViewModel
import edu.watumull.presencify.core.presentation.utils.DateTimeUtils
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlin.math.abs

class StudentAttendanceDashboardViewModel(
    private val studentRepository: StudentRepository,
    private val userRepository: UserRepository,
    private val attendanceRepository: AttendanceRepository,
    private val semesterRepository: SemesterRepository,
) : BaseViewModel<StudentAttendanceDashboardState, StudentAttendanceDashboardEvent, StudentAttendanceDashboardAction>(
    initialState = StudentAttendanceDashboardState()
) {

    init {
        viewModelScope.launch {
            val role = userRepository.getUserRole().firstOrNull()
            val studentId = userRepository.getUserId().firstOrNull()

            if (role != UserRole.STUDENT || studentId.isNullOrBlank()) {
                updateState {
                    it.copy(
                        viewState = StudentAttendanceDashboardState.ViewState.Error(
                            UiText.DynamicString("Student attendance dashboard is available only for student accounts.")
                        )
                    )
                }
                return@launch
            }

            updateState { it.copy(studentId = studentId) }
            loadStudentData(studentId)
        }
    }

    private suspend fun loadStudentData(studentId: String) {
        updateState { it.copy(viewState = StudentAttendanceDashboardState.ViewState.Loading) }

        studentRepository.getStudentById(studentId)
            .onSuccess { student ->
                val semesters = student.studentSemesters
                    ?.filter { it.semester != null }
                    ?.sortedWith(
                        compareByDescending<StudentSemester> { it.semester?.semesterNumber?.value ?: 0 }
                            .thenByDescending { it.semester?.startDate?.toEpochDays() ?: Int.MIN_VALUE }
                    )
                    .orEmpty()
                val selectedSemesterId = findClosestSemesterToToday(semesters)?.semester?.id

                updateState {
                    it.copy(
                        viewState = StudentAttendanceDashboardState.ViewState.Content,
                        student = student,
                        semesters = semesters,
                        selectedSemesterId = selectedSemesterId,
                        selectedCourseIds = setOf(OVERALL_ATTENDANCE_COURSE_ID)
                    )
                }

                selectedSemesterId?.let { loadSemesterAttendance(it) }
                loadRecentAttendances(studentId)
            }
            .onError { error ->
                updateState {
                    it.copy(
                        viewState = StudentAttendanceDashboardState.ViewState.Error(
                            message = error.toUiText()
                        )
                    )
                }
            }
    }

    private suspend fun loadRecentAttendances(studentId: String) {
        updateState { it.copy(isLoadingRecentAttendances = true) }

        attendanceRepository.getAttendances(
            date = null,
            classId = null,
            studentId = studentId,
            courseId = null,
            semesterId = null,
            divisionId = null,
            batchId = null,
            semesterNumber = null,
            academicStartYear = null,
            academicEndYear = null,
            branchId = null,
            page = 1,
            limit = 10
        )
            .onSuccess { attendanceResult ->
                updateState {
                    it.copy(
                        recentAttendances = attendanceResult.attendances,
                        isLoadingRecentAttendances = false
                    )
                }
            }
            .onError { error ->
                updateState {
                    it.copy(
                        isLoadingRecentAttendances = false,
                        dialogState = DialogState(
                            title = UiText.DynamicString("Error Loading Recent Attendances"),
                            message = error.toUiText(),
                            dialogType = DialogType.ERROR,
                        )
                    )
                }
            }
    }

    private fun findClosestSemesterToToday(semesters: List<StudentSemester>): StudentSemester? {
        val todayEpoch: Long = DateTimeUtils.getCurrentDate().toEpochDays()
        return semesters.minByOrNull { studentSemester ->
            val startEpoch: Long = studentSemester.semester?.startDate?.toEpochDays() ?: Long.MAX_VALUE
            abs(startEpoch - todayEpoch)
        }
    }

    private suspend fun loadSemesterAttendance(semesterId: String) {
        if (stateFlow.value.semesterAttendanceData.containsKey(semesterId)) return

        updateState {
            it.copy(
                loadingSemesterIds = it.loadingSemesterIds + semesterId,
                selectedCourseIds = setOf(OVERALL_ATTENDANCE_COURSE_ID)
            )
        }

        val coursesResult = semesterRepository.getCoursesOfSemester(semesterId)
        var courses: List<Course> = emptyList()
        var courseFetchFailed = false

        coursesResult
            .onSuccess { fetchedCourses ->
                courses = fetchedCourses
            }
            .onError { error ->
                courseFetchFailed = true
                updateState {
                    it.copy(
                        loadingSemesterIds = it.loadingSemesterIds - semesterId,
                        dialogState = DialogState(
                            title = UiText.DynamicString("Error Loading Courses"),
                            message = error.toUiText(),
                            dialogType = DialogType.ERROR,
                        )
                    )
                }
            }

        if (courseFetchFailed) return

        if (courses.isEmpty()) {
            updateState {
                it.copy(
                    loadingSemesterIds = it.loadingSemesterIds - semesterId,
                    semesterAttendanceData = it.semesterAttendanceData + (semesterId to emptyList()),
                    semesterDetailedAttendance = it.semesterDetailedAttendance + (semesterId to emptyMap())
                )
            }
            return
        }

        val attendanceResults = courses.map { course ->
            viewModelScope.async {
                attendanceRepository.getAttendanceOfSelfForSpecificCourseInSemester(
                    courseId = course.id,
                    semesterId = semesterId,
                    divisionId = null,
                    batchId = null,
                    startDate = null,
                    endDate = null,
                    semesterNumber = null,
                    academicStartYear = null,
                    academicEndYear = null,
                    branchId = null,
                    schemeId = null
                )
            }
        }.awaitAll()

        val aggregatedAttendance =
            mutableListOf<edu.watumull.presencify.core.domain.model.attendance.AggregatedAttendance>()
        val detailedAttendanceMap =
            mutableMapOf<String, List<edu.watumull.presencify.core.domain.model.attendance.DetailedAttendanceRecord>>()

        attendanceResults.forEach { result ->
            result.onSuccess { attendanceData ->
                attendanceData.aggregatedAttendance.forEach { aggregated ->
                    aggregatedAttendance.add(aggregated)
                    detailedAttendanceMap[aggregated.courseId] = attendanceData.detailedAttendanceRecord
                }
            }
        }

        updateState {
            it.copy(
                loadingSemesterIds = it.loadingSemesterIds - semesterId,
                semesterAttendanceData = it.semesterAttendanceData + (semesterId to aggregatedAttendance.toList()),
                semesterDetailedAttendance = it.semesterDetailedAttendance + (semesterId to detailedAttendanceMap.toMap())
            )
        }
    }

    override fun handleAction(action: StudentAttendanceDashboardAction) {
        when (action) {
            StudentAttendanceDashboardAction.NavigateBack -> {
                sendEvent(StudentAttendanceDashboardEvent.NavigateBack)
            }

            StudentAttendanceDashboardAction.DismissDialog -> {
                updateState { it.copy(dialogState = null) }
            }

            is StudentAttendanceDashboardAction.SelectSemester -> {
                if (stateFlow.value.selectedSemesterId == action.semesterId) return
                updateState {
                    it.copy(
                        selectedSemesterId = action.semesterId,
                        selectedCourseIds = setOf(OVERALL_ATTENDANCE_COURSE_ID)
                    )
                }
                viewModelScope.launch {
                    loadSemesterAttendance(action.semesterId)
                }
            }

            is StudentAttendanceDashboardAction.ToggleCourseSelection -> {
                updateState { currentState ->
                    val selectedCourses = currentState.selectedCourseIds
                    currentState.copy(
                        selectedCourseIds = if (selectedCourses.contains(action.courseId)) {
                            selectedCourses - action.courseId
                        } else {
                            selectedCourses + action.courseId
                        }
                    )
                }
            }

            is StudentAttendanceDashboardAction.DonutCourseClick -> {
                stateFlow.value.studentId?.let { studentId ->
                    sendEvent(
                        StudentAttendanceDashboardEvent.NavigateToSearchAttendanceForCourse(
                            courseId = action.courseId,
                            studentId = studentId
                        )
                    )
                }
            }
        }
    }
}
