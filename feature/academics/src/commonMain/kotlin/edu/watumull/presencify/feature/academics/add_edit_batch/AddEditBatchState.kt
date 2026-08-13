package edu.watumull.presencify.feature.academics.add_edit_batch

import edu.watumull.presencify.core.domain.enums.SemesterNumber
import edu.watumull.presencify.core.domain.model.academics.Branch
import edu.watumull.presencify.core.domain.model.academics.Division
import edu.watumull.presencify.core.domain.model.academics.Semester
import edu.watumull.presencify.core.presentation.components.dialog.DialogState

data class AddEditBatchState(
    val batchId: String? = null,
    val isEditMode: Boolean = false,
    val isLoadingBatchDetails: Boolean = false,
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,

    val semesterNumber: SemesterNumber? = null,
    val academicStartYear: String = "",
    val academicEndYear: String = "",
    val selectedBranchId: String = "",

    val branchOptions: List<Branch> = emptyList(),
    val foundDivision: Division? = null,
    val foundSemester: Semester? = null,

    val foundDivisions: List<Division> = emptyList(),
    val selectedDivisionId: String = "",
    val showDivisionInput: Boolean = false,

    val batchCode: String = "",
    val showBatchInput: Boolean = false,

    val semesterNumberError: String? = null,
    val academicStartYearError: String? = null,
    val academicEndYearError: String? = null,
    val branchError: String? = null,
    val divisionError: String? = null,
    val batchCodeError: String? = null,

    val isSemesterNumberDropdownOpen: Boolean = false,
    val isBranchDropdownOpen: Boolean = false,
    val isDivisionDropdownOpen: Boolean = false,

    val dialogState: DialogState? = null,
)
