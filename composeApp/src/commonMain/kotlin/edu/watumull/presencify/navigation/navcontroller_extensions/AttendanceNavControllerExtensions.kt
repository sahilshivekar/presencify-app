package edu.watumull.presencify.navigation.navcontroller_extensions

import androidx.navigation.NavController
import edu.watumull.presencify.feature.attendance.navigation.AttendanceRoutes
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import edu.watumull.presencify.feature.schedule.navigation.ScheduleRoutes



fun NavController.navigateToAttendanceDashboard() {
    navigate(AttendanceRoutes.AttendanceDashboard)
}


fun NavController.navigateToCreateAttendanceSheet(classId: String) {
    navigate(AttendanceRoutes.CreateAttendanceSheet(classId = classId))
}


fun NavController.navigateToMarkStudentAttendance(attendanceId: String) {
    val currentDestination = currentBackStackEntry?.destination
    val previousDestination = previousBackStackEntry?.destination

    val isCurrentlyOnCreateSheet = currentDestination?.hasRoute<AttendanceRoutes.CreateAttendanceSheet>() == true
    val isPreviousSearchClass = previousDestination?.hasRoute<ScheduleRoutes.SearchClass>() == true

    navigate(AttendanceRoutes.MarkStudentAttendance(attendanceId = attendanceId)) {
        if (isCurrentlyOnCreateSheet) {
            if (isPreviousSearchClass) {
                popUpTo<ScheduleRoutes.SearchClass> {
                    inclusive = true
                }
            } else {
                popUpTo<AttendanceRoutes.CreateAttendanceSheet> {
                    inclusive = true
                }
            }
        }
    }
}



fun NavController.refreshRootDestination() {
    val rootDestination = graph.findStartDestination()

    navigate(rootDestination.id) {
        popUpTo(rootDestination.id) {
            inclusive = true
        }
        launchSingleTop = true
    }
}


fun NavController.navigateBackFromMarkAttendanceScreen(attendanceId: String) {
    val isLaunchedFromDetailsScreen = previousBackStackEntry?.destination
        ?.hasRoute<AttendanceRoutes.AttendanceDetails>() == true

    if (isLaunchedFromDetailsScreen) {
        navigate(AttendanceRoutes.AttendanceDetails(attendanceId = attendanceId)) {
            popUpTo<AttendanceRoutes.AttendanceDetails> {
                inclusive = true
            }
        }
    } else {
        navigateUp()
    }
}


fun NavController.navigateToStudentAttendanceAnalytics(
    studentId: String,
    semesterId: String? = null,
    divisionId: String? = null,
    batchId: String? = null,
    startDate: String? = null,
    endDate: String? = null,
    courseId: String? = null,
    semesterNumber: Int? = null,
    academicStartYear: String? = null,
    academicEndYear: String? = null,
    branchId: String? = null,
    schemeId: String? = null,
) {
    navigate(
        AttendanceRoutes.StudentAttendanceAnalytics(
            studentId = studentId,
            semesterId = semesterId,
            divisionId = divisionId,
            batchId = batchId,
            startDate = startDate,
            endDate = endDate,
            courseId = courseId,
            semesterNumber = semesterNumber,
            academicStartYear = academicStartYear,
            academicEndYear = academicEndYear,
            branchId = branchId,
            schemeId = schemeId
        )
    )
}


fun NavController.navigateToAggregateAttendanceAnalytics(
    semesterId: String? = null,
    divisionId: String? = null,
    batchId: String? = null,
    startDate: String? = null,
    endDate: String? = null,
    courseId: String? = null,
    semesterNumber: Int? = null,
    academicStartYear: String? = null,
    academicEndYear: String? = null,
    branchId: String? = null,
    schemeId: String? = null,
) {
    navigate(
        AttendanceRoutes.AggregateAttendanceAnalytics(
            semesterId = semesterId,
            divisionId = divisionId,
            batchId = batchId,
            startDate = startDate,
            endDate = endDate,
            courseId = courseId,
            semesterNumber = semesterNumber,
            academicStartYear = academicStartYear,
            academicEndYear = academicEndYear,
            branchId = branchId,
            schemeId = schemeId
        )
    )
}


fun NavController.navigateToSearchAttendance(
    courseId: String? = null,
    studentId: String? = null,
    startDate: String? = null,
    endDate: String? = null,
    semesterId: String? = null,
    batchId: String? = null,
    divisionId: String? = null,
) {
    navigate(
        AttendanceRoutes.SearchAttendance(
            courseId = courseId,
            studentId = studentId,
            startDate = startDate,
            endDate = endDate,
            semesterId = semesterId,
            batchId = batchId,
            divisionId = divisionId
        )
    )
}


fun NavController.navigateToAttendanceDetails(attendanceId: String) {
    navigate(AttendanceRoutes.AttendanceDetails(attendanceId = attendanceId))
}

fun NavController.navigateToScanQr() {
    navigate(AttendanceRoutes.ScanQr)
}

fun NavController.navigateToRecognizeStudent(attendanceId: String) {
    navigate(AttendanceRoutes.RecognizeStudent(attendanceId = attendanceId)) {
        popUpTo(AttendanceRoutes.ScanQr) {
            inclusive = true
        }
    }
}


fun NavController.navigateToDefaulters() {
    navigate(AttendanceRoutes.Defaulters)
}
