package edu.watumull.presencify.feature.attendance.search_attendance

import androidx.compose.runtime.Stable
import edu.watumull.presencify.core.domain.enums.SemesterNumber
import edu.watumull.presencify.core.domain.model.academics.Batch
import edu.watumull.presencify.core.domain.model.academics.Branch
import edu.watumull.presencify.core.domain.model.academics.Course
import edu.watumull.presencify.core.domain.model.academics.Division
import edu.watumull.presencify.core.domain.model.attendance.Attendance
import edu.watumull.presencify.core.presentation.UiText
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.datetime.LocalDate

@Stable
data class SearchAttendanceState(
    val viewState: ViewState = ViewState.Content,

    val searchQuery: String = "",
    val isRefreshing: Boolean = false,

    val attendances: PersistentList<Attendance> = persistentListOf(),
    val isLoadingAttendances: Boolean = true,

    val selectedDate: LocalDate? = null,

    val branchOptions: PersistentList<Branch> = persistentListOf(),
    val selectedBranch: Branch? = null,
    val areBranchesLoading: Boolean = true,

    val semesterOptions: ImmutableList<SemesterNumber> = SemesterNumber.entries.toImmutableList(),
    val selectedSemesters: PersistentList<SemesterNumber> = persistentListOf(),

    val academicStartYear: String = "",
    val academicEndYear: String = "",


    val divisionOptions: PersistentList<Division> = persistentListOf(),
    val selectedDivision: Division? = null,
    val areDivisionsLoading: Boolean = false,

    val batchOptions: PersistentList<Batch> = persistentListOf(),
    val selectedBatch: Batch? = null,
    val areBatchesLoading: Boolean = false,

    val courseOptions: PersistentList<Course> = persistentListOf(),
    val selectedCourse: Course? = null,
    val areCoursesLoading: Boolean = false,

    val currentPage: Int = 1,
    val isLoadingMore: Boolean = false,

    val routeCourseId: String? = null,
    val isRouteCourseLoading: Boolean = false,
    val studentId: String? = null,
    val semesterId: String? = null,
    val divisionId: String? = null,
    val batchId: String? = null,
    val classId: String? = null,
    val startDate: String? = null,
    val endDate: String? = null
) {
    sealed interface ViewState {
        data object Loading : ViewState
        data object Content : ViewState
        data class Error(val message: UiText) : ViewState
    }
}

