package edu.watumull.presencify.navigation.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import edu.watumull.presencify.core.domain.model.auth.UserRole
import edu.watumull.presencify.core.presentation.composition_locals.LocalUserRole
import edu.watumull.presencify.core.presentation.navigation.NavRoute
import edu.watumull.presencify.feature.academics.navigation.academicsDashboard
import edu.watumull.presencify.feature.attendance.navigation.AttendanceRoutes
import edu.watumull.presencify.feature.attendance.navigation.attendanceDashboard
import edu.watumull.presencify.feature.schedule.navigation.scheduleDashboard
import edu.watumull.presencify.feature.users.navigation.SearchStudentIntention
import edu.watumull.presencify.feature.users.navigation.usersDashboard
import edu.watumull.presencify.navigation.navcontroller_extensions.navigateToAggregateAttendanceAnalytics
import edu.watumull.presencify.navigation.navcontroller_extensions.navigateToAssignUnassignStudentToBatch
import edu.watumull.presencify.navigation.navcontroller_extensions.navigateToAssignUnassignStudentToDivision
import edu.watumull.presencify.navigation.navcontroller_extensions.navigateToAssignUnassignStudentToSemester
import edu.watumull.presencify.navigation.navcontroller_extensions.navigateToDefaulters
import edu.watumull.presencify.navigation.navcontroller_extensions.navigateToImportStudents
import edu.watumull.presencify.navigation.navcontroller_extensions.navigateToImportTeachers
import edu.watumull.presencify.navigation.navcontroller_extensions.navigateToLinkUnlinkCourse
import edu.watumull.presencify.navigation.navcontroller_extensions.navigateToMarkUnmarkStudentAsDropout
import edu.watumull.presencify.navigation.navcontroller_extensions.navigateToModifyStudentBatch
import edu.watumull.presencify.navigation.navcontroller_extensions.navigateToModifyStudentDivision
import edu.watumull.presencify.navigation.navcontroller_extensions.navigateToClassDetails
import edu.watumull.presencify.navigation.navcontroller_extensions.navigateToClassDetailsWithSyntheticBackStack
import edu.watumull.presencify.navigation.navcontroller_extensions.navigateToScanQr
import edu.watumull.presencify.navigation.navcontroller_extensions.navigateToSearchAttendance
import edu.watumull.presencify.navigation.navcontroller_extensions.navigateToSearchBatch
import edu.watumull.presencify.navigation.navcontroller_extensions.navigateToSearchBranch
import edu.watumull.presencify.navigation.navcontroller_extensions.navigateToSearchClass
import edu.watumull.presencify.navigation.navcontroller_extensions.navigateToSearchCourse
import edu.watumull.presencify.navigation.navcontroller_extensions.navigateToSearchDivision
import edu.watumull.presencify.navigation.navcontroller_extensions.navigateToSearchRoom
import edu.watumull.presencify.navigation.navcontroller_extensions.navigateToSearchScheme
import edu.watumull.presencify.navigation.navcontroller_extensions.navigateToSearchSemester
import edu.watumull.presencify.navigation.navcontroller_extensions.navigateToSearchStudent
import edu.watumull.presencify.navigation.navcontroller_extensions.navigateToSearchTeacher
import edu.watumull.presencify.navigation.navcontroller_extensions.navigateToSearchTimetable
import edu.watumull.presencify.navigation.navcontroller_extensions.navigateToUniversityDetails
import edu.watumull.presencify.navigation.notification.ScheduleNotificationDeepLink
import kotlinx.serialization.Serializable

@Serializable
data object Home : NavRoute

