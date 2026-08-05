package edu.watumull.presencify.feature.attendance.defaulters

import androidx.lifecycle.viewModelScope
import edu.watumull.presencify.core.designsystem.components.dialog.DialogType
import edu.watumull.presencify.core.domain.Result
import edu.watumull.presencify.core.domain.model.student.Student
import edu.watumull.presencify.core.domain.repository.academics.BranchRepository
import edu.watumull.presencify.core.domain.repository.academics.DivisionRepository
import edu.watumull.presencify.core.domain.repository.academics.SemesterRepository
import edu.watumull.presencify.core.domain.repository.attendance.AttendanceRepository
import edu.watumull.presencify.core.domain.repository.student.StudentRepository
import edu.watumull.presencify.core.presentation.UiText
import edu.watumull.presencify.core.presentation.components.dialog.DialogState
import edu.watumull.presencify.core.presentation.toUiText
import edu.watumull.presencify.core.presentation.utils.BaseViewModel
import edu.watumull.presencify.core.presentation.utils.CsvUtils
import edu.watumull.presencify.core.presentation.utils.MimeType
import edu.watumull.presencify.core.presentation.utils.ShareFileModel
import edu.watumull.presencify.core.presentation.utils.ShareUtils
import edu.watumull.presencify.core.presentation.utils.toReadableString
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class DefaultersViewModel(
    private val branchRepository: BranchRepository,
    private val divisionRepository: DivisionRepository,
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
                updateState {
                    it.copy(
                        selectedSemesterNumber = action.semesterNumber,
                        isSemesterNumberDropdownOpen = false
                    )
                }
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
                updateState {
                    it.copy(
                        selectedBranch = action.branch,
                        selectedDivision = null,
                        selectedCourse = null,
                        divisionOptions = emptyList(),
                        courseOptions = emptyList(),
                        isBranchDropdownOpen = false
                    )
                }
                tryResolveSemesterAndLoadCourses()
            }

            is DefaultersAction.ChangeBranchDropDownVisibility -> {
                updateState { it.copy(isBranchDropdownOpen = action.isVisible) }
            }

            is DefaultersAction.SelectDivision -> {
                updateState {
                    it.copy(
                        selectedDivision = action.division,
                        selectedCourse = null,
                        isCourseDropdownOpen = false,
                        isDivisionDropdownOpen = false
                    )
                }
                action.division?.let {
                    loadCoursesForDivision(action.division.id)
                }
            }

            is DefaultersAction.ChangeDivisionDropDownVisibility -> {
                updateState { it.copy(isDivisionDropdownOpen = action.isVisible) }
            }

            is DefaultersAction.SelectCourse -> {
                updateState { it.copy(selectedCourse = action.course, isCourseDropdownOpen = false) }
            }

            is DefaultersAction.ChangeCourseDropDownVisibility -> {
                updateState { it.copy(isCourseDropdownOpen = action.isVisible) }
            }

            is DefaultersAction.SelectStartDate -> {
                updateState { it.copy(startDate = action.date, showStartDatePicker = false) }
            }

            is DefaultersAction.ChangeStartDatePickerVisibility -> {
                updateState { it.copy(showStartDatePicker = action.isVisible) }
            }

            is DefaultersAction.SelectEndDate -> {
                updateState { it.copy(endDate = action.date, showEndDatePicker = false) }
            }

            is DefaultersAction.ChangeEndDatePickerVisibility -> {
                updateState { it.copy(showEndDatePicker = action.isVisible) }
            }

            DefaultersAction.GetDefaulters -> {
                getStudents()
            }

            DefaultersAction.ExportCsv -> {
                exportCsv()
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
            updateState {
                it.copy(
                    divisionOptions = emptyList(),
                    selectedDivision = null,
                    courseOptions = emptyList(),
                    selectedCourse = null
                )
            }
            return
        }

        val startYear = currentState.academicStartYear.toIntOrNull()
        val endYear = currentState.academicEndYear.toIntOrNull()
        if (startYear == null || endYear == null) {
            updateState {
                it.copy(
                    divisionOptions = emptyList(),
                    selectedDivision = null,
                    courseOptions = emptyList(),
                    selectedCourse = null
                )
            }
            return
        }

        viewModelScope.launch {
            updateState {
                it.copy(
                    areDivisionsLoading = true,
                    areCoursesLoading = true,
                    divisionOptions = emptyList(),
                    selectedDivision = null,
                    courseOptions = emptyList(),
                    selectedCourse = null
                )
            }

            when (val semesterResult = semesterRepository.getSemesters(
                semesterNumber = currentState.selectedSemesterNumber,
                academicStartYear = startYear,
                academicEndYear = endYear,
                branchId = currentState.selectedBranch.id,
                getAll = true
            )) {
                is Result.Error -> {
                    updateState {
                        it.copy(
                            areDivisionsLoading = false,
                            areCoursesLoading = false
                        )
                    }
                    showError(semesterResult.error.toUiText())
                    return@launch
                }

                is Result.Success -> {
                    val semester = semesterResult.data.semesters.firstOrNull()

                    if (semester == null) {
                        updateState {
                            it.copy(
                                areDivisionsLoading = false,
                                areCoursesLoading = false,
                                dialogState = DialogState(
                                    title = UiText.DynamicString("Error"),
                                    message = UiText.DynamicString("No semester found for the selected criteria"),
                                    dialogType = DialogType.ERROR
                                )
                            )
                        }
                        return@launch
                    }

                    val semesterId = semester.id

                    if (currentState.startDate == null && currentState.endDate == null) {
                        updateState {
                            it.copy(
                                startDate = semester.startDate,
                                endDate = semester.endDate
                            )
                        }
                    }



                    when (val divisionsResult =
                        divisionRepository.getDivisions(semesterId = semesterId, getAll = true)) {
                        is Result.Error -> {
                            updateState { it.copy(areDivisionsLoading = false, areCoursesLoading = false) }
                            return@launch
                        }

                        is Result.Success -> {
                            if (divisionsResult.data.divisions.isEmpty()) {
                                updateState {
                                    it.copy(
                                        areDivisionsLoading = false,
                                        areCoursesLoading = false,
                                        dialogState = DialogState(
                                            title = UiText.DynamicString("Error"),
                                            message = UiText.DynamicString("No divisions found for this semester"),
                                            dialogType = DialogType.ERROR
                                        )
                                    )
                                }
                                return@launch
                            }

                            updateState {
                                it.copy(
                                    divisionOptions = divisionsResult.data.divisions,
                                    areDivisionsLoading = false
                                )
                            }
                        }
                    }
                }
            }

            updateState { it.copy(areCoursesLoading = false) }
        }
    }

    private fun loadCoursesForDivision(divisionId: String) {
        viewModelScope.launch {
            updateState {
                it.copy(
                    areCoursesLoading = true,
                    courseOptions = emptyList(),
                    selectedCourse = null
                )
            }

            when (val coursesResult = divisionRepository.getCoursesOfDivision(divisionId)) {
                is Result.Error -> {
                    updateState { it.copy(areCoursesLoading = false) }
                }

                is Result.Success -> {
                    updateState {
                        it.copy(
                            courseOptions = coursesResult.data.compulsoryCourses + coursesResult.data.optionalCourses.mapNotNull { optionalCourse ->
                                optionalCourse.course
                            }.distinctBy { course ->
                                course.id
                            },
                            areCoursesLoading = false
                        )
                    }
                }
            }
        }
    }

    private fun getStudents() {
        val currentState = state
        if (currentState.selectedSemesterNumber == null || currentState.selectedBranch == null || currentState.selectedDivision == null || currentState.academicStartYear.isBlank() || currentState.academicEndYear.isBlank()) {
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
            updateState {
                it.copy(
                    isLoadingStudents = true,
                    students = emptyList(),
                    studentAttendanceMap = emptyMap(),
                    studentCourseAttendanceMap = emptyMap(),
                    studentAttendanceNumbersMap = emptyMap(),
                    studentCourseAttendanceNumbersMap = emptyMap(),
                    isAttendanceLoadingMap = emptyMap()
                )
            }

            when (val result = studentRepository.getStudents(
                semesterNumbers = listOf(currentState.selectedSemesterNumber.value),
                branchIds = listOf(currentState.selectedBranch.id),
                divisionId = currentState.selectedDivision.id,
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

    private fun loadAttendanceForStudents(students: List<Student>) {
        if (students.isEmpty()) return

        val currentState = state
        val branchId = currentState.selectedBranch?.id ?: return
        val divisionId = currentState.selectedDivision?.id ?: return
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

            // 2. Fetch courses for the selected division
            val coursesResult = divisionRepository.getCoursesOfDivision(divisionId)
            val courses = (coursesResult as? Result.Success)?.data?.let {
                it.compulsoryCourses +
                        it.optionalCourses
                            .mapNotNull { optional -> optional.course }
                            .distinctBy { course -> course.id }
            } ?: emptyList()

            if (courses.isEmpty()) {
                clearLoadingStateForStudents(students)
                return@launch
            }

            // 3. Fetch attendance per course for all students in one request per course
            val courseResults = courses.map { course ->
                async {
                    course.id to attendanceRepository.getAttendanceOfEveryStudentForSpecificCourseInSemester(
                        studentIds = students.map { it.id },
                        courseId = course.id,
                        semesterId = semesterId,
                        divisionId = divisionId,
                        batchId = null,
                        startDate = currentState.startDate,
                        endDate = currentState.endDate,
                        semesterNumber = currentState.selectedSemesterNumber,
                        academicStartYear = startYear,
                        academicEndYear = endYear,
                        branchId = branchId,
                        schemeId = null
                    )
                }
            }.awaitAll()

            val attendanceByStudentId = students.associateWith { student ->
                var totalLectures = 0
                var attendedLectures = 0
                val coursePercentages = mutableMapOf<String, Float>()
                val courseNumbers = mutableMapOf<String, Pair<Int, Int>>()

                courseResults.forEach { (courseId, result) ->
                    if (result is Result.Success) {
                        val studentAttendance = result.data.firstOrNull { it.first == student.id }?.second
                        if (studentAttendance != null) {
                            var courseTotal = 0
                            var courseAttended = 0
                            studentAttendance.aggregatedAttendance.forEach { aggregated ->
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
                }

                val overallPercentage = if (totalLectures > 0) {
                    (attendedLectures.toFloat() / totalLectures.toFloat()) * 100f
                } else {
                    0f
                }

                StudentAttendanceResult(
                    studentId = student.id,
                    overallPercentage = overallPercentage,
                    coursePercentages = coursePercentages,
                    overallNumbers = attendedLectures to totalLectures,
                    courseNumbers = courseNumbers
                )
            }

            val studentResults = students.map { student -> attendanceByStudentId.getValue(student) }

            // Update state simultaneously for all students
            updateState { state ->
                val newAttendanceMap = state.studentAttendanceMap.toMutableMap()
                val newCourseAttendanceMap = state.studentCourseAttendanceMap.toMutableMap()
                val newNumbersMap = state.studentAttendanceNumbersMap.toMutableMap()
                val newCourseNumbersMap = state.studentCourseAttendanceNumbersMap.toMutableMap()
                val newLoadingMap = state.isAttendanceLoadingMap.toMutableMap()

                studentResults.forEach { result ->
                    newAttendanceMap[result.studentId] = result.overallPercentage
                    newCourseAttendanceMap[result.studentId] = result.coursePercentages
                    newNumbersMap[result.studentId] = result.overallNumbers
                    newCourseNumbersMap[result.studentId] = result.courseNumbers
                    newLoadingMap[result.studentId] = false // Clear loading state for this student
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
                dialogState = DialogState(
                    title = UiText.DynamicString("Error"),
                    message = message,
                    dialogType = DialogType.ERROR
                )
            )
        }
    }

    private fun exportCsv() {
        val currentState = state

        if (currentState.students.isEmpty() || currentState.courseOptions.isEmpty()) {
            showError(UiText.DynamicString("No data available to export"))
            return
        }

        viewModelScope.launch {
            try {
                val csvBuilder = StringBuilder()

                val semesterName = currentState.selectedSemesterNumber?.toDisplayLabel() ?: "N/A"
                val academicYear = "${currentState.academicStartYear}-${currentState.academicEndYear}"
                val branchName = currentState.selectedBranch?.name ?: "N/A"
                val startDateStr =
                    if (currentState.startDate != null) "=\"${currentState.startDate.toReadableString()}\"" else "N/A"
                val endDateStr =
                    if (currentState.endDate != null) "=\"${currentState.endDate.toReadableString()}\"" else "N/A"

                csvBuilder.append("Semester Details\n")
                csvBuilder.append("Semester Number,Academic Year,Branch,Start Date,End Date\n")
                csvBuilder.append("$semesterName,$academicYear,$branchName,$startDateStr,$endDateStr\n")
                csvBuilder.append("\n")

                val courses = currentState.courseOptions

                val headerColumns = mutableListOf("PRN", "Roll No", "Full Name")
                courses.forEach { course ->
                    val safeName = course.name.replace(",", " ")
                    headerColumns.add(safeName)
                }
                headerColumns.add("Overall Status")

                csvBuilder.append(headerColumns.joinToString(",")).append("\n")

                val sortedStudents = currentState.students.sortedBy { student ->
                    student.studentDivisions?.firstOrNull { it.endDate == null }?.rollNo ?: Int.MAX_VALUE
                }

                sortedStudents.forEach { student ->
                    val rowColumns = mutableListOf<String>()
                    rowColumns.add(student.prn)

                    val rollNoVal = student.studentDivisions?.firstOrNull { it.endDate == null }?.rollNo
                    val rollNo = if (rollNoVal != null) "=\"$rollNoVal\"" else "N/A"
                    rowColumns.add(rollNo)

                    val safeFullName = "${student.firstName} ${student.lastName}".replace(",", " ")
                    rowColumns.add(safeFullName)

                    var isDefaulter = false

                    courses.forEach { course ->
                        val numbers = currentState.studentCourseAttendanceNumbersMap[student.id]?.get(course.id)
                        val percentage = currentState.studentCourseAttendanceMap[student.id]?.get(course.id)

                        val cellValue = if (numbers != null && percentage != null) {
                            if (percentage < 75.0f) {
                                isDefaulter = true
                            }
                            // Encapsulating the entire string inside ="..." prevents Excel from parsing it as a formula or date
                            "=\"${numbers.first}/${numbers.second} - ${percentage.toInt()}%\""
                        } else {
                            "N/A"
                        }

                        rowColumns.add(cellValue)
                    }

                    val overallStatus = if (isDefaulter) "Defaulter" else ""
                    rowColumns.add(overallStatus)

                    csvBuilder.append(rowColumns.joinToString(",")).append("\n")
                }

                val csvBytes = CsvUtils.stringToBytes(csvBuilder.toString())

                ShareUtils.shareFile(
                    ShareFileModel(
                        mime = MimeType.CSV,
                        fileName = "Defaulters_${branchName}_${academicYear}.csv",
                        bytes = csvBytes
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
                showError(UiText.DynamicString("Failed to generate CSV: ${e.message}"))
            }
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
