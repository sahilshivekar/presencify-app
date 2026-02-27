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
            MarkAttendanceAction.BackButtonClick -> sendEvent(MarkAttendanceEvent.NavigateBack)
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
