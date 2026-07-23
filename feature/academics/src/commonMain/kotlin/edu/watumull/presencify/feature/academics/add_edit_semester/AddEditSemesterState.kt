package edu.watumull.presencify.feature.academics.add_edit_semester

import edu.watumull.presencify.core.domain.enums.SemesterNumber
import edu.watumull.presencify.core.domain.model.academics.Branch
import edu.watumull.presencify.core.domain.model.academics.Scheme
import edu.watumull.presencify.core.presentation.components.dialog.DialogState
import kotlinx.datetime.LocalDate

data class AddEditSemesterState(
    val semesterId: String? = null,
    val isEditMode: Boolean = false,
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,

    val semesterNumber: SemesterNumber? = null,
    val academicStartYear: String = "",
    val academicEndYear: String = "",
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val selectedBranchId: String = "",
    val selectedSchemeId: String = "",

    val branchOptions: List<Branch> = emptyList(),
    val schemeOptions: List<Scheme> = emptyList(),

    val semesterNumberError: String? = null,
    val academicStartYearError: String? = null,
    val academicEndYearError: String? = null,
    val startDateError: String? = null,
    val endDateError: String? = null,
    val branchError: String? = null,
    val schemeError: String? = null,

    val isSemesterNumberDropdownOpen: Boolean = false,
    val isBranchDropdownOpen: Boolean = false,
    val isSchemeDropdownOpen: Boolean = false,
    val isStartDatePickerVisible: Boolean = false,
    val isEndDatePickerVisible: Boolean = false,

    val dialogState: DialogState? = null,
)
