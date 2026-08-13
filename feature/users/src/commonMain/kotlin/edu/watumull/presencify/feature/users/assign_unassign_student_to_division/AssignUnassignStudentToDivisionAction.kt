package edu.watumull.presencify.feature.users.assign_unassign_student_to_division

import edu.watumull.presencify.core.domain.enums.SemesterNumber
import edu.watumull.presencify.core.domain.model.academics.Branch
import edu.watumull.presencify.core.domain.model.academics.Division

sealed interface AssignUnassignStudentToDivisionAction {
    data object NavigateBack : AssignUnassignStudentToDivisionAction
    data object DismissDialog : AssignUnassignStudentToDivisionAction

    data class SelectBranch(val branch: Branch?) : AssignUnassignStudentToDivisionAction
    data class SelectSemesterNumber(val semesterNumber: SemesterNumber?) : AssignUnassignStudentToDivisionAction
    data class UpdateStartYear(val year: String) : AssignUnassignStudentToDivisionAction
    data class UpdateEndYear(val year: String) : AssignUnassignStudentToDivisionAction

    data class ChangeBranchDropDownVisibility(val isOpen: Boolean) : AssignUnassignStudentToDivisionAction
    data class ChangeSemesterNumberDropDownVisibility(val isOpen: Boolean) : AssignUnassignStudentToDivisionAction

    data object FindDivisionsClick : AssignUnassignStudentToDivisionAction

    data class SelectDivision(val division: Division?) : AssignUnassignStudentToDivisionAction
    data class ChangeDivisionDropDownVisibility(val isOpen: Boolean) : AssignUnassignStudentToDivisionAction
    data object NavigateToSearchStudentClick : AssignUnassignStudentToDivisionAction
}
