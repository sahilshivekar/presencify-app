package edu.watumull.presencify.feature.schedule.timetable_details

import edu.watumull.presencify.core.domain.enums.DayOfWeek

sealed interface TimetableDetailsAction {
    data object NavigateBack : TimetableDetailsAction
    data object DismissDialog : TimetableDetailsAction
    data object RemoveTimetableClick : TimetableDetailsAction
    data object ConfirmRemoveTimetable : TimetableDetailsAction
    data object EditTimetableClick : TimetableDetailsAction
    data object AddClassClick : TimetableDetailsAction
    data class DayTabClick(val day: DayOfWeek) : TimetableDetailsAction
    data class ClassClick(val classId: String) : TimetableDetailsAction
    data object ToggleShowInactiveClasses : TimetableDetailsAction
}
