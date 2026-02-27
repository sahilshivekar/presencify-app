package edu.watumull.presencify.feature.attendance.navigation

import edu.watumull.presencify.core.presentation.navigation.NavRoute
import kotlinx.serialization.Serializable

sealed interface AttendanceRoutes : NavRoute {

    @Serializable
    data object AttendanceDashboard : AttendanceRoutes

    @Serializable
    data class CreateAttendanceSheet(val classId: String) : AttendanceRoutes

    @Serializable
    data class MarkStudentAttendance(val attendanceId: String) : AttendanceRoutes

    @Serializable
    data class StudentAttendanceAnalytics(
        val studentId: String,
        val semesterId: String?,
        val divisionId: String?,
        val batchId: String?,
        val startDate: String?,
        val endDate: String?,
        val courseId: String?,
        val semesterNumber: String?,
        val academicStartYear: String?,
        val academicEndYear: String?,
        val branchId: String?,
        val schemeId: String?,
    ) : AttendanceRoutes

    @Serializable
    data class AggregateAttendanceAnalytics(
        val semesterId: String?,
        val divisionId: String?,
        val batchId: String?,
        val startDate: String?,
        val endDate: String?,
        val courseId: String?,
        val semesterNumber: String?,
        val academicStartYear: String?,
        val academicEndYear: String?,
        val branchId: String?,
        val schemeId: String?,
    ) : AttendanceRoutes

    @Serializable
    data class SearchAttendance(
        val courseId: String?,
        val studentId: String?,
        val startDate: String?,
        val endDate: String?,
        val semesterId: String?,
        val batchId: String?,
        val divisionId: String?,
    ): AttendanceRoutes

    @Serializable
    data class AttendanceDetails(
        val attendanceId: String
    ): AttendanceRoutes

}
