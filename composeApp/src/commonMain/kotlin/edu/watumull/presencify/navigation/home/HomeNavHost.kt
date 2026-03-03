package edu.watumull.presencify.navigation.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import edu.watumull.presencify.core.presentation.navigation.NavRoute
import edu.watumull.presencify.feature.academics.navigation.academicsDashboard
import edu.watumull.presencify.feature.attendance.navigation.AttendanceRoutes
import edu.watumull.presencify.feature.attendance.navigation.attendanceDashboard
import edu.watumull.presencify.feature.schedule.navigation.scheduleDashboard
import edu.watumull.presencify.feature.users.navigation.usersDashboard
import edu.watumull.presencify.feature.users.navigation.SearchStudentIntention
import edu.watumull.presencify.navigation.navcontroller_extensions.navigateToAggregateAttendanceAnalytics
import edu.watumull.presencify.navigation.navcontroller_extensions.navigateToAssignUnassignStudentToBatch
import edu.watumull.presencify.navigation.navcontroller_extensions.navigateToAssignUnassignStudentToDivision
import edu.watumull.presencify.navigation.navcontroller_extensions.navigateToAssignUnassignStudentToSemester
import edu.watumull.presencify.navigation.navcontroller_extensions.navigateToLinkUnlinkCourse
import edu.watumull.presencify.navigation.navcontroller_extensions.navigateToMarkUnmarkStudentAsDropout
import edu.watumull.presencify.navigation.navcontroller_extensions.navigateToModifyStudentBatch
import edu.watumull.presencify.navigation.navcontroller_extensions.navigateToModifyStudentDivision
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
import kotlinx.serialization.Serializable

@Serializable
data object Home : NavRoute

@Composable
fun HomeNavHost(
    homeNavController: NavHostController,
    rootNavController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = homeNavController,
        startDestination = AttendanceRoutes.AttendanceDashboard,
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
            }
        )

        usersDashboard(
            onNavigateToSearchStudents = rootNavController::navigateToSearchStudent,
            onNavigateToSearchTeachers = rootNavController::navigateToSearchTeacher,
            onNavigateToAssignUnassignSemester = rootNavController::navigateToAssignUnassignStudentToSemester,
            onNavigateToAssignUnassignDivision = rootNavController::navigateToAssignUnassignStudentToDivision,
            onNavigateToAssignUnassignBatch = rootNavController::navigateToAssignUnassignStudentToBatch,
            onNavigateToModifyDivision = rootNavController::navigateToModifyStudentDivision,
            onNavigateToModifyBatch = rootNavController::navigateToModifyStudentBatch,
            onNavigateToMarkUnmarkStudentAsDropout = rootNavController::navigateToMarkUnmarkStudentAsDropout
        )

        scheduleDashboard(
            onNavigateToSearchRoom = rootNavController::navigateToSearchRoom,
            onNavigateToSearchClass = rootNavController::navigateToSearchClass,
            onNavigateToSearchTimetable = rootNavController::navigateToSearchTimetable
        )
    }
}

fun NavHostController.navigateToHome() {
    navigate(Home){
        popUpTo(graph.startDestinationId){
            inclusive = true
        }
    }
}

