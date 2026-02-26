package edu.watumull.presencify.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import edu.watumull.presencify.core.presentation.navigation.NavRoute
import edu.watumull.presencify.feature.academics.navigation.SearchCourseIntention
import edu.watumull.presencify.feature.academics.navigation.academicsNavGraph
import edu.watumull.presencify.feature.admin.auth.navigation.adminAuthNavGraph
import edu.watumull.presencify.feature.admin.mgt.navigation.adminMgtNavGraph
import edu.watumull.presencify.feature.attendance.navigation.attendanceNavGraph
import edu.watumull.presencify.feature.onboarding.navigation.onboardingNavGraph
import edu.watumull.presencify.feature.schedule.navigation.scheduleNavGraph
import edu.watumull.presencify.feature.student.auth.navigation.studentAuthNavGraph
import edu.watumull.presencify.feature.teacher.auth.navigation.teacherAuthNavGraph
import edu.watumull.presencify.feature.users.navigation.SearchStudentIntention
import edu.watumull.presencify.feature.users.navigation.usersNavGraph
import edu.watumull.presencify.navigation.home.Home
import edu.watumull.presencify.navigation.home.navigateToHome
import edu.watumull.presencify.navigation.navcontroller_extensions.navigateToAddAdmin
import edu.watumull.presencify.navigation.navcontroller_extensions.navigateToAddEditBatch
import edu.watumull.presencify.navigation.navcontroller_extensions.navigateToAddEditBranch
import edu.watumull.presencify.navigation.navcontroller_extensions.navigateToAddEditClass
import edu.watumull.presencify.navigation.navcontroller_extensions.navigateToAddEditCourse
import edu.watumull.presencify.navigation.navcontroller_extensions.navigateToAddEditDivision
import edu.watumull.presencify.navigation.navcontroller_extensions.navigateToAddEditRoom
import edu.watumull.presencify.navigation.navcontroller_extensions.navigateToAddEditScheme
import edu.watumull.presencify.navigation.navcontroller_extensions.navigateToAddEditSemester
import edu.watumull.presencify.navigation.navcontroller_extensions.navigateToAddEditStudent
import edu.watumull.presencify.navigation.navcontroller_extensions.navigateToAddEditTeacher
import edu.watumull.presencify.navigation.navcontroller_extensions.navigateToAddEditTimetable
import edu.watumull.presencify.navigation.navcontroller_extensions.navigateToAddEditUniversity
import edu.watumull.presencify.navigation.navcontroller_extensions.navigateToAdminDetails
import edu.watumull.presencify.navigation.navcontroller_extensions.navigateToAdminForgotPassword
import edu.watumull.presencify.navigation.navcontroller_extensions.navigateToAdminLogin
import edu.watumull.presencify.navigation.navcontroller_extensions.navigateToAdminVerifyCode
import edu.watumull.presencify.navigation.navcontroller_extensions.navigateToBatchDetails
import edu.watumull.presencify.navigation.navcontroller_extensions.navigateToBranchDetails
import edu.watumull.presencify.navigation.navcontroller_extensions.navigateToClassDetails
import edu.watumull.presencify.navigation.navcontroller_extensions.navigateToCourseDetails
import edu.watumull.presencify.navigation.navcontroller_extensions.navigateToDivisionDetails
import edu.watumull.presencify.navigation.navcontroller_extensions.navigateToRoomDetails
import edu.watumull.presencify.navigation.navcontroller_extensions.navigateToSchemeDetails
import edu.watumull.presencify.navigation.navcontroller_extensions.navigateToSearchCourse
import edu.watumull.presencify.navigation.navcontroller_extensions.navigateToSearchStudent
import edu.watumull.presencify.navigation.navcontroller_extensions.navigateToSemesterDetails
import edu.watumull.presencify.navigation.navcontroller_extensions.navigateToStudentDetails
import edu.watumull.presencify.navigation.navcontroller_extensions.navigateToStudentLogin
import edu.watumull.presencify.navigation.navcontroller_extensions.navigateToTeacherDetails
import edu.watumull.presencify.navigation.navcontroller_extensions.navigateToTeacherLogin
import edu.watumull.presencify.navigation.navcontroller_extensions.navigateToTimetableDetails
import edu.watumull.presencify.navigation.navcontroller_extensions.navigateToUpdateAdminPassword

