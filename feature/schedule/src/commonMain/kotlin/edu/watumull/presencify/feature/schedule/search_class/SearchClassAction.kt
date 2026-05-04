package edu.watumull.presencify.feature.schedule.search_class

import edu.watumull.presencify.core.domain.enums.ClassType
import edu.watumull.presencify.core.domain.enums.SemesterNumber
import edu.watumull.presencify.core.domain.model.academics.Batch
import edu.watumull.presencify.core.domain.model.academics.Branch
import edu.watumull.presencify.core.domain.model.academics.Division
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

sealed interface SearchClassAction {
    data object BackButtonClick : SearchClassAction
    data object DismissDialog : SearchClassAction

    // Search & Refresh
    data class UpdateSearchQuery(val query: String) : SearchClassAction
    data object Search : SearchClassAction
    data object Refresh : SearchClassAction

    // Filters - Branch, Semester, Academic Year
    data class ToggleBranch(val branch: Branch) : SearchClassAction
    data class ToggleSemester(val semester: SemesterNumber) : SearchClassAction
    data class UpdateAcademicStartYear(val year: String) : SearchClassAction
    data class UpdateAcademicEndYear(val year: String) : SearchClassAction

    // Filters - Division and Batch (dependent on above filters)
    data class SelectDivision(val division: Division?) : SearchClassAction
    data class SelectBatch(val batch: Batch?) : SearchClassAction

    // Filters - Class Type
    data class ToggleClassType(val classType: ClassType) : SearchClassAction

    // Filters - Extra Class
    data class UpdateIsExtraClass(val isExtraClass: Boolean?) : SearchClassAction

    // Filters - Time Range
    data class UpdateStartTime(val time: LocalTime?) : SearchClassAction
    data class UpdateEndTime(val time: LocalTime?) : SearchClassAction

    // Filters - Date Range
    data class UpdateActiveFrom(val date: LocalDate?) : SearchClassAction
    data class UpdateActiveTill(val date: LocalDate?) : SearchClassAction

    // Filters - Room
    data class ToggleRoom(val roomId: String) : SearchClassAction

    // Filters - Teacher
    data class ToggleTeacher(val teacherId: String) : SearchClassAction

    data object ResetFilters : SearchClassAction
    data object ApplyFilters : SearchClassAction

    // Class Actions
    data class ClassCardClick(val classId: String) : SearchClassAction

    // Pagination
    data object LoadMoreClasses : SearchClassAction
}
