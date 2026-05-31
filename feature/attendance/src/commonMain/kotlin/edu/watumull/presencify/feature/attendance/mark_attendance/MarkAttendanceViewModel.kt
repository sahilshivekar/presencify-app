package edu.watumull.presencify.feature.attendance.mark_attendance

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import edu.watumull.presencify.core.domain.onError
import edu.watumull.presencify.core.domain.onSuccess
import edu.watumull.presencify.core.domain.repository.attendance.AttendanceRepository
import edu.watumull.presencify.core.presentation.components.ListItemFeedback
import edu.watumull.presencify.core.presentation.toUiText
import edu.watumull.presencify.core.presentation.utils.BaseViewModel
import edu.watumull.presencify.core.presentation.utils.ShareUtils
import edu.watumull.presencify.core.presentation.utils.toReadableString
import edu.watumull.presencify.feature.attendance.navigation.AttendanceRoutes
import kotlinx.coroutines.launch

class MarkAttendanceViewModel(
    private val attendanceRepository: AttendanceRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<MarkAttendanceState, MarkAttendanceEvent, MarkAttendanceAction>(
    initialState = MarkAttendanceState()
) {
    private val attendanceId: String = savedStateHandle.toRoute<AttendanceRoutes.MarkStudentAttendance>().attendanceId

    init {
        loadAttendance()
    }

    override fun handleAction(action: MarkAttendanceAction) {
        when (action) {
            MarkAttendanceAction.NavigateBack -> sendEvent(MarkAttendanceEvent.NavigateBack)
            MarkAttendanceAction.DynamicQRClick -> sendEvent(MarkAttendanceEvent.NavigateToDynamicQR(attendanceId))
            MarkAttendanceAction.GroupPhotoScanClick -> sendEvent(MarkAttendanceEvent.NavigateToGroupPhotoScan(attendanceId))
            MarkAttendanceAction.ShareAttendanceSummary -> shareAttendanceSummary()
            is MarkAttendanceAction.ToggleStudentAttendance -> toggleStudentAttendance(
                action.studentId,
                action.currentStatus
            )
        }
    }

    private fun loadAttendance() {
        viewModelScope.launch {
            attendanceRepository.getAttendanceById(attendanceId)
                .onSuccess { attendance ->
                    val attendanceStudents = attendance.attendanceStudents ?: emptyList()
                    val presentCount = attendanceStudents.count { it.attendanceStatus }
                    val totalCount = attendanceStudents.size
                    val absentCount = totalCount - presentCount

                    updateState { it.copy(
                        viewState = MarkAttendanceState.ViewState.Content,
                        attendance = attendance,
                        classSession = attendance.klass,
                        totalStudents = totalCount,
                        presentStudents = presentCount,
                        absentStudents = absentCount
                    ) }
                }
                .onError { error ->
                    updateState { it.copy(
                        viewState = MarkAttendanceState.ViewState.Error(error.toUiText())
                    ) }
                }
        }
    }

    private fun shareAttendanceSummary() {
        val attendance = state.attendance ?: return
        val classSession = state.classSession

        val builder = StringBuilder()

        // Header (exact same format as AttendanceDetails)
        builder.appendLine("Attendance Details")
        builder.appendLine()

        // Date
        builder.appendLine("Date: ${attendance.date.toReadableString()}")

        // Class / course details
        classSession?.let { session ->
            val division = session.timetable?.division
            val batch = session.batch
            val semester = division?.semester
            val branch = semester?.branch

            val divisionBatchText = when {
                batch != null -> batch.batchCode
                division != null -> division.divisionCode
                else -> null
            }

            val semesterText = semester?.let { sem ->
                val semNum = sem.semesterNumber.value
                val academicYear = "${sem.academicStartYear}-${sem.academicEndYear}"
                "Sem $semNum $academicYear"
            }

            builder.appendLine("Course: ${session.course?.name ?: "Unknown Course"}")
            builder.appendLine(
                "Teacher: " + (session.teacher?.let { "${it.firstName} ${it.lastName}" }
                    ?: "Unknown Teacher")
            )
            divisionBatchText?.let { builder.appendLine("Division/Batch: $it") }
            branch?.abbreviation?.let { builder.appendLine("Branch: $it") }
            semesterText?.let { builder.appendLine("Semester: $it") }
            builder.appendLine(
                "Time: ${session.startTime.toReadableString()} - ${session.endTime.toReadableString()}"
            )
        }

        builder.appendLine()

        // Stats (same labels/casing as AttendanceDetails)
        builder.appendLine("Total Students: ${state.totalStudents}")
        builder.appendLine("Present Students: ${state.presentStudents}")
        builder.appendLine("Absent Students: ${state.absentStudents}")

        builder.appendLine()

        val attendanceStudents = attendance.attendanceStudents ?: emptyList()

        // Present students list
        val presentStudents = attendanceStudents.filter { it.attendanceStatus }
        builder.appendLine("Present Students (${presentStudents.size}):")
        if (presentStudents.isEmpty()) {
            builder.appendLine("- None")
        } else {
            presentStudents.forEachIndexed { index, attendanceStudent ->
                val student = attendanceStudent.student
                val name = student?.let { "${it.firstName} ${it.lastName}" } ?: "Unknown Student"
                val prn = student?.prn?.let { " (PRN: $it)" } ?: ""
                builder.appendLine("${index + 1}. $name$prn")
            }
        }

        builder.appendLine()

        // Absent students list
        val absentStudents = attendanceStudents.filter { !it.attendanceStatus }
        builder.appendLine("Absent Students (${absentStudents.size}):")
        if (absentStudents.isEmpty()) {
            builder.appendLine("- None")
        } else {
            absentStudents.forEachIndexed { index, attendanceStudent ->
                val student = attendanceStudent.student
                val name = student?.let { "${it.firstName} ${it.lastName}" } ?: "Unknown Student"
                val prn = student?.prn?.let { " (PRN: $it)" } ?: ""
                builder.appendLine("${index + 1}. $name$prn")
            }
        }

        val shareText = builder.toString().trimEnd()
        if (shareText.isBlank()) return

        viewModelScope.launch {
            ShareUtils.shareText(shareText)
        }
    }

    private fun toggleStudentAttendance(studentId: String, currentStatus: Boolean) {
        // Set loading state for this student
        updateState { it.copy(
            studentLoadingStates = it.studentLoadingStates + (studentId to true),
            studentFeedbacks = it.studentFeedbacks - studentId // Clear previous feedback
        ) }

        viewModelScope.launch {
            val newStatus = !currentStatus
            attendanceRepository.updateStudentAttendance(
                attendanceId = attendanceId,
                studentId = studentId,
                newAttendanceStatus = newStatus
            )
                .onSuccess { updatedAttendanceStudent ->
                    // Update the attendance student in the list
                    val currentState = state
                    val updatedAttendanceStudents = currentState.attendance?.attendanceStudents?.map { attendanceStudent ->
                        if (attendanceStudent.studentId == studentId) {
                            attendanceStudent.copy(attendanceStatus = updatedAttendanceStudent.attendanceStatus)
                        } else {
                            attendanceStudent
                        }
                    }

                    // Recalculate counts
                    val presentCount = updatedAttendanceStudents?.count { it.attendanceStatus } ?: 0
                    val totalCount = updatedAttendanceStudents?.size ?: 0
                    val absentCount = totalCount - presentCount

                    updateState { it.copy(
                        attendance = it.attendance?.copy(attendanceStudents = updatedAttendanceStudents),
                        presentStudents = presentCount,
                        absentStudents = absentCount,
                        studentLoadingStates = it.studentLoadingStates - studentId
                        // No feedback for success
                    ) }
                }
                .onError { error ->
                    // Show error feedback permanently - user must take action to fix
                    updateState { it.copy(
                        studentLoadingStates = it.studentLoadingStates - studentId,
                        studentFeedbacks = it.studentFeedbacks + (studentId to ListItemFeedback.Error(
                            error.toUiText()
                        ))
                    ) }
                }
        }
    }
}
