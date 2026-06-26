package edu.watumull.presencify.feature.attendance.student_attendance_dashboard

import edu.watumull.presencify.core.domain.model.attendance.AggregatedAttendance
import edu.watumull.presencify.core.domain.model.attendance.DetailedAttendanceRecord
import edu.watumull.presencify.core.domain.model.student.Student
import edu.watumull.presencify.core.domain.model.student.StudentSemester
import edu.watumull.presencify.core.presentation.UiText
import edu.watumull.presencify.core.presentation.components.dialog.DialogState

const val OVERALL_ATTENDANCE_COURSE_ID = "__overall__"

data class StudentAttendanceDashboardState(
    val viewState: ViewState = ViewState.Loading,
    val dialogState: DialogState? = null,
    val student: Student? = null,
    val studentId: String? = null,
    val semesters: List<StudentSemester> = emptyList(),
    val selectedSemesterId: String? = null,
    val selectedCourseIds: Set<String> = setOf(OVERALL_ATTENDANCE_COURSE_ID),
    val semesterAttendanceData: Map<String, List<AggregatedAttendance>> = emptyMap(),
    val semesterDetailedAttendance: Map<String, Map<String, List<DetailedAttendanceRecord>>> = emptyMap(),
    val loadingSemesterIds: Set<String> = emptySet(),
) {
    val selectedAttendanceData: List<AggregatedAttendance>
        get() = selectedSemesterId?.let { semesterAttendanceData[it] }.orEmpty()

    val selectedDetailedAttendance: Map<String, List<DetailedAttendanceRecord>>
        get() = selectedSemesterId?.let { semesterDetailedAttendance[it] }.orEmpty()

    sealed interface ViewState {
        data object Loading : ViewState
        data class Error(val message: UiText) : ViewState
        data object Content : ViewState
    }
}
