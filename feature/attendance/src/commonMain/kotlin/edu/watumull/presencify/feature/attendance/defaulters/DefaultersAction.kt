package edu.watumull.presencify.feature.attendance.defaulters

import edu.watumull.presencify.core.domain.enums.SemesterNumber
import edu.watumull.presencify.core.domain.model.academics.Branch
import edu.watumull.presencify.core.domain.model.academics.Course
import kotlinx.datetime.LocalDate

sealed interface DefaultersAction {
    data class SelectSemesterNumber(val semesterNumber: SemesterNumber) : DefaultersAction
    data class ChangeSemesterNumberDropDownVisibility(val isVisible: Boolean) : DefaultersAction
    data class UpdateAcademicStartYear(val year: String) : DefaultersAction
    data class UpdateAcademicEndYear(val year: String) : DefaultersAction
    data class SelectBranch(val branch: Branch) : DefaultersAction
    data class ChangeBranchDropDownVisibility(val isVisible: Boolean) : DefaultersAction

    data class SelectCourse(val course: Course?) : DefaultersAction
    data class ChangeCourseDropDownVisibility(val isVisible: Boolean) : DefaultersAction

    data class SelectStartDate(val date: LocalDate?) : DefaultersAction
    data class ChangeStartDatePickerVisibility(val isVisible: Boolean) : DefaultersAction

    data class SelectEndDate(val date: LocalDate?) : DefaultersAction
    data class ChangeEndDatePickerVisibility(val isVisible: Boolean) : DefaultersAction

    data object GetDefaulters : DefaultersAction
    data object ExportCsv : DefaultersAction
    data object DismissDialog : DefaultersAction
}
