package edu.watumull.presencify.feature.users.assign_unassign_student_to_batch

import edu.watumull.presencify.core.domain.enums.SemesterNumber
import edu.watumull.presencify.core.domain.model.academics.Batch
import edu.watumull.presencify.core.domain.model.academics.Branch

sealed interface AssignUnassignStudentToBatchAction {
    data object NavigateBack : AssignUnassignStudentToBatchAction
    data object DismissDialog : AssignUnassignStudentToBatchAction

    data class SelectBranch(val branch: Branch?) : AssignUnassignStudentToBatchAction
    data class SelectSemesterNumber(val semesterNumber: SemesterNumber?) : AssignUnassignStudentToBatchAction
    data class UpdateStartYear(val year: String) : AssignUnassignStudentToBatchAction
    data class UpdateEndYear(val year: String) : AssignUnassignStudentToBatchAction

    data class ChangeBranchDropDownVisibility(val isOpen: Boolean) : AssignUnassignStudentToBatchAction
    data class ChangeSemesterNumberDropDownVisibility(val isOpen: Boolean) : AssignUnassignStudentToBatchAction


    data object FindBatchesClick : AssignUnassignStudentToBatchAction

    // Batch selection after finding batches
    data class SelectBatch(val batch: Batch?) : AssignUnassignStudentToBatchAction
    data class ChangeBatchDropDownVisibility(val isOpen: Boolean) : AssignUnassignStudentToBatchAction
    data object NavigateToSearchStudentClick : AssignUnassignStudentToBatchAction
}
