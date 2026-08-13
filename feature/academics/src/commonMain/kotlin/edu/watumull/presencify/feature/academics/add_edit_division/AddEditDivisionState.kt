package edu.watumull.presencify.feature.academics.add_edit_division

import edu.watumull.presencify.core.domain.enums.SemesterNumber
import edu.watumull.presencify.core.domain.model.academics.Branch
import edu.watumull.presencify.core.domain.model.academics.Course
import edu.watumull.presencify.core.domain.model.academics.Semester
import edu.watumull.presencify.core.presentation.components.dialog.DialogState

data class AddEditDivisionState(
    val divisionId: String? = null,
    val isEditMode: Boolean = false,
    val isLoadingDivision: Boolean = false,
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,

    val semesterNumber: SemesterNumber? = null,
    val academicStartYear: String = "",
    val academicEndYear: String = "",
    val selectedBranchId: String = "",

    val branchOptions: List<Branch> = emptyList(),

    val foundSemester: Semester? = null,
    val divisionCode: String = "",
    val showDivisionInput: Boolean = false,
    val isFetchingOptionalCourses: Boolean = false,
    val optionalCourseGroups: Map<String, List<Course>> = emptyMap(),
    val selectedOptionalCourses: Map<String, String> = emptyMap(),
    val openOptionalDropdowns: Set<String> = emptySet(),

    val semesterNumberError: String? = null,
    val academicStartYearError: String? = null,
    val academicEndYearError: String? = null,
    val branchError: String? = null,
    val divisionCodeError: String? = null,

    val isSemesterNumberDropdownOpen: Boolean = false,
    val isBranchDropdownOpen: Boolean = false,

    val dialogState: DialogState? = null,
)
