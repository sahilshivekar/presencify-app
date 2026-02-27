package edu.watumull.presencify.feature.attendance.navigation

import androidx.navigation.NavGraphBuilder
import edu.watumull.presencify.core.design.systems.components.composableWithSlideTransitions
import edu.watumull.presencify.feature.attendance.attendance_dashboard.AttendanceDashboardRoot
import edu.watumull.presencify.feature.attendance.attendance_details.AttendanceDetailsRoot
import edu.watumull.presencify.feature.attendance.create_attendance.CreateAttendanceRoot
import edu.watumull.presencify.feature.attendance.mark_attendance.MarkAttendanceRoot
import edu.watumull.presencify.feature.attendance.search_attendance.SearchAttendanceRoot

fun NavGraphBuilder.attendanceDashboard(
    onNavigateBack: () -> Unit,
    onNavigateToStudentAttendanceAnalytics: () -> Unit,
    onNavigateToAggregateAttendanceAnalytics: () -> Unit,
    onNavigateToSearchAttendance: () -> Unit,
    onNavigateToCreateAttendance: () -> Unit
) {
    composableWithSlideTransitions<AttendanceRoutes.AttendanceDashboard> {
        AttendanceDashboardRoot(
            onNavigateBack = onNavigateBack,
            onNavigateToStudentAttendanceAnalytics = onNavigateToStudentAttendanceAnalytics,
            onNavigateToAggregateAttendanceAnalytics = onNavigateToAggregateAttendanceAnalytics,
            onNavigateToSearchAttendance = onNavigateToSearchAttendance,
            onNavigateToCreateAttendance = onNavigateToCreateAttendance
        )
    }
}

fun NavGraphBuilder.attendanceNavGraph(
    onNavigateBack: () -> Unit,
    onNavigateToMarkAttendance: (String) -> Unit,
    onNavigateToAttendanceDetails: (String) -> Unit
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
            onNavigateBack = onNavigateBack
        )
    }

    // 4. Individual Student Analytics
    composableWithSlideTransitions<AttendanceRoutes.StudentAttendanceAnalytics> {
        // Access args via: it.toRoute<AttendanceRoutes.StudentAttendanceAnalytics>()
        // TODO: Add StudentAttendanceAnalyticsScreen()
    }

    // 5. Aggregate (Batch/Group) Analytics
    composableWithSlideTransitions<AttendanceRoutes.AggregateAttendanceAnalytics> {
        // TODO: Add AggregateAttendanceAnalyticsScreen()
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
}

