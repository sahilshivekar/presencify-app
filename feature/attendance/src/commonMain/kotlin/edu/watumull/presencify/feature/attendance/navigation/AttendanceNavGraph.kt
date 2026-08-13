package edu.watumull.presencify.feature.attendance.navigation

import androidx.navigation.NavGraphBuilder
import edu.watumull.presencify.core.designsystem.components.composableWithSlideTransitions
import edu.watumull.presencify.feature.attendance.aggregate_analytics.AggregateAttendanceAnalyticsRoot
import edu.watumull.presencify.feature.attendance.attendance_dashboard.AttendanceDashboardRoot
import edu.watumull.presencify.feature.attendance.attendance_details.AttendanceDetailsRoot
import edu.watumull.presencify.feature.attendance.create_attendance.CreateAttendanceRoot
import edu.watumull.presencify.feature.attendance.defaulters.DefaultersRoot
import edu.watumull.presencify.feature.attendance.mark_attendance.MarkAttendanceRoot
import edu.watumull.presencify.feature.attendance.recognize_student.RecognizeStudentRoot
import edu.watumull.presencify.feature.attendance.scan_qr.ScanQrRoot
import edu.watumull.presencify.feature.attendance.search_attendance.SearchAttendanceRoot
import edu.watumull.presencify.feature.attendance.student_analytics.StudentAttendanceAnalyticsRoot
import edu.watumull.presencify.feature.attendance.student_attendance_dashboard.StudentAttendanceDashboardRoot

fun NavGraphBuilder.attendanceDashboard(
    onNavigateBack: () -> Unit,
    onNavigateToStudentAttendanceAnalytics: () -> Unit,
    onNavigateToAggregateAttendanceAnalytics: () -> Unit,
    onNavigateToSearchAttendance: () -> Unit,
    onNavigateToCreateAttendance: () -> Unit,
    onNavigateToSearchAttendanceForCourseAndStudent: (courseId: String, studentId: String) -> Unit,
    onNavigateToDefaulters: () -> Unit,
) {
    composableWithSlideTransitions<AttendanceRoutes.AttendanceDashboard> {
        AttendanceDashboardRoot(
            onNavigateBack = onNavigateBack,
            onNavigateToStudentAttendanceAnalytics = onNavigateToStudentAttendanceAnalytics,
            onNavigateToAggregateAttendanceAnalytics = onNavigateToAggregateAttendanceAnalytics,
            onNavigateToSearchAttendance = onNavigateToSearchAttendance,
            onNavigateToCreateAttendance = onNavigateToCreateAttendance,
            onNavigateToDefaulters = onNavigateToDefaulters,
        )
    }

    composableWithSlideTransitions<AttendanceRoutes.StudentAttendanceDashboard> {
        StudentAttendanceDashboardRoot(
            onNavigateBack = onNavigateBack,
            onNavigateToSearchAttendanceForCourse = onNavigateToSearchAttendanceForCourseAndStudent,
        )
    }
}

fun NavGraphBuilder.attendanceNavGraph(
    onNavigateBack: () -> Unit,
    onNavigateToMarkAttendance: (String) -> Unit,
    onNavigateToAttendanceDetails: (String) -> Unit,
    onNavigateToSearchAttendanceForCourse: (String, String?) -> Unit,
    onNavigateToSearchAttendanceForCourseAndStudent: (courseId: String, studentId: String) -> Unit,
    onNavigateToRecognizeStudent: (String) -> Unit,
    onNavigateBackFromMarkAttendanceScreen: (String) -> Unit,
    onRefreshDashboard: () -> Unit,
) {
    composableWithSlideTransitions<AttendanceRoutes.CreateAttendanceSheet> {
        CreateAttendanceRoot(
            onNavigateBack = onNavigateBack,
            onNavigateToMarkAttendance = onNavigateToMarkAttendance
        )
    }

    composableWithSlideTransitions<AttendanceRoutes.MarkStudentAttendance> {
        MarkAttendanceRoot(
            onNavigateBackFromMarkAttendanceScreen = onNavigateBackFromMarkAttendanceScreen
        )
    }


    composableWithSlideTransitions<AttendanceRoutes.StudentAttendanceAnalytics> {
        StudentAttendanceAnalyticsRoot(
            onNavigateBack = onNavigateBack,
            onNavigateToSearchAttendanceForCourse = onNavigateToSearchAttendanceForCourseAndStudent,
        )
    }

    composableWithSlideTransitions<AttendanceRoutes.StudentAttendanceDashboard> {
        StudentAttendanceDashboardRoot(
            onNavigateBack = onNavigateBack,
            onNavigateToSearchAttendanceForCourse = onNavigateToSearchAttendanceForCourseAndStudent,
        )
    }

    composableWithSlideTransitions<AttendanceRoutes.AggregateAttendanceAnalytics> {
        AggregateAttendanceAnalyticsRoot(
            onNavigateBack = onNavigateBack,
            onNavigateToSearchAttendanceForCourse = onNavigateToSearchAttendanceForCourse,
        )
    }

    composableWithSlideTransitions<AttendanceRoutes.SearchAttendance> {
        SearchAttendanceRoot(
            onNavigateBack = onNavigateBack,
            onNavigateToAttendanceDetails = onNavigateToAttendanceDetails
        )
    }

    composableWithSlideTransitions<AttendanceRoutes.AttendanceDetails> {
        AttendanceDetailsRoot(
            onNavigateBack = onNavigateBack,
            onNavigateToEditAttendance = onNavigateToMarkAttendance
        )
    }

    composableWithSlideTransitions<AttendanceRoutes.ScanQr> {
        ScanQrRoot(
            onNavigateBack = onNavigateBack,
            onNavigateToRecognizeStudent = { attendanceId ->
                onNavigateToRecognizeStudent(attendanceId)
            }
        )
    }

    composableWithSlideTransitions<AttendanceRoutes.RecognizeStudent> { backStackEntry ->
         RecognizeStudentRoot(
            onNavigateBack = onRefreshDashboard,
            onSuccess = onNavigateBack
        )
    }

    composableWithSlideTransitions<AttendanceRoutes.Defaulters> {
        DefaultersRoot(
            onNavigateBack = onNavigateBack,
        )
    }
}
