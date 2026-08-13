package edu.watumull.presencify.feature.schedule.search_class

import edu.watumull.presencify.core.domain.enums.CourseType
import edu.watumull.presencify.core.domain.enums.SemesterNumber
import edu.watumull.presencify.core.domain.model.academics.Batch
import edu.watumull.presencify.core.domain.model.academics.Branch
import edu.watumull.presencify.core.domain.model.academics.Division
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

sealed interface SearchClassAction {
    data object NavigateBack : SearchClassAction

    data class UpdateSearchQuery(val query: String) : SearchClassAction
    data object Search : SearchClassAction
    data object Refresh : SearchClassAction

    data class ToggleBranch(val branch: Branch) : SearchClassAction
    data class ToggleSemester(val semester: SemesterNumber) : SearchClassAction
    data class UpdateAcademicStartYear(val year: String) : SearchClassAction
    data class UpdateAcademicEndYear(val year: String) : SearchClassAction

    data class SelectDivision(val division: Division?) : SearchClassAction
    data class SelectBatch(val batch: Batch?) : SearchClassAction

    data class ToggleCourseType(val courseType: CourseType) : SearchClassAction

    data class UpdateIsExtraClass(val isExtraClass: Boolean?) : SearchClassAction

    data class UpdateStartTime(val time: LocalTime?) : SearchClassAction
    data class UpdateEndTime(val time: LocalTime?) : SearchClassAction

    data class UpdateActiveFrom(val date: LocalDate?) : SearchClassAction
    data class UpdateActiveTill(val date: LocalDate?) : SearchClassAction

    data class ToggleRoom(val roomId: String) : SearchClassAction

    data class ToggleTeacher(val teacherId: String) : SearchClassAction

    data object ResetFilters : SearchClassAction
    data object ApplyFilters : SearchClassAction

    data class ClassCardClick(val classId: String) : SearchClassAction

    data object LoadMoreClasses : SearchClassAction
}
