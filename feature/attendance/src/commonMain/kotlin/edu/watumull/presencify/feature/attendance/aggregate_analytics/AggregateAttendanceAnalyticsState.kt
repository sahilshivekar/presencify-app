package edu.watumull.presencify.feature.attendance.aggregate_analytics

import edu.watumull.presencify.core.designsystem.components.dialog.DialogType
import edu.watumull.presencify.core.domain.enums.SemesterNumber
import edu.watumull.presencify.core.domain.model.academics.Batch
import edu.watumull.presencify.core.domain.model.academics.Branch
import edu.watumull.presencify.core.domain.model.academics.Division
import edu.watumull.presencify.core.domain.model.academics.Semester
import edu.watumull.presencify.core.domain.model.attendance.AggregatedAttendance
import edu.watumull.presencify.core.domain.model.attendance.AttendanceRecord
import edu.watumull.presencify.core.presentation.UiText

data class AggregateAttendanceAnalyticsState(
    val viewState: ViewState = ViewState.Content,
    val dialogState: DialogState? = null,

    // Filter fields
    val selectedSemesterNumber: SemesterNumber? = null,
    val academicStartYear: String = "",
    val academicEndYear: String = "",
    val selectedBranch: Branch? = null,
    val selectedDivision: Division? = null,
    val selectedBatch: Batch? = null,

    // Dropdown visibility
    val isSemesterNumberDropdownOpen: Boolean = false,
    val isBranchDropdownOpen: Boolean = false,
    val isDivisionDropdownOpen: Boolean = false,
    val isBatchDropdownOpen: Boolean = false,

    // Options
    val branchOptions: List<Branch> = emptyList(),
    val divisionOptions: List<Division> = emptyList(),
    val batchOptions: List<Batch> = emptyList(),

    // Loading states
    val areBranchesLoading: Boolean = false,
    val areDivisionsLoading: Boolean = false,
    val areBatchesLoading: Boolean = false,
    val isLoadingAttendance: Boolean = false,

    // Resolved semester
    val semester: Semester? = null,

    // Attendance data
    val attendanceData: List<AggregatedAttendance> = emptyList(),

    // Detailed attendance records per course: Map<CourseId, List<AttendanceRecord>>
    val detailedAttendanceRecords: Map<String, List<AttendanceRecord>> = emptyMap(),
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
