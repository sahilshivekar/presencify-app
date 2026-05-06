package edu.watumull.presencify.feature.attendance.student_analytics

import edu.watumull.presencify.core.designsystem.components.dialog.DialogType
import edu.watumull.presencify.core.domain.model.attendance.AggregatedAttendance
import edu.watumull.presencify.core.domain.model.attendance.DetailedAttendanceRecord
import edu.watumull.presencify.core.domain.model.student.Student
import edu.watumull.presencify.core.presentation.UiText

data class StudentAttendanceAnalyticsState(
    val viewState: ViewState = ViewState.Loading,
    val dialogState: DialogState? = null,

    // Student data
    val student: Student? = null,
    val studentId: String? = null,

    // Expanded semester IDs to show attendance
    val expandedSemesterIds: Set<String> = emptySet(),

    // Attendance data per semester
    // Map<SemesterId, List<AggregatedAttendance>>
    val semesterAttendanceData: Map<String, List<AggregatedAttendance>> = emptyMap(),

    // Detailed attendance records per semester
    // Map<SemesterId, Map<CourseId, List<DetailedAttendanceRecord>>>
    val semesterDetailedAttendance: Map<String, Map<String, List<DetailedAttendanceRecord>>> = emptyMap(),

    // Loading states for individual semesters
    val loadingSemesterIds: Set<String> = emptySet(),
) {
    sealed interface ViewState {
        data object Loading : ViewState
        data class Error(val message: UiText) : ViewState
        data object Content : ViewState
    }

    data class DialogState(
        val isVisible: Boolean = true,
        val dialogType: DialogType = DialogType.INFO,
        val dialogIntention: DialogIntention = DialogIntention.GENERIC,
        val title: String = "",
        val message: UiText? = null,
    )
}

enum class DialogIntention {
    GENERIC,
}
