package edu.watumull.presencify.feature.schedule.search_class

import edu.watumull.presencify.core.domain.enums.CourseType
import edu.watumull.presencify.core.domain.enums.SemesterNumber
import edu.watumull.presencify.core.domain.model.academics.Batch
import edu.watumull.presencify.core.domain.model.academics.Branch
import edu.watumull.presencify.core.domain.model.academics.Division
import edu.watumull.presencify.core.domain.model.schedule.ClassSession
import edu.watumull.presencify.core.domain.model.schedule.Room
import edu.watumull.presencify.core.domain.model.teacher.Teacher
import edu.watumull.presencify.core.presentation.UiText
import edu.watumull.presencify.feature.schedule.navigation.SearchClassIntention
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

data class SearchClassState(
    val viewState: ViewState = ViewState.Content,

    // Intention
    val intention: SearchClassIntention = SearchClassIntention.DEFAULT,

    // Search & Filter
    val searchQuery: String = "",
    val isRefreshing: Boolean = false,

    // Classes List
    val classes: PersistentList<ClassSession> = persistentListOf(),
    val isLoadingClasses: Boolean = true,

    // Filter Options - Branch
    val branchOptions: PersistentList<Branch> = persistentListOf(),
    val selectedBranches: PersistentList<Branch> = persistentListOf(),
    val areBranchesLoading: Boolean = true,

    // Filter Options - Semester
    val semesterOptions: ImmutableList<SemesterNumber> = SemesterNumber.entries.toImmutableList(),
    val selectedSemesters: PersistentList<SemesterNumber> = persistentListOf(),

    // Filter Options - Academic Year of Semester
    val academicStartYear: String = "",
    val academicEndYear: String = "",

    // Filter Options - Division (loaded based on branch + semester + academic year)
    val divisionOptions: PersistentList<Division> = persistentListOf(),
    val selectedDivision: Division? = null,
    val areDivisionsLoading: Boolean = false,

    // Filter Options - Batch (loaded based on branch + semester + academic year)
    val batchOptions: PersistentList<Batch> = persistentListOf(),
    val selectedBatch: Batch? = null,
    val areBatchesLoading: Boolean = false,

    // Filter Options - Course Type
    val courseTypeOptions: ImmutableList<CourseType> = CourseType.entries.toImmutableList(),
    val selectedCourseTypes: PersistentList<CourseType> = persistentListOf(),

    // Filter Options - Extra Class
    val isExtraClass: Boolean? = null,

    // Filter Options - Time Range
    val startTime: LocalTime? = null,
    val endTime: LocalTime? = null,
    val startTimeError: String? = null,
    val endTimeError: String? = null,

    // Filter Options - Date Range
    val activeFrom: LocalDate? = null,
    val activeTill: LocalDate? = null,
    val activeFromError: String? = null,
    val activeTillError: String? = null,

    // Filter Options - Rooms
    val availableRooms: PersistentList<Room> = persistentListOf(),
    val selectedRoomIds: PersistentList<String> = persistentListOf(),
    val isLoadingRooms: Boolean = false,

    // Filter Options - Teachers
    val availableTeachers: PersistentList<Teacher> = persistentListOf(),
    val selectedTeacherIds: PersistentList<String> = persistentListOf(),
    val isLoadingTeachers: Boolean = false,

    // Pagination
    val currentPage: Int = 1,
    val isLoadingMore: Boolean = false
) {
    sealed interface ViewState {
        data object Loading : ViewState
        data class Error(val message: UiText) : ViewState
        data object Content : ViewState
    }
}


