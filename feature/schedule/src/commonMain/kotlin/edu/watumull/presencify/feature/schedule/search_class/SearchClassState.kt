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

    val intention: SearchClassIntention = SearchClassIntention.DEFAULT,

    val searchQuery: String = "",
    val isRefreshing: Boolean = false,

    val classes: PersistentList<ClassSession> = persistentListOf(),
    val isLoadingClasses: Boolean = true,

    val branchOptions: PersistentList<Branch> = persistentListOf(),
    val selectedBranches: PersistentList<Branch> = persistentListOf(),
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

    val courseTypeOptions: ImmutableList<CourseType> = CourseType.entries.toImmutableList(),
    val selectedCourseTypes: PersistentList<CourseType> = persistentListOf(),

    val isExtraClass: Boolean? = null,

    val startTime: LocalTime? = null,
    val endTime: LocalTime? = null,
    val startTimeError: String? = null,
    val endTimeError: String? = null,

    val activeFrom: LocalDate? = null,
    val activeTill: LocalDate? = null,
    val activeFromError: String? = null,
    val activeTillError: String? = null,

    val availableRooms: PersistentList<Room> = persistentListOf(),
    val selectedRoomIds: PersistentList<String> = persistentListOf(),
    val isLoadingRooms: Boolean = false,

    val availableTeachers: PersistentList<Teacher> = persistentListOf(),
    val selectedTeacherIds: PersistentList<String> = persistentListOf(),
    val isLoadingTeachers: Boolean = false,

    val currentPage: Int = 1,
    val isLoadingMore: Boolean = false,

    val academicStartYearError: String? = null,
    val academicEndYearError: String? = null,
) {
    sealed interface ViewState {
        data object Loading : ViewState
        data class Error(val message: UiText) : ViewState
        data object Content : ViewState
    }
}


