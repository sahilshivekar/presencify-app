package edu.watumull.presencify.navigation.navcontroller_extensions

import androidx.navigation.NavController
import edu.watumull.presencify.feature.schedule.navigation.ScheduleRoutes


fun NavController.navigateToScheduleDashboard() {
    navigate(ScheduleRoutes.ScheduleDashboard)
}


fun NavController.navigateToClassDetailsWithSyntheticBackStack(classId: String, rootNavController: NavController) {
    navigate(ScheduleRoutes.ScheduleDashboard) {
        popUpTo(graph.startDestinationId) {
            inclusive = false
        }
        launchSingleTop = true
    }
    rootNavController.navigate(ScheduleRoutes.ClassDetails(classId = classId))
}


fun NavController.navigateToAddEditClass(timetableId: String, classId: String? = null) {
    navigate(ScheduleRoutes.AddEditClass(timetableId = timetableId, classId = classId))
}


fun NavController.navigateToSearchClass(intention: String = "DEFAULT") {
    navigate(ScheduleRoutes.SearchClass(intention = intention))
}


fun NavController.navigateToClassDetails(classId: String) {
    navigate(ScheduleRoutes.ClassDetails(classId = classId))
}


fun NavController.navigateToAddEditRoom(roomId: String? = null) {
    navigate(ScheduleRoutes.AddEditRoom(roomId = roomId))
}


fun NavController.navigateToSearchRoom() {
    navigate(ScheduleRoutes.SearchRoom)
}


fun NavController.navigateToRoomDetails(roomId: String) {
    navigate(ScheduleRoutes.RoomDetails(roomId = roomId))
}


fun NavController.navigateToAddEditTimetable(timetableId: String? = null) {
    navigate(ScheduleRoutes.AddEditTimetable(timetableId = timetableId))
}


fun NavController.navigateToSearchTimetable() {
    navigate(ScheduleRoutes.SearchTimetable)
}


fun NavController.navigateToTimetableDetails(timetableId: String) {
    navigate(ScheduleRoutes.TimetableDetails(timetableId = timetableId))
}


fun NavController.navigateToStudentSchedule(studentId: String? = null) {
    navigate(ScheduleRoutes.StudentSchedule(studentId = studentId))
}


fun NavController.navigateToTeacherSchedule(teacherId: String? = null) {
    navigate(ScheduleRoutes.TeacherSchedule(teacherId = teacherId))
}

