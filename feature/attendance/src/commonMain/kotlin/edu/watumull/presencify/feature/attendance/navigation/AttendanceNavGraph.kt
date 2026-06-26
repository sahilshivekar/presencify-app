package edu.watumull.presencify.feature.attendance.navigation

import androidx.navigation.NavGraphBuilder
import edu.watumull.presencify.core.designsystem.components.composableWithSlideTransitions
import edu.watumull.presencify.feature.attendance.aggregate_analytics.AggregateAttendanceAnalyticsRoot
import edu.watumull.presencify.feature.attendance.attendance_dashboard.AttendanceDashboardRoot
import edu.watumull.presencify.feature.attendance.attendance_details.AttendanceDetailsRoot
import edu.watumull.presencify.feature.attendance.create_attendance.CreateAttendanceRoot
import edu.watumull.presencify.feature.attendance.defaulters.DefaultersRoot
import edu.watumull.presencify.feature.attendance.dynamic_qr.DynamicQRRoot
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
    onNavigateToScanQr: () -> Unit,
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

    // for student dashboard when role is student
    composableWithSlideTransitions<AttendanceRoutes.StudentAttendanceDashboard> {
        StudentAttendanceDashboardRoot(
            onNavigateBack = onNavigateBack,
            onNavigateToSearchAttendanceForCourse = onNavigateToSearchAttendanceForCourseAndStudent,
            onNavigateToScanQr = onNavigateToScanQr
        )
    }
}

fun NavGraphBuilder.attendanceNavGraph(
    onNavigateBack: () -> Unit,
    onNavigateToMarkAttendance: (String) -> Unit,
    onNavigateToDynamicQR: (String) -> Unit,
    onNavigateToAttendanceDetails: (String) -> Unit,
    onNavigateToSearchAttendanceForCourse: (String, String?) -> Unit,
    onNavigateToSearchAttendanceForCourseAndStudent: (courseId: String, studentId: String) -> Unit,
    onNavigateToRecognizeStudent: (String) -> Unit,
    onNavigateToScanQr: () -> Unit,
) {
    // 2. Create/Update Sheet
    composableWithSlideTransitions<AttendanceRoutes.CreateAttendanceSheet> {
        CreateAttendanceRoot(
            onNavigateBack = onNavigateBack,
            onNavigateToMarkAttendance = onNavigateToMarkAttendance
        )
    }

    // 3. Mark Attendance
    composableWithSlideTransitions<AttendanceRoutes.MarkStudentAttendance> {
        MarkAttendanceRoot(
            onNavigateBack = onNavigateBack,
            onNavigateToDynamicQR = onNavigateToDynamicQR,
        )
    }

    // 4. Dynamic QR
    composableWithSlideTransitions<AttendanceRoutes.DynamicQR> {
        DynamicQRRoot(
            onNavigateBack = onNavigateBack,
            onNavigateToDetails = onNavigateToAttendanceDetails
        )
    }

    composableWithSlideTransitions<AttendanceRoutes.StudentAttendanceAnalytics> {
        StudentAttendanceAnalyticsRoot(
            onNavigateBack = onNavigateBack,
            onNavigateToSearchAttendanceForCourse = onNavigateToSearchAttendanceForCourseAndStudent,
            onNavigateToScanQr = onNavigateToScanQr
        )
    }

    composableWithSlideTransitions<AttendanceRoutes.StudentAttendanceDashboard> {
        StudentAttendanceDashboardRoot(
            onNavigateBack = onNavigateBack,
            onNavigateToSearchAttendanceForCourse = onNavigateToSearchAttendanceForCourseAndStudent,
            onNavigateToScanQr = onNavigateToScanQr
        )
    }

    // 5. Aggregate (Batch/Group) Analytics
    composableWithSlideTransitions<AttendanceRoutes.AggregateAttendanceAnalytics> {
        AggregateAttendanceAnalyticsRoot(
            onNavigateBack = onNavigateBack,
            onNavigateToSearchAttendanceForCourse = onNavigateToSearchAttendanceForCourse,
        )
    }

    // 6. Search
    composableWithSlideTransitions<AttendanceRoutes.SearchAttendance> {
        SearchAttendanceRoot(
            onNavigateBack = onNavigateBack,
            onNavigateToAttendanceDetails = onNavigateToAttendanceDetails
        )
    }

    // 7. Specific Attendance Details
    composableWithSlideTransitions<AttendanceRoutes.AttendanceDetails> {
        AttendanceDetailsRoot(
            onNavigateBack = onNavigateBack,
            onNavigateToEditAttendance = onNavigateToMarkAttendance
        )
    }

    // 8. Scan QR
    composableWithSlideTransitions<AttendanceRoutes.ScanQr> {
        ScanQrRoot(
            onNavigateBack = onNavigateBack,
            onNavigateToRecognizeStudent = { attendanceId ->
                onNavigateToRecognizeStudent(attendanceId)
            }
        )
    }

    // 9. Face Scan
    composableWithSlideTransitions<AttendanceRoutes.RecognizeStudent> { backStackEntry ->
         RecognizeStudentRoot(
            onNavigateBack = onNavigateBack,
            onSuccess = onNavigateBack
        )
    }

    // 10. Defaulters
    composableWithSlideTransitions<AttendanceRoutes.Defaulters> {
        DefaultersRoot(
            onNavigateBack = onNavigateBack,
        )
    }
}