@Composable
fun AppNavHost(
    startDestination: NavRoute,
) {
    val rootNavController = rememberNavController()

    NavHost(
        navController = rootNavController,
        startDestination = startDestination
    ) {
        composable<Home> {
            Home(rootNavController = rootNavController)
        }

        academicsNavGraph(
            onNavigateBack = { rootNavController.navigateUp() },
            onNavigateToBranchDetails = rootNavController::navigateToBranchDetails,
            onNavigateToAddEditBranch = rootNavController::navigateToAddEditBranch,
            onNavigateToSchemeDetails = rootNavController::navigateToSchemeDetails,
            onNavigateToAddEditScheme = rootNavController::navigateToAddEditScheme,
            onNavigateToAddEditUniversity = rootNavController::navigateToAddEditUniversity,
            onNavigateToCourseDetails = rootNavController::navigateToCourseDetails,
            onNavigateToAddEditCourse = rootNavController::navigateToAddEditCourse,
            onNavigateToSearchCourse = { branchId, semesterNumber ->
                rootNavController.navigateToSearchCourse(
                    intention = SearchCourseIntention.LINK_UNLINK_COURSE_TO_SEMESTER_NUMBER_BRANCH.name,
                    branchId = branchId,
                    semesterNumber = semesterNumber
                )
            },
            onNavigateToSemesterDetails = rootNavController::navigateToSemesterDetails,
            onNavigateToAddEditSemester = rootNavController::navigateToAddEditSemester,
            onNavigateToDivisionDetails = rootNavController::navigateToDivisionDetails,
            onNavigateToAddEditDivision = rootNavController::navigateToAddEditDivision,
            onNavigateToBatchDetails = rootNavController::navigateToBatchDetails,
            onNavigateToAddEditBatch = rootNavController::navigateToAddEditBatch,
        )

        onboardingNavGraph(
            navigateToStudentLogin = {
                rootNavController.navigateToStudentLogin()
            },
            navigateToTeacherLogin = {
                rootNavController.navigateToTeacherLogin()
            },
            navigateToAdminLogin = {
                rootNavController.navigateToAdminLogin()
            }
        )

        attendanceNavGraph()

        usersNavGraph(
            onNavigateBack = { rootNavController.navigateUp() },
            onNavigateToStudentDetails = rootNavController::navigateToStudentDetails,
            onNavigateToAddEditStudent = rootNavController::navigateToAddEditStudent,
            onNavigateToSearchStudentForAssignUnassignSemester = { semesterId, branchId ->
                rootNavController.navigateToSearchStudent(
                    intention = SearchStudentIntention.ASSIGN_UNASSIGN_STUDENT_TO_SEMESTER.name,
                    semesterId = semesterId,
                    branchId = branchId
                )
            },
            onNavigateToSearchStudentForAssignUnassignDivision = { divisionId, branchId, academicStartYear, academicEndYear, semesterNumber ->
                rootNavController.navigateToSearchStudent(
                    intention = SearchStudentIntention.ASSIGN_UNASSIGN_STUDENT_TO_DIVISION.name,
                    divisionId = divisionId,
                    branchId = branchId,
                    academicStartYear = academicStartYear,
                    academicEndYear = academicEndYear,
                    semesterNumber = semesterNumber
                )
            },
            onNavigateToSearchStudentForAssignUnassignBatch = { batchId, branchId, academicStartYear, academicEndYear, semesterNumber ->
                rootNavController.navigateToSearchStudent(
                    intention = SearchStudentIntention.ASSIGN_UNASSIGN_STUDENT_TO_BATCH.name,
                    batchId = batchId,
                    branchId = branchId,
                    academicStartYear = academicStartYear,
                    academicEndYear = academicEndYear,
                    semesterNumber = semesterNumber
                )
            },
            onNavigateToTeacherDetails = rootNavController::navigateToTeacherDetails,
            onNavigateToAddEditTeacher = rootNavController::navigateToAddEditTeacher,
            onNavigateToAssignUnassignCourses = { teacherId ->
                rootNavController.navigateToSearchCourse(
                    intention = SearchCourseIntention.ASSIGN_UNASSIGN_COURSE_TO_TEACHER.name,
                    teacherId = teacherId
                )
            },
            onNavigateToSearchStudentForModifyDivision = { divisionId, branchId, academicStartYear, academicEndYear, semesterNumber, newStartDate ->
                rootNavController.navigateToSearchStudent(
                    intention = SearchStudentIntention.MODIFY_STUDENT_DIVISION.name,
                    divisionId = divisionId,
                    branchId = branchId,
                    academicStartYear = academicStartYear,
                    academicEndYear = academicEndYear,
                    semesterNumber = semesterNumber,
                    newStartDate = newStartDate
                )
            },
            onNavigateToSearchStudentForModifyBatch = { batchId, branchId, academicStartYear, academicEndYear, semesterNumber, newStartDate ->
                rootNavController.navigateToSearchStudent(
                    intention = SearchStudentIntention.MODIFY_STUDENT_BATCH.name,
                    batchId = batchId,
                    branchId = branchId,
                    academicStartYear = academicStartYear,
                    academicEndYear = academicEndYear,
                    semesterNumber = semesterNumber,
                    newStartDate = newStartDate
                )
            },
            onNavigateToSearchStudentForMarkUnmarkDropout = { dropoutAcademicStartYear, dropoutAcademicEndYear ->
                rootNavController.navigateToSearchStudent(
                    intention = SearchStudentIntention.MARK_UNMARK_STUDENT_AS_DROPOUT.name,
                    dropoutAcademicStartYear = dropoutAcademicStartYear,
                    dropoutAcademicEndYear = dropoutAcademicEndYear
                )
            }
        )

        scheduleNavGraph(
            onNavigateBack = { rootNavController.navigateUp() },
            onNavigateToRoomDetails = rootNavController::navigateToRoomDetails,
            onNavigateToAddEditRoom = rootNavController::navigateToAddEditRoom,
            onNavigateToClassDetails = rootNavController::navigateToClassDetails,
            onNavigateToAddEditClass = rootNavController::navigateToAddEditClass,
            onNavigateToTimetableDetails = rootNavController::navigateToTimetableDetails,
            onNavigateToAddEditTimetable = rootNavController::navigateToAddEditTimetable
        )

        adminAuthNavGraph(
            onNavigateToHome = rootNavController::navigateToHome,
            onNavigateToForgotPassword = rootNavController::navigateToAdminForgotPassword,
            onNavigateBack = { rootNavController.navigateUp() },
            onNavigateToVerifyCode = rootNavController::navigateToAdminVerifyCode,
        )

        adminMgtNavGraph(
            onNavigateToUpdatePassword = rootNavController::navigateToUpdateAdminPassword,
            onNavigateBack = { rootNavController.navigateUp() },
            onNavigateToVerifyCode = rootNavController::navigateToAdminVerifyCode,
            onNavigateToAddAdmin = rootNavController::navigateToAddAdmin,
            onNavigateToAdminDetails = rootNavController::navigateToAdminDetails,
        )

        studentAuthNavGraph()

        teacherAuthNavGraph()

    }
}
