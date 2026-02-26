package edu.watumull.presencify.feature.schedule.add_edit_class

import edu.watumull.presencify.core.domain.enums.ClassType
import edu.watumull.presencify.core.domain.enums.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

sealed interface AddEditClassAction {
    data object BackButtonClick : AddEditClassAction
    data object DismissDialog : AddEditClassAction

    // Class Details
    data class UpdateCourse(val courseId: String?) : AddEditClassAction
    data class ChangeCourseDropDownVisibility(val isVisible: Boolean) : AddEditClassAction

    data class UpdateTeacher(val teacherId: String?) : AddEditClassAction
    data class ChangeTeacherDropDownVisibility(val isVisible: Boolean) : AddEditClassAction

    data class UpdateRoom(val roomId: String?) : AddEditClassAction
    data class ChangeRoomDropDownVisibility(val isVisible: Boolean) : AddEditClassAction

    data class UpdateBatch(val batchId: String?) : AddEditClassAction
    data class ChangeBatchDropDownVisibility(val isVisible: Boolean) : AddEditClassAction

    data class UpdateClassType(val classType: ClassType?) : AddEditClassAction
    data class ChangeClassTypeDropDownVisibility(val isVisible: Boolean) : AddEditClassAction

    data class UpdateDayOfWeek(val dayOfWeek: DayOfWeek?) : AddEditClassAction
    data class ChangeDayOfWeekDropDownVisibility(val isVisible: Boolean) : AddEditClassAction

    data class UpdateStartTime(val startTime: LocalTime?) : AddEditClassAction
    data class UpdateEndTime(val endTime: LocalTime?) : AddEditClassAction

    data class UpdateActiveFrom(val activeFrom: LocalDate?) : AddEditClassAction
    data class UpdateActiveTill(val activeTill: LocalDate?) : AddEditClassAction

    data class UpdateIsExtraClass(val isExtraClass: Boolean) : AddEditClassAction

    // Submit
    data object SubmitClick : AddEditClassAction
}
