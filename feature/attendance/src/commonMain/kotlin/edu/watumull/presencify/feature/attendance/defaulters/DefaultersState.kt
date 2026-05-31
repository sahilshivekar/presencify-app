package edu.watumull.presencify.feature.attendance.defaulters

import edu.watumull.presencify.core.domain.enums.SemesterNumber
import edu.watumull.presencify.core.domain.model.academics.Branch
import edu.watumull.presencify.core.domain.model.academics.Course
import edu.watumull.presencify.core.domain.model.student.Student
import edu.watumull.presencify.core.presentation.UiText
import edu.watumull.presencify.core.presentation.components.dialog.DialogState
import kotlinx.datetime.LocalDate

data class DefaultersState(
    val viewState: ViewState = ViewState.Content,
    val dialogState: DialogState? = null,

    // Filter fields
    val selectedSemesterNumber: SemesterNumber? = null,
    val academicStartYear: String = "",
    val academicEndYear: String = "",
    val selectedBranch: Branch? = null,
    val selectedCourse: Course? = null,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val showStartDatePicker: Boolean = false,
    val showEndDatePicker: Boolean = false,

    // Dropdown visibility
    val isSemesterNumberDropdownOpen: Boolean = false,
    val isBranchDropdownOpen: Boolean = false,
    val isCourseDropdownOpen: Boolean = false,

    // Options
    val branchOptions: List<Branch> = emptyList(),
    val courseOptions: List<Course> = emptyList(),

    // Loading states
    val areBranchesLoading: Boolean = false,
    val areCoursesLoading: Boolean = false,
    val isLoadingStudents: Boolean = false,

    // Student data
    val students: List<Student> = emptyList(),

    // Attendance Data
    val studentAttendanceMap: Map<String, Float> = emptyMap(),
    val studentCourseAttendanceMap: Map<String, Map<String, Float>> = emptyMap(),
    val studentAttendanceNumbersMap: Map<String, Pair<Int, Int>> = emptyMap(),
    val studentCourseAttendanceNumbersMap: Map<String, Map<String, Pair<Int, Int>>> = emptyMap(),
    val isAttendanceLoadingMap: Map<String, Boolean> = emptyMap(),
) {
    sealed interface ViewState {
        data object Loading : ViewState
        data class Error(val message: UiText) : ViewState
        data object Content : ViewState
    }
}
