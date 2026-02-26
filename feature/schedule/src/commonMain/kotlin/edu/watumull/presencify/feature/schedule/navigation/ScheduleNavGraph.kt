package edu.watumull.presencify.feature.schedule.navigation

import androidx.navigation.NavGraphBuilder
import edu.watumull.presencify.core.design.systems.components.composableWithSlideTransitions
import edu.watumull.presencify.feature.schedule.add_edit_room.AddEditRoomRoot
import edu.watumull.presencify.feature.schedule.dashboard.ScheduleDashboardRoot
import edu.watumull.presencify.feature.schedule.room_details.RoomDetailsRoot
import edu.watumull.presencify.feature.schedule.search_class.SearchClassRoot
import edu.watumull.presencify.feature.schedule.search_room.SearchRoomRoot

fun NavGraphBuilder.scheduleDashboard(
    onNavigateToSearchRoom: () -> Unit,
    onNavigateToSearchClass: () -> Unit
) {
    composableWithSlideTransitions<ScheduleRoutes.ScheduleDashboard> {
        ScheduleDashboardRoot(
            onNavigateToSearchRoom = onNavigateToSearchRoom,
            onNavigateToSearchClass = onNavigateToSearchClass
        )
    }
}

fun NavGraphBuilder.scheduleNavGraph(
    onNavigateBack: () -> Unit,
    onNavigateToRoomDetails: (String) -> Unit,
    onNavigateToAddEditRoom: (String?) -> Unit,
    onNavigateToClassDetails: (String) -> Unit,
    onNavigateToAddEditClass: (String?) -> Unit
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
            onNavigateToAddEditClass = { onNavigateToAddEditClass(null) }
        )
    }
    composableWithSlideTransitions<ScheduleRoutes.AddEditClass> {
        // TODO: Add screen content
    }
    composableWithSlideTransitions<ScheduleRoutes.ClassDetails> {
        // TODO: Add screen content
    }

    // Timetable Navigation (TODO)
    composableWithSlideTransitions<ScheduleRoutes.AddEditTimetable> {
        // TODO: Add screen content
    }
    composableWithSlideTransitions<ScheduleRoutes.SearchTimetable> {
        // TODO: Add screen content
    }
    composableWithSlideTransitions<ScheduleRoutes.TimetableDetails> {
        // TODO: Add screen content
    }

    // Schedule Views (TODO)
    composableWithSlideTransitions<ScheduleRoutes.StudentSchedule> {
        // TODO: Add screen content
    }
    composableWithSlideTransitions<ScheduleRoutes.TeacherSchedule> {
        // TODO: Add screen content
    }
}

