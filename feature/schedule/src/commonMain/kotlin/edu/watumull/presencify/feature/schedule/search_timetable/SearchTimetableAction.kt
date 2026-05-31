package edu.watumull.presencify.feature.schedule.search_timetable

import edu.watumull.presencify.core.domain.enums.SemesterNumber
import edu.watumull.presencify.core.domain.model.academics.Branch

sealed interface SearchTimetableAction {
    data object NavigateBack : SearchTimetableAction

    // Search & Refresh
    data class UpdateSearchQuery(val query: String) : SearchTimetableAction
    data object Search : SearchTimetableAction
    data object Refresh : SearchTimetableAction

    // Filters - Branch, Semester, Academic Year
    data class ToggleBranch(val branch: Branch) : SearchTimetableAction
    data class ToggleSemester(val semester: SemesterNumber) : SearchTimetableAction
    data class UpdateAcademicStartYear(val year: String) : SearchTimetableAction
    data class UpdateAcademicEndYear(val year: String) : SearchTimetableAction

    data object ResetFilters : SearchTimetableAction
    data object ApplyFilters : SearchTimetableAction

    // Timetable Actions
    data class TimetableCardClick(val timetableId: String) : SearchTimetableAction

    // Pagination
    data object LoadMoreTimetables : SearchTimetableAction

    data object ClickFloatingActionButton : SearchTimetableAction
}
