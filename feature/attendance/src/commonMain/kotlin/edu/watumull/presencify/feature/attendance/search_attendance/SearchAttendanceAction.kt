package edu.watumull.presencify.feature.attendance.search_attendance

import edu.watumull.presencify.core.domain.enums.SemesterNumber
import edu.watumull.presencify.core.domain.model.academics.Batch
import edu.watumull.presencify.core.domain.model.academics.Branch
import edu.watumull.presencify.core.domain.model.academics.Course
import edu.watumull.presencify.core.domain.model.academics.Division
import kotlinx.datetime.LocalDate

sealed interface SearchAttendanceAction {
    data object BackButtonClick : SearchAttendanceAction
    data object DismissDialog : SearchAttendanceAction

    // Search & Refresh
    data class UpdateSearchQuery(val query: String) : SearchAttendanceAction
    data object Search : SearchAttendanceAction
    data object Refresh : SearchAttendanceAction

    // Filters
    data class SelectDate(val date: LocalDate?) : SearchAttendanceAction
    data class ToggleBranch(val branch: Branch) : SearchAttendanceAction
    data class ToggleSemester(val semester: SemesterNumber) : SearchAttendanceAction
    data class SelectAcademicYearOfSemester(val year: String?) : SearchAttendanceAction
    data class SelectDivision(val division: Division?) : SearchAttendanceAction
    data class SelectBatch(val batch: Batch?) : SearchAttendanceAction
    data class SelectCourse(val course: Course?) : SearchAttendanceAction

    data object ResetFilters : SearchAttendanceAction
    data object ApplyFilters : SearchAttendanceAction

    // Attendance Actions
    data class AttendanceCardClick(val attendanceId: String) : SearchAttendanceAction

    // Pagination
    data object LoadMoreAttendances : SearchAttendanceAction
}
