package edu.watumull.presencify.feature.schedule.navigation

import androidx.navigation.NavGraphBuilder
import edu.watumull.presencify.core.design.systems.components.composableWithSlideTransitions
import edu.watumull.presencify.feature.schedule.add_edit_class.AddEditClassRoot
import edu.watumull.presencify.feature.schedule.add_edit_room.AddEditRoomRoot
import edu.watumull.presencify.feature.schedule.add_edit_timetable.AddEditTimetableRoot
import edu.watumull.presencify.feature.schedule.class_details.ClassDetailsRoot
import edu.watumull.presencify.feature.schedule.dashboard.ScheduleDashboardRoot
import edu.watumull.presencify.feature.schedule.room_details.RoomDetailsRoot
import edu.watumull.presencify.feature.schedule.search_class.SearchClassRoot
import edu.watumull.presencify.feature.schedule.search_room.SearchRoomRoot
import edu.watumull.presencify.feature.schedule.search_timetable.SearchTimetableRoot
import edu.watumull.presencify.feature.schedule.timetable_details.TimetableDetailsRoot

fun NavGraphBuilder.scheduleDashboard(
    onNavigateToSearchRoom: () -> Unit,
    onNavigateToSearchClass: () -> Unit,
    onNavigateToSearchTimetable: () -> Unit
) {
    composableWithSlideTransitions<ScheduleRoutes.ScheduleDashboard> {
        ScheduleDashboardRoot(
            onNavigateToSearchRoom = onNavigateToSearchRoom,
            onNavigateToSearchClass = onNavigateToSearchClass,
            onNavigateToSearchTimetable = onNavigateToSearchTimetable
        )
    }
}

fun NavGraphBuilder.scheduleNavGraph(
    onNavigateBack: () -> Unit,
    onNavigateToRoomDetails: (String) -> Unit,
    onNavigateToAddEditRoom: (String?) -> Unit,
    onNavigateToClassDetails: (String) -> Unit,
    onNavigateToAddEditClass: (timetableId: String, classId: String?) -> Unit,
    onNavigateToTimetableDetails: (String) -> Unit,
    onNavigateToAddEditTimetable: (String?) -> Unit,
    onNavigateToCreateAttendanceSheet: (String) -> Unit
) {
    // Room Navigation
    composableWithSlideTransitions<ScheduleRoutes.SearchRoom> {
        SearchRoomRoot(
            onNavigateBack = onNavigateBack,
            onNavigateToRoomDetails = onNavigateToRoomDetails,
            onNavigateToAddEditRoom = { onNavigateToAddEditRoom(null) }
        )
    }
    composableWithSlideTransitions<ScheduleRoutes.AddEditRoom> {
        AddEditRoomRoot(
            onNavigateBack = onNavigateBack
        )
    }
    composableWithSlideTransitions<ScheduleRoutes.RoomDetails> {
        RoomDetailsRoot(
            onNavigateBack = onNavigateBack,
            onNavigateToEditRoom = onNavigateToAddEditRoom
        )
    }

    // Class Navigation
    composableWithSlideTransitions<ScheduleRoutes.SearchClass> {
        SearchClassRoot(
            onNavigateBack = onNavigateBack,
            onNavigateToClassDetails = onNavigateToClassDetails,
            onNavigateToCreateAttendanceSheet = onNavigateToCreateAttendanceSheet
        )
    }
    composableWithSlideTransitions<ScheduleRoutes.AddEditClass> {
        AddEditClassRoot(
            onNavigateBack = onNavigateBack
        )
    }
    composableWithSlideTransitions<ScheduleRoutes.ClassDetails> {
        ClassDetailsRoot(
            onNavigateBack = onNavigateBack,
            onNavigateToEditClass = onNavigateToAddEditClass
        )
    }

    // Timetable Navigation
    composableWithSlideTransitions<ScheduleRoutes.SearchTimetable> {
        SearchTimetableRoot(
            onNavigateBack = onNavigateBack,
            onNavigateToTimetableDetails = onNavigateToTimetableDetails,
            onNavigateToAddEditTimetable = { onNavigateToAddEditTimetable(null) }
        )
    }
    composableWithSlideTransitions<ScheduleRoutes.AddEditTimetable> {
        AddEditTimetableRoot(
            onNavigateBack = onNavigateBack,
            onNavigateToTimetableDetails = onNavigateToTimetableDetails
        )
    }
    composableWithSlideTransitions<ScheduleRoutes.TimetableDetails> {
        TimetableDetailsRoot(
            onNavigateBack = onNavigateBack,
            onNavigateToEditTimetable = onNavigateToAddEditTimetable,
            onNavigateToAddClass = onNavigateToAddEditClass,
            onNavigateToClassDetails = onNavigateToClassDetails
        )
    }

    // Schedule Views (TODO)
    composableWithSlideTransitions<ScheduleRoutes.StudentSchedule> {
        // TODO: Add screen content
    }
    composableWithSlideTransitions<ScheduleRoutes.TeacherSchedule> {
        // TODO: Add screen content
    }
}