@Composable
fun HomeNavHost(
    homeNavController: NavHostController,
    rootNavController: NavHostController,
    modifier: Modifier = Modifier,
    scheduleNotificationDeepLink: ScheduleNotificationDeepLink? = null,
    onDeepLinkConsumed: () -> Unit = {}
) {
    val attendanceTabRoute: NavRoute = if (LocalUserRole.current == UserRole.STUDENT) {
        AttendanceRoutes.StudentAttendanceDashboard
    } else {
        AttendanceRoutes.AttendanceDashboard
    }
    NavHost(
        navController = homeNavController,
        startDestination = attendanceTabRoute,
        modifier = modifier
    ) {
        academicsDashboard(
            onNavigateToSearchBranch = rootNavController::navigateToSearchBranch,
            onNavigateToSearchScheme = rootNavController::navigateToSearchScheme,
            onNavigateToSearchCourse = rootNavController::navigateToSearchCourse,
            onNavigateToLinkUnlinkCourse = rootNavController::navigateToLinkUnlinkCourse,
            onNavigateToUniversityDetails = rootNavController::navigateToUniversityDetails,
            onNavigateToSearchSemester = rootNavController::navigateToSearchSemester,
            onNavigateToSearchDivision = rootNavController::navigateToSearchDivision,
            onNavigateToSearchBatch = rootNavController::navigateToSearchBatch
        )

        attendanceDashboard(
            onNavigateBack = homeNavController::navigateUp,
            onNavigateToStudentAttendanceAnalytics = {
                rootNavController.navigateToSearchStudent(
                    intention = SearchStudentIntention.VIEW_ATTENDANCE.name
                )
            },
            onNavigateToAggregateAttendanceAnalytics = {
                rootNavController.navigateToAggregateAttendanceAnalytics(
                    semesterId = null,
                    divisionId = null,
                    batchId = null,
                    startDate = null,
                    endDate = null,
                    courseId = null,
                    semesterNumber = null,
                    academicStartYear = null,
                    academicEndYear = null,
                    branchId = null,
                    schemeId = null
                )
            },
            onNavigateToSearchAttendance = {
                rootNavController.navigateToSearchAttendance(
                    courseId = null,
                    studentId = null,
                    startDate = null,
                    endDate = null,
                    semesterId = null,
                    batchId = null,
                    divisionId = null
                )
            },
            onNavigateToCreateAttendance = {
                rootNavController.navigateToSearchClass(intention = "CREATE_ATTENDANCE_SHEET")
            },
            onNavigateToSearchAttendanceForCourseAndStudent = { courseId, studentId ->
                rootNavController.navigateToSearchAttendance(
                    courseId = courseId,
                    studentId = studentId
                )
            },
            onNavigateToDefaulters = rootNavController::navigateToDefaulters
        )

        usersDashboard(
            onNavigateToSearchStudents = rootNavController::navigateToSearchStudent,
            onNavigateToSearchTeachers = rootNavController::navigateToSearchTeacher,
            onNavigateToAssignUnassignSemester = rootNavController::navigateToAssignUnassignStudentToSemester,
            onNavigateToAssignUnassignDivision = rootNavController::navigateToAssignUnassignStudentToDivision,
            onNavigateToAssignUnassignBatch = rootNavController::navigateToAssignUnassignStudentToBatch,
            onNavigateToModifyDivision = rootNavController::navigateToModifyStudentDivision,
            onNavigateToModifyBatch = rootNavController::navigateToModifyStudentBatch,
            onNavigateToMarkUnmarkStudentAsDropout = rootNavController::navigateToMarkUnmarkStudentAsDropout,
            onNavigateToImportStudents = rootNavController::navigateToImportStudents,
            onNavigateToImportTeachers = rootNavController::navigateToImportTeachers
        )

        scheduleDashboard(
            onNavigateToSearchClass = rootNavController::navigateToSearchClass,
            onNavigateToSearchTimetable = rootNavController::navigateToSearchTimetable,
            onNavigateToSearchRoom = rootNavController::navigateToSearchRoom,
            onNavigateToClassDetails = rootNavController::navigateToClassDetails
        )
    }
    LaunchedEffect(scheduleNotificationDeepLink) {
        val deepLink = scheduleNotificationDeepLink ?: return@LaunchedEffect
        if (deepLink.type == "ExtraLectureAdded") {
            homeNavController.navigateToClassDetailsWithSyntheticBackStack(deepLink.classId, rootNavController)
        }
        // Mark as consumed to prevent duplicate navigation on recomposition (e.g., screen rotation)
        onDeepLinkConsumed()
    }
}

fun NavHostController.navigateToHome() {
    navigate(Home){
        popUpTo(graph.startDestinationId){
            inclusive = true
        }
    }
}
