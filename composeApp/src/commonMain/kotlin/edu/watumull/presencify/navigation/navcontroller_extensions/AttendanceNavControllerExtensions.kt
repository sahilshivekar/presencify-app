package edu.watumull.presencify.navigation.navcontroller_extensions

import androidx.navigation.NavController
import edu.watumull.presencify.feature.attendance.navigation.AttendanceRoutes

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
 * Navigate to Mark Student Attendance screen
 */
fun NavController.navigateToMarkStudentAttendance(attendanceId: String) {
    navigate(AttendanceRoutes.MarkStudentAttendance(attendanceId = attendanceId))
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
    semesterNumber: String? = null,
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
    semesterNumber: String? = null,
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