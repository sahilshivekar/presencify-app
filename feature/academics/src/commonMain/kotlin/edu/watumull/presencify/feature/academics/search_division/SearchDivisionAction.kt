package edu.watumull.presencify.feature.academics.search_division

import edu.watumull.presencify.core.domain.enums.SemesterNumber
import edu.watumull.presencify.core.domain.model.academics.Branch

sealed interface SearchDivisionAction {
    data object NavigateBack : SearchDivisionAction


    // Search & Refresh
    data class UpdateSearchQuery(val query: String) : SearchDivisionAction
    data object Search : SearchDivisionAction
    data object Refresh : SearchDivisionAction

    // Filters
    data class SelectSemesterNumber(val semesterNumber: SemesterNumber?) : SearchDivisionAction
    data class UpdateAcademicStartYear(val year: String) : SearchDivisionAction
    data class UpdateAcademicEndYear(val year: String) : SearchDivisionAction
    data class SelectBranch(val branch: Branch?) : SearchDivisionAction

    data object ResetFilters : SearchDivisionAction
    data object ApplyFilters : SearchDivisionAction

    // Division Card Click
    data class DivisionCardClick(val divisionId: String) : SearchDivisionAction

    // Pagination
    data object LoadMoreDivisions : SearchDivisionAction

    data object ClickFloatingActionButton : SearchDivisionAction
}

