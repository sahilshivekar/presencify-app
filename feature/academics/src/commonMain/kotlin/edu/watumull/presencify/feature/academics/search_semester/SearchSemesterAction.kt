package edu.watumull.presencify.feature.academics.search_semester

import edu.watumull.presencify.core.domain.enums.SemesterNumber
import edu.watumull.presencify.core.domain.model.academics.Branch
import edu.watumull.presencify.core.domain.model.academics.Scheme

sealed interface SearchSemesterAction {
    data object BackButtonClick : SearchSemesterAction
    data object DismissDialog : SearchSemesterAction

    data object ShowBottomSheet: SearchSemesterAction
    data object HideBottomSheet: SearchSemesterAction

    // Search & Refresh
    data class UpdateSearchQuery(val query: String) : SearchSemesterAction
    data object Search : SearchSemesterAction
    data object Refresh : SearchSemesterAction

    // Filters
    data class SelectSemesterNumber(val semesterNumber: SemesterNumber?) : SearchSemesterAction
    data class SelectAcademicYear(val year: String?) : SearchSemesterAction
    data class ToggleBranch(val branch: Branch) : SearchSemesterAction
    data class SelectScheme(val scheme: Scheme?) : SearchSemesterAction

    data object ResetFilters : SearchSemesterAction
    data object ApplyFilters : SearchSemesterAction

    // Semester Card Click
    data class SemesterCardClick(val semesterId: String) : SearchSemesterAction

    // Pagination
    data object LoadMoreSemesters : SearchSemesterAction

    data object ClickFloatingActionButton : SearchSemesterAction
}

