package edu.watumull.presencify.feature.attendance.search_attendance

import androidx.compose.runtime.Stable
import edu.watumull.presencify.core.design.systems.components.dialog.DialogType
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
    val dialogState: DialogState? = null,

    // Search & Filter
    val searchQuery: String = "",
    val isRefreshing: Boolean = false,

    // Attendance List
    val attendances: PersistentList<Attendance> = persistentListOf(),
    val isLoadingAttendances: Boolean = true,

    // Filter Options - Date
    val selectedDate: LocalDate? = null,

    // Filter Options - Branches
    val branchOptions: PersistentList<Branch> = persistentListOf(),
    val selectedBranch: Branch? = null,
    val areBranchesLoading: Boolean = true,

    // Filter Options - Semesters
    val semesterOptions: ImmutableList<SemesterNumber> = SemesterNumber.entries.toImmutableList(),
    val selectedSemesters: PersistentList<SemesterNumber> = persistentListOf(),

    // Filter Options - Academic Year of Semester
    val academicStartYear: String = "",
    val academicEndYear: String = "",


    // Filter Options - Division
    val divisionOptions: PersistentList<Division> = persistentListOf(),
    val selectedDivision: Division? = null,
    val areDivisionsLoading: Boolean = false,

    // Filter Options - Batch
    val batchOptions: PersistentList<Batch> = persistentListOf(),
    val selectedBatch: Batch? = null,
    val areBatchesLoading: Boolean = false,

    // Filter Options - Courses (fetched based on semester selection)
    val courseOptions: PersistentList<Course> = persistentListOf(),
    val selectedCourse: Course? = null,
    val areCoursesLoading: Boolean = false,

    // Pagination
    val currentPage: Int = 1,
    val isLoadingMore: Boolean = false,

    // Parameters passed via navigation (if any)
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

    data class DialogState(
        val isVisible: Boolean = true,
        val dialogType: DialogType,
        val title: String,
        val message: UiText,
        val dialogIntention: DialogIntention = DialogIntention.GENERIC
    )

    enum class DialogIntention {
        GENERIC
    }
}

