package edu.watumull.presencify.feature.attendance.recognize_student

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import edu.watumull.presencify.core.presentation.utils.BaseViewModel
import edu.watumull.presencify.feature.attendance.navigation.AttendanceRoutes
import kotlinx.coroutines.launch

class RecognizeStudentViewModel(
    savedStateHandle: SavedStateHandle
) : BaseViewModel<RecognizeStudentState, RecognizeStudentEvent, RecognizeStudentAction>(
    initialState = RecognizeStudentState()
) {
    private val attendanceId = savedStateHandle.toRoute<AttendanceRoutes.RecognizeStudent>().attendanceId

    override fun handleAction(action: RecognizeStudentAction) {
        when (action) {
            is RecognizeStudentAction.NavigateBack -> {
                viewModelScope.launch {
                    sendEvent(RecognizeStudentEvent.NavigateBack)
                }
            }
        }
    }
}
