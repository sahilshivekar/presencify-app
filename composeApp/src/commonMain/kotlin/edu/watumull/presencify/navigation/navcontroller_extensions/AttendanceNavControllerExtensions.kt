package edu.watumull.presencify.navigation.navcontroller_extensions

import androidx.navigation.NavController
import edu.watumull.presencify.feature.attendance.navigation.AttendanceRoutes
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import edu.watumull.presencify.feature.schedule.navigation.ScheduleRoutes


/**
 * Navigate to Attendance Dashboard screen
 */
fun NavController.navigateToAttendanceDashboard() {
    navigate(AttendanceRoutes.AttendanceDashboard)
}

/**
 * Navigate to Create/Update Attendance Sheet
 */
fun NavController.navigateToCreateAttendanceSheet(classId: String) {
    navigate(AttendanceRoutes.CreateAttendanceSheet(classId = classId))
}

/**
 * Navigate to Mark Student Attendance screen.
 * Automatically pops the CreateAttendanceSheet screen if it is the current destination.
 */
fun NavController.navigateToMarkStudentAttendance(attendanceId: String) {
    val currentDestination = currentBackStackEntry?.destination
    val previousDestination = previousBackStackEntry?.destination

    val isCurrentlyOnCreateSheet = currentDestination?.hasRoute<AttendanceRoutes.CreateAttendanceSheet>() == true
    val isPreviousSearchClass = previousDestination?.hasRoute<ScheduleRoutes.SearchClass>() == true

    navigate(AttendanceRoutes.MarkStudentAttendance(attendanceId = attendanceId)) {
        if (isCurrentlyOnCreateSheet) {
            if (isPreviousSearchClass) {
                // If the flow was SearchClass -> CreateAttendanceSheet,
                // pop SearchClass inclusively to clear the entire sequence.
                popUpTo<ScheduleRoutes.SearchClass> {
                    inclusive = true
                }
            } else {
                // Otherwise, just pop the CreateAttendanceSheet screen.
                popUpTo<AttendanceRoutes.CreateAttendanceSheet> {
                    inclusive = true
                }
            }
        }
    }
}


/**
 * Pops the entire backstack down to the root destination, clearing its state,
 * and launches a fresh instance of the root screen to force a full page refresh.
 */
fun NavController.refreshRootDestination() {
    val rootDestination = graph.findStartDestination()

    // Navigate back to the start destination
    navigate(rootDestination.id) {
        // Pop up to the start destination itself to clear its saved instance/state
        popUpTo(rootDestination.id) {
            inclusive = true
        }
        // Avoid multi-instances of the root if clicked repeatedly
        launchSingleTop = true
    }
}

/**
 * Handles going back from the Mark Student Attendance screen.
 * - If the user came from Attendance Details, it clears the history and re-routes
 *   to a fresh, completely refreshed details screen.
 * - Otherwise, it falls back to a standard backward pop.
 */
fun NavController.navigateBackFromMarkAttendanceScreen(attendanceId: String) {
    // If the stale details screen was popped inclusively during the forward navigation,
    // the previous backstack entry will now be the screen *before* details (e.g., Dashboard).
    val isLaunchedFromDetailsScreen = previousBackStackEntry?.destination
        ?.hasRoute<AttendanceRoutes.AttendanceDetails>() == true

    if (isLaunchedFromDetailsScreen) {
        // Clear out the Mark screen and load a brand new, refreshed Details instance
        navigate(AttendanceRoutes.AttendanceDetails(attendanceId = attendanceId)) {
            popUpTo<AttendanceRoutes.AttendanceDetails> {
                inclusive = true
            }
        }
    } else {
        // Fall back normally (handles the post-creation sheet flows)
        navigateUp()
    }
}

/**
 * Navigate to Individual Student Analytics with filters
 */
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

/**
 * Navigate to Aggregate (Batch/Group) Analytics with filters
 */
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

/**
 * Navigate to Search Attendance screen
 */
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

/**
 * Navigate to specific Attendance Details
 */
fun NavController.navigateToAttendanceDetails(attendanceId: String) {
    navigate(AttendanceRoutes.AttendanceDetails(attendanceId = attendanceId))
}

fun NavController.navigateToDynamicQR(attendanceId: String) {
    navigate(AttendanceRoutes.DynamicQR(attendanceId = attendanceId))
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

/**
 * Navigate to Defaulters screen
 */
fun NavController.navigateToDefaulters() {
    navigate(AttendanceRoutes.Defaulters)
}
