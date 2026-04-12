package edu.watumull.presencify.feature.attendance.defaulters

import androidx.lifecycle.viewModelScope
import edu.watumull.presencify.core.design.systems.components.dialog.DialogType
import edu.watumull.presencify.core.domain.Result
import edu.watumull.presencify.core.domain.repository.academics.BranchRepository
import edu.watumull.presencify.core.domain.repository.academics.SemesterRepository
import edu.watumull.presencify.core.domain.repository.attendance.AttendanceRepository
import edu.watumull.presencify.core.domain.repository.student.StudentRepository
import edu.watumull.presencify.core.presentation.UiText
import edu.watumull.presencify.core.presentation.toUiText
import edu.watumull.presencify.core.presentation.utils.BaseViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class DefaultersViewModel(
    private val branchRepository: BranchRepository,
    private val studentRepository: StudentRepository,
    private val semesterRepository: SemesterRepository,
    private val attendanceRepository: AttendanceRepository,
) : BaseViewModel<DefaultersState, DefaultersEvent, DefaultersAction>(
    initialState = DefaultersState()
) {
    init {
        loadBranches()
    }

    override fun handleAction(action: DefaultersAction) {
        when (action) {
            is DefaultersAction.SelectSemesterNumber -> {
                updateState { it.copy(selectedSemesterNumber = action.semesterNumber, isSemesterNumberDropdownOpen = false) }
                tryResolveSemesterAndLoadCourses()
            }
            is DefaultersAction.ChangeSemesterNumberDropDownVisibility -> {
                updateState { it.copy(isSemesterNumberDropdownOpen = action.isVisible) }
            }
            is DefaultersAction.UpdateAcademicStartYear -> {
                updateState { it.copy(academicStartYear = action.year) }
                tryResolveSemesterAndLoadCourses()
            }
            is DefaultersAction.UpdateAcademicEndYear -> {
                updateState { it.copy(academicEndYear = action.year) }
                tryResolveSemesterAndLoadCourses()
            }
            is DefaultersAction.SelectBranch -> {
                updateState { it.copy(selectedBranch = action.branch, isBranchDropdownOpen = false) }
                tryResolveSemesterAndLoadCourses()
            }
            is DefaultersAction.ChangeBranchDropDownVisibility -> {
                updateState { it.copy(isBranchDropdownOpen = action.isVisible) }
            }
            is DefaultersAction.SelectCourse -> {
                updateState { it.copy(selectedCourse = action.course, isCourseDropdownOpen = false) }
            }
            is DefaultersAction.ChangeCourseDropDownVisibility -> {
                updateState { it.copy(isCourseDropdownOpen = action.isVisible) }
            }
            DefaultersAction.GetDefaulters -> {
                getStudents()
            }
            DefaultersAction.DismissDialog -> {
                updateState { it.copy(dialogState = null) }
            }
        }
    }

    private fun loadBranches() {
        viewModelScope.launch {
            updateState { it.copy(areBranchesLoading = true) }
            when (val result = branchRepository.getBranches()) {
                is Result.Error -> {
                    showError(result.error.toUiText())
                    updateState { it.copy(areBranchesLoading = false) }
                }
                is Result.Success -> {
                    updateState {
                        it.copy(
                            branchOptions = result.data,
                            areBranchesLoading = false
                        )
                    }
                }
            }
        }
    }

    private fun tryResolveSemesterAndLoadCourses() {
        val currentState = state
        if (currentState.selectedSemesterNumber == null || currentState.selectedBranch == null || currentState.academicStartYear.isBlank() || currentState.academicEndYear.isBlank()) {
            updateState { it.copy(courseOptions = emptyList(), selectedCourse = null) }
            return
        }

        val startYear = currentState.academicStartYear.toIntOrNull()
        val endYear = currentState.academicEndYear.toIntOrNull()
        if (startYear == null || endYear == null) {
            updateState { it.copy(courseOptions = emptyList(), selectedCourse = null) }
            return
        }

        viewModelScope.launch {
            updateState { it.copy(areCoursesLoading = true, courseOptions = emptyList(), selectedCourse = null) }

            val semesterResult = semesterRepository.getSemesters(
                semesterNumber = currentState.selectedSemesterNumber,
                academicStartYear = startYear,
                academicEndYear = endYear,
                branchId = currentState.selectedBranch.id,
                getAll = true
            )

            val semesterId = (semesterResult as? Result.Success)?.data?.semesters?.firstOrNull()?.id
            if (semesterId == null) {
                updateState { it.copy(areCoursesLoading = false) }
                return@launch
            }

            when (val coursesResult = semesterRepository.getCoursesOfSemester(semesterId)) {
                is Result.Error -> {
                    updateState { it.copy(areCoursesLoading = false) }
                }
                is Result.Success -> {
                    updateState {
                        it.copy(
                            courseOptions = coursesResult.data,
                            areCoursesLoading = false
                        )
                    }
                }
            }
        }
    }

    private fun getStudents() {
        val currentState = state
        if (currentState.selectedSemesterNumber == null || currentState.selectedBranch == null || currentState.academicStartYear.isBlank() || currentState.academicEndYear.isBlank()) {
            showError(UiText.DynamicString("Please fill all fields"))
            return
        }

        val startYear = currentState.academicStartYear.toIntOrNull()
        val endYear = currentState.academicEndYear.toIntOrNull()
        if (startYear == null || endYear == null) {
            showError(UiText.DynamicString("Please enter valid years"))
            return
        }

        viewModelScope.launch {
            updateState { it.copy(isLoadingStudents = true, students = emptyList(), studentAttendanceMap = emptyMap(), studentCourseAttendanceMap = emptyMap(), studentAttendanceNumbersMap = emptyMap(), studentCourseAttendanceNumbersMap = emptyMap(), isAttendanceLoadingMap = emptyMap()) }

            when (val result = studentRepository.getStudents(
                semesterNumbers = listOf(currentState.selectedSemesterNumber.value),
                branchIds = listOf(currentState.selectedBranch.id),
                academicStartYearOfSemester = startYear,
                academicEndYearOfSemester = endYear,
                getAll = true
            )) {
                is Result.Error -> {
                    showError(result.error.toUiText())
                    updateState { it.copy(isLoadingStudents = false) }
                }
                is Result.Success -> {
                    val students = result.data.students
                    updateState {
                        it.copy(
                            students = students,
                            isLoadingStudents = false
                        )
                    }
                    loadAttendanceForStudents(students)
                }
            }
        }
    }

    private fun loadAttendanceForStudents(students: List<edu.watumull.presencify.core.domain.model.student.Student>) {
        if (students.isEmpty()) return

        val currentState = state
        val branchId = currentState.selectedBranch?.id ?: return
        val startYear = currentState.academicStartYear.toIntOrNull() ?: return
        val endYear = currentState.academicEndYear.toIntOrNull() ?: return

        viewModelScope.launch {
            // Mark all valid students as loading
            updateState { state ->
                val newLoadingMap = state.isAttendanceLoadingMap.toMutableMap()
                students.forEach { newLoadingMap[it.id] = true }
                state.copy(isAttendanceLoadingMap = newLoadingMap)
            }

            // 1. Resolve the semester
            val semesterResult = semesterRepository.getSemesters(
                semesterNumber = currentState.selectedSemesterNumber,
                academicStartYear = startYear,
                academicEndYear = endYear,
                branchId = branchId,
                getAll = true
            )

            val semesterId = (semesterResult as? Result.Success)?.data?.semesters?.firstOrNull()?.id
            if (semesterId == null) {
                // If we can't find semesterId, stop loading
                clearLoadingStateForStudents(students)
                return@launch
            }

            // 2. Fetch courses for the semester
            val coursesResult = semesterRepository.getCoursesOfSemester(semesterId)
            val courses = (coursesResult as? Result.Success)?.data ?: emptyList()
            if (courses.isEmpty()) {
                clearLoadingStateForStudents(students)
                return@launch
            }

            // 3. Fetch attendance for each student in parallel
            // Chunked execution to not overwhelm the network if there are many students
            students.chunked(10).forEach { chunk ->
                val chunkResults = chunk.map { student ->
                    async {
                        val studentCourseResults = courses.associate { course ->
                            val result = attendanceRepository.getAttendanceOfAnyStudentForSpecificCourseInSemester(
                                studentId = student.id,
                                courseId = course.id,
                                semesterId = semesterId,
                                divisionId = null,
                                batchId = null,
                                startDate = null,
                                endDate = null,
                                semesterNumber = currentState.selectedSemesterNumber,
                                academicStartYear = startYear,
                                academicEndYear = endYear,
                                branchId = branchId,
                                schemeId = null
                            )
                            course.id to result
                        }

                        var totalLectures = 0
                        var attendedLectures = 0
                        val coursePercentages = mutableMapOf<String, Float>()
                        val courseNumbers = mutableMapOf<String, Pair<Int, Int>>()

                        studentCourseResults.forEach { (courseId, res) ->
                            if (res is Result.Success) {
                                var courseTotal = 0
                                var courseAttended = 0
                                res.data.aggregatedAttendance.forEach { aggregated ->
                                    totalLectures += aggregated.totalLectures
                                    attendedLectures += aggregated.attendedLectures
                                    courseTotal += aggregated.totalLectures
                                    courseAttended += aggregated.attendedLectures
                                }
                                coursePercentages[courseId] = if (courseTotal > 0) {
                                    (courseAttended.toFloat() / courseTotal.toFloat()) * 100f
                                } else {
                                    0f
                                }
                                courseNumbers[courseId] = courseAttended to courseTotal
                            }
                        }

                        val overallPercentage = if (totalLectures > 0) {
                            (attendedLectures.toFloat() / totalLectures.toFloat()) * 100f
                        } else {
                            0f
                        }

                        // Triple -> Quintuple conceptually or just define a local class
                        StudentAttendanceResult(
                            studentId = student.id,
                            overallPercentage = overallPercentage,
                            coursePercentages = coursePercentages,
                            overallNumbers = attendedLectures to totalLectures,
                            courseNumbers = courseNumbers
                        )
                    }
                }.awaitAll()

                // Update state incrementally
                updateState { state ->
                    val newAttendanceMap = state.studentAttendanceMap.toMutableMap()
                    val newCourseAttendanceMap = state.studentCourseAttendanceMap.toMutableMap()
                    val newNumbersMap = state.studentAttendanceNumbersMap.toMutableMap()
                    val newCourseNumbersMap = state.studentCourseAttendanceNumbersMap.toMutableMap()
                    val newLoadingMap = state.isAttendanceLoadingMap.toMutableMap()

                    chunkResults.forEach { result ->
                        newAttendanceMap[result.studentId] = result.overallPercentage
                        newCourseAttendanceMap[result.studentId] = result.coursePercentages
                        newNumbersMap[result.studentId] = result.overallNumbers
                        newCourseNumbersMap[result.studentId] = result.courseNumbers
                        newLoadingMap[result.studentId] = false
                    }

                    state.copy(
                        studentAttendanceMap = newAttendanceMap,
                        studentCourseAttendanceMap = newCourseAttendanceMap,
                        studentAttendanceNumbersMap = newNumbersMap,
                        studentCourseAttendanceNumbersMap = newCourseNumbersMap,
                        isAttendanceLoadingMap = newLoadingMap
                    )
                }
            }
        }
    }

    private fun clearLoadingStateForStudents(students: List<edu.watumull.presencify.core.domain.model.student.Student>) {
        updateState { state ->
            val newLoadingMap = state.isAttendanceLoadingMap.toMutableMap()
            students.forEach { newLoadingMap[it.id] = false }
            state.copy(isAttendanceLoadingMap = newLoadingMap)
        }
    }

    private fun showError(message: UiText) {
        updateState {
            it.copy(
                dialogState = DefaultersState.DialogState(
                    isVisible = true,
                    dialogType = DialogType.ERROR,
                    title = "Error",
                    message = message
                )
            )
        }
    }
}

private data class StudentAttendanceResult(
    val studentId: String,
    val overallPercentage: Float,
    val coursePercentages: Map<String, Float>,
    val overallNumbers: Pair<Int, Int>,
    val courseNumbers: Map<String, Pair<Int, Int>>
)
