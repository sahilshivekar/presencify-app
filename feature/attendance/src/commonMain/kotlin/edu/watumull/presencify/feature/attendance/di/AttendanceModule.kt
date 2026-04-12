package edu.watumull.presencify.feature.attendance.di

import edu.watumull.presencify.feature.attendance.add_student_biometrics.AddStudentBiometricsViewModel
import edu.watumull.presencify.feature.attendance.aggregate_analytics.AggregateAttendanceAnalyticsViewModel
import edu.watumull.presencify.feature.attendance.attendance_dashboard.AttendanceDashboardViewModel
import edu.watumull.presencify.feature.attendance.attendance_details.AttendanceDetailsViewModel
import edu.watumull.presencify.feature.attendance.create_attendance.CreateAttendanceViewModel
import edu.watumull.presencify.feature.attendance.defaulters.DefaultersViewModel
import edu.watumull.presencify.feature.attendance.dynamic_qr.DynamicQRViewModel
import edu.watumull.presencify.feature.attendance.mark_attendance.MarkAttendanceViewModel
import edu.watumull.presencify.feature.attendance.scan_qr.ScanQrViewModel
import edu.watumull.presencify.feature.attendance.search_attendance.SearchAttendanceViewModel
import edu.watumull.presencify.feature.attendance.student_analytics.StudentAttendanceAnalyticsViewModel
import edu.watumull.presencify.feature.attendance.recognize_student.RecognizeStudentViewModel
import edu.watumull.presencify.feature.attendance.group_photo_scan.GroupPhotoScanViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val attendanceModule = module {
    viewModel { AttendanceDashboardViewModel() }
    viewModel { CreateAttendanceViewModel(get(), get(), get()) }
    viewModel { MarkAttendanceViewModel(get(), get()) }
    viewModel { ScanQrViewModel(get()) }
    viewModel { RecognizeStudentViewModel(get(), get(), get(), get(), get()) }
    viewModel { DynamicQRViewModel(get(), get(), get()) }
    viewModel { SearchAttendanceViewModel(get(), get(), get(), get(), get(), get(), get()) }
    viewModel { AttendanceDetailsViewModel(get(), get()) }
    viewModel { StudentAttendanceAnalyticsViewModel(get(), get(), get(), get(), get()) }
    viewModel { AggregateAttendanceAnalyticsViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { AddStudentBiometricsViewModel(get(), get()) }
    viewModel { GroupPhotoScanViewModel(get(), get()) }
    viewModel { DefaultersViewModel(get(), get(), get(), get()) }
}
