package edu.watumull.presencify.feature.users.modify_student_batch

import edu.watumull.presencify.core.domain.enums.SemesterNumber
import edu.watumull.presencify.core.domain.model.academics.Batch
import edu.watumull.presencify.core.domain.model.academics.Branch
import kotlinx.datetime.LocalDate

sealed interface ModifyStudentBatchAction {
    data object BackButtonClick : ModifyStudentBatchAction
    data object DismissDialog : ModifyStudentBatchAction

    data class SelectBranch(val branch: Branch?) : ModifyStudentBatchAction
    data class SelectSemesterNumber(val semesterNumber: SemesterNumber?) : ModifyStudentBatchAction
    data class UpdateStartYear(val year: String) : ModifyStudentBatchAction
    data class UpdateEndYear(val year: String) : ModifyStudentBatchAction

    data class ChangeBranchDropDownVisibility(val isOpen: Boolean) : ModifyStudentBatchAction
    data class ChangeSemesterNumberDropDownVisibility(val isOpen: Boolean) : ModifyStudentBatchAction

    data object FindBatchesClick : ModifyStudentBatchAction

    // Batch selection after finding batches
    data class SelectBatch(val batch: Batch?) : ModifyStudentBatchAction
    data class ChangeBatchDropDownVisibility(val isOpen: Boolean) : ModifyStudentBatchAction

    // Date picker actions
    data class ChangeDatePickerVisibility(val isVisible: Boolean) : ModifyStudentBatchAction
    data class UpdateNewBatchStartDate(val date: LocalDate?) : ModifyStudentBatchAction

    data object NavigateToSearchStudentClick : ModifyStudentBatchAction
}
