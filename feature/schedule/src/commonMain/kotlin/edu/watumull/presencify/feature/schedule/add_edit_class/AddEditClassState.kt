package edu.watumull.presencify.feature.schedule.add_edit_class

import edu.watumull.presencify.core.designsystem.components.dialog.DialogType
import edu.watumull.presencify.core.domain.enums.ClassType
import edu.watumull.presencify.core.domain.enums.DayOfWeek
import edu.watumull.presencify.core.domain.model.academics.Batch
import edu.watumull.presencify.core.domain.model.academics.Course
import edu.watumull.presencify.core.domain.model.schedule.Room
import edu.watumull.presencify.core.domain.model.teacher.Teacher
import edu.watumull.presencify.core.presentation.UiText
import edu.watumull.presencify.core.presentation.components.dialog.DialogState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

data class AddEditClassState(
    val viewState: ViewState = ViewState.Content,
    val dialogState: DialogState? = null,

    // Mode
    val isEditMode: Boolean = false,
    val classId: String? = null,
    val timetableId: String = "",

    // Class Details
    val selectedCourse: Course? = null,
    val selectedCourseError: String? = null,
    val availableCourses: ImmutableList<Course> = persistentListOf(),
    val isLoadingCourses: Boolean = false,
    val isCourseDropdownOpen: Boolean = false,

    val selectedTeacher: Teacher? = null,
    val selectedTeacherError: String? = null,
    val availableTeachers: ImmutableList<Teacher> = persistentListOf(),
    val isLoadingTeachers: Boolean = false,
    val isTeacherDropdownOpen: Boolean = false,

    val selectedRoom: Room? = null,
    val selectedRoomError: String? = null,
    val availableRooms: ImmutableList<Room> = persistentListOf(),
    val isLoadingRooms: Boolean = false,
    val isRoomDropdownOpen: Boolean = false,

    val selectedBatch: Batch? = null,
    val availableBatches: ImmutableList<Batch> = persistentListOf(),
    val isLoadingBatches: Boolean = false,
    val isBatchDropdownOpen: Boolean = false,

    val classType: ClassType? = null,
    val classTypeError: String? = null,
    val classTypeOptions: ImmutableList<ClassType> = ClassType.entries.toImmutableList(),
    val isClassTypeDropdownOpen: Boolean = false,

    val dayOfWeek: DayOfWeek? = null,
    val dayOfWeekError: String? = null,
    val dayOfWeekOptions: ImmutableList<DayOfWeek> = DayOfWeek.entries.toImmutableList(),
    val isDayOfWeekDropdownOpen: Boolean = false,

    val startTime: LocalTime? = null,
    val startTimeError: String? = null,

    val endTime: LocalTime? = null,
    val endTimeError: String? = null,

    val activeFrom: LocalDate? = null,
    val activeFromError: String? = null,

    val activeTill: LocalDate? = null,
    val activeTillError: String? = null,

    val isExtraClass: Boolean = false,

    // Loading
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false
) {
    sealed interface ViewState {
        data object Loading : ViewState
        data class Error(val message: UiText) : ViewState
        data object Content : ViewState
    }
}
