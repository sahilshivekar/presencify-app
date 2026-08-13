package edu.watumull.presencify.feature.attendance.aggregate_analytics

import edu.watumull.presencify.core.domain.enums.SemesterNumber
import edu.watumull.presencify.core.domain.model.academics.Batch
import edu.watumull.presencify.core.domain.model.academics.Branch
import edu.watumull.presencify.core.domain.model.academics.Division

sealed interface AggregateAttendanceAnalyticsAction {
    data object NavigateBack : AggregateAttendanceAnalyticsAction
    data object DismissDialog : AggregateAttendanceAnalyticsAction

    data class DonutCourseClick(val courseId: String) : AggregateAttendanceAnalyticsAction

    data class SelectSemesterNumber(val semesterNumber: SemesterNumber) : AggregateAttendanceAnalyticsAction
    data class UpdateAcademicStartYear(val year: String) : AggregateAttendanceAnalyticsAction
    data class UpdateAcademicEndYear(val year: String) : AggregateAttendanceAnalyticsAction
    data class SelectBranch(val branch: Branch) : AggregateAttendanceAnalyticsAction
    data class SelectDivision(val division: Division?) : AggregateAttendanceAnalyticsAction
    data class SelectBatch(val batch: Batch?) : AggregateAttendanceAnalyticsAction

    data class ChangeSemesterNumberDropDownVisibility(val isVisible: Boolean) : AggregateAttendanceAnalyticsAction
    data class ChangeBranchDropDownVisibility(val isVisible: Boolean) : AggregateAttendanceAnalyticsAction
    data class ChangeDivisionDropDownVisibility(val isVisible: Boolean) : AggregateAttendanceAnalyticsAction
    data class ChangeBatchDropDownVisibility(val isVisible: Boolean) : AggregateAttendanceAnalyticsAction
}
