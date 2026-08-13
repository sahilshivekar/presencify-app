package edu.watumull.presencify.feature.attendance.student_analytics

import edu.watumull.presencify.core.domain.model.attendance.AggregatedAttendance
import edu.watumull.presencify.core.domain.model.attendance.DetailedAttendanceRecord
import edu.watumull.presencify.core.domain.model.student.Student
import edu.watumull.presencify.core.presentation.UiText
import edu.watumull.presencify.core.presentation.components.dialog.DialogState

data class StudentAttendanceAnalyticsState(
    val viewState: ViewState = ViewState.Loading,
    val dialogState: DialogState? = null,

    val student: Student? = null,
    val studentId: String? = null,

    val expandedSemesterIds: Set<String> = emptySet(),

    val semesterAttendanceData: Map<String, List<AggregatedAttendance>> = emptyMap(),

    val semesterDetailedAttendance: Map<String, Map<String, List<DetailedAttendanceRecord>>> = emptyMap(),

    val loadingSemesterIds: Set<String> = emptySet(),
) {
    sealed interface ViewState {
        data object Loading : ViewState
        data class Error(val message: UiText) : ViewState
        data object Content : ViewState
    }
}
