package edu.watumull.presencify.feature.users.assign_unassign_student_to_semester

import edu.watumull.presencify.core.domain.enums.SemesterNumber
import edu.watumull.presencify.core.domain.model.academics.Branch

sealed interface AssignUnassignStudentToSemesterAction {
    data object NavigateBack : AssignUnassignStudentToSemesterAction
    data object DismissDialog : AssignUnassignStudentToSemesterAction

    data class SelectBranch(val branch: Branch?) : AssignUnassignStudentToSemesterAction
    data class SelectSemesterNumber(val semesterNumber: SemesterNumber?) : AssignUnassignStudentToSemesterAction
    data class UpdateStartYear(val year: String) : AssignUnassignStudentToSemesterAction
    data class UpdateEndYear(val year: String) : AssignUnassignStudentToSemesterAction

    data class ChangeBranchDropDownVisibility(val isOpen: Boolean) : AssignUnassignStudentToSemesterAction
    data class ChangeSemesterNumberDropDownVisibility(val isOpen: Boolean) : AssignUnassignStudentToSemesterAction

    data object FindAndNavigateClick : AssignUnassignStudentToSemesterAction
}
