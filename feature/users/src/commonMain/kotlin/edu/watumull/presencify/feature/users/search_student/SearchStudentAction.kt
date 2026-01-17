package edu.watumull.presencify.feature.users.search_student

import edu.watumull.presencify.core.domain.enums.AdmissionType
import edu.watumull.presencify.core.domain.enums.SemesterNumber
import edu.watumull.presencify.core.domain.model.academics.Batch
import edu.watumull.presencify.core.domain.model.academics.Branch
import edu.watumull.presencify.core.domain.model.academics.Division
import edu.watumull.presencify.core.domain.model.academics.Scheme

sealed interface SearchStudentAction {
    data object BackButtonClick : SearchStudentAction
    data object DismissDialog : SearchStudentAction

    data object ShowBottomSheet: SearchStudentAction
    data object HideBottomSheet: SearchStudentAction

    // Search & Refresh
    data class UpdateSearchQuery(val query: String) : SearchStudentAction
    data object Search : SearchStudentAction
    data object Refresh : SearchStudentAction

    // Filters
    data class ToggleBranch(val branch: Branch) : SearchStudentAction
    data class ToggleSemester(val semester: SemesterNumber) : SearchStudentAction
    data class SelectAcademicYearOfSemester(val year: String?) : SearchStudentAction
    data class ToggleAdmissionType(val admissionType: AdmissionType) : SearchStudentAction
    data class SelectAdmissionYear(val year: String?) : SearchStudentAction
    data class SelectDropoutYear(val year: String?) : SearchStudentAction
    data class SelectScheme(val scheme: Scheme?) : SearchStudentAction
    data class SelectDivision(val division: Division?) : SearchStudentAction
    data class SelectBatch(val batch: Batch?) : SearchStudentAction

    data object ResetFilters : SearchStudentAction
    data object ApplyFilters : SearchStudentAction

    // Student Selection
    data class ToggleStudentSelection(val studentId: String) : SearchStudentAction
    data class StudentCardClick(val studentId: String) : SearchStudentAction

    // Pagination
    data object LoadMoreStudents : SearchStudentAction

    // Done button for selection mode
    data object DoneButtonClick : SearchStudentAction

    data object ClickFloatingActionButton : SearchStudentAction
}

