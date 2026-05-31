package edu.watumull.presencify.feature.schedule.add_edit_timetable

import edu.watumull.presencify.core.domain.enums.SemesterNumber
import edu.watumull.presencify.core.domain.model.academics.Batch
import edu.watumull.presencify.core.domain.model.academics.Branch
import edu.watumull.presencify.core.domain.model.academics.Division

sealed interface AddEditTimetableAction {
    data object NavigateBack : AddEditTimetableAction
    data object ConfirmNavigateBack : AddEditTimetableAction
    data object DismissDialog : AddEditTimetableAction

    data class SelectBranch(val branch: Branch?) : AddEditTimetableAction
    data class SelectSemesterNumber(val semesterNumber: SemesterNumber?) : AddEditTimetableAction
    data class UpdateStartYear(val year: String) : AddEditTimetableAction
    data class UpdateEndYear(val year: String) : AddEditTimetableAction

    data class ChangeBranchDropDownVisibility(val isOpen: Boolean) : AddEditTimetableAction
    data class ChangeSemesterNumberDropDownVisibility(val isOpen: Boolean) : AddEditTimetableAction

    data object FindDivisionsAndBatchesClick : AddEditTimetableAction

    // Division selection after finding divisions
    data class SelectDivision(val division: Division?) : AddEditTimetableAction
    data class ChangeDivisionDropDownVisibility(val isOpen: Boolean) : AddEditTimetableAction

    // Batch selection (multiple)
    data class ToggleBatchSelection(val batch: Batch) : AddEditTimetableAction

    // Timetable version
    data class UpdateTimetableVersion(val version: String) : AddEditTimetableAction

    data object SaveTimetableClick : AddEditTimetableAction
}
