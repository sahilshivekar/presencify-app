package edu.watumull.presencify.feature.users.modify_student_division

import edu.watumull.presencify.core.domain.enums.SemesterNumber
import edu.watumull.presencify.core.domain.model.academics.Branch
import edu.watumull.presencify.core.domain.model.academics.Division
import kotlinx.datetime.LocalDate

sealed interface ModifyStudentDivisionAction {
    data object NavigateBack : ModifyStudentDivisionAction
    data object DismissDialog : ModifyStudentDivisionAction

    data class SelectBranch(val branch: Branch?) : ModifyStudentDivisionAction
    data class SelectSemesterNumber(val semesterNumber: SemesterNumber?) : ModifyStudentDivisionAction
    data class UpdateStartYear(val year: String) : ModifyStudentDivisionAction
    data class UpdateEndYear(val year: String) : ModifyStudentDivisionAction

    data class ChangeBranchDropDownVisibility(val isOpen: Boolean) : ModifyStudentDivisionAction
    data class ChangeSemesterNumberDropDownVisibility(val isOpen: Boolean) : ModifyStudentDivisionAction

    data object FindDivisionsClick : ModifyStudentDivisionAction

    data class SelectDivision(val division: Division?) : ModifyStudentDivisionAction
    data class ChangeDivisionDropDownVisibility(val isOpen: Boolean) : ModifyStudentDivisionAction

    data class UpdateNewDivisionStartDate(val date: LocalDate?) : ModifyStudentDivisionAction

    data object NavigateToSearchStudentClick : ModifyStudentDivisionAction
}
