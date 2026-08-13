package edu.watumull.presencify.feature.academics.search_course

import edu.watumull.presencify.core.domain.enums.SemesterNumber
import edu.watumull.presencify.core.domain.model.academics.Branch
import edu.watumull.presencify.core.domain.model.academics.Scheme

sealed interface SearchCourseAction {
    data object NavigateBack : SearchCourseAction


    data class UpdateSearchQuery(val query: String) : SearchCourseAction
    data object Search : SearchCourseAction
    data object Refresh : SearchCourseAction

    data class SelectSemesterNumber(val semesterNumber: SemesterNumber?) : SearchCourseAction
    data class SelectBranch(val branch: Branch?) : SearchCourseAction
    data class SelectScheme(val scheme: Scheme?) : SearchCourseAction
    data class ToggleTeacherSelection(val teacherId: String) : SearchCourseAction

    data object ResetFilters : SearchCourseAction
    data object ApplyFilters : SearchCourseAction

    data class CourseCardClick(val courseId: String) : SearchCourseAction

    data class CourseActionButtonClick(val courseId: String) : SearchCourseAction

    data object LoadMoreCourses : SearchCourseAction

    data object ClickFloatingActionButton : SearchCourseAction
}
