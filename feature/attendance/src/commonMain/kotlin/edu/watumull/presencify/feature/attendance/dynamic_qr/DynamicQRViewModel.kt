package edu.watumull.presencify.feature.attendance.dynamic_qr

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import edu.watumull.presencify.core.domain.NtpClock
import edu.watumull.presencify.core.domain.onError
import edu.watumull.presencify.core.domain.onSuccess
import edu.watumull.presencify.core.domain.repository.attendance.AttendanceRepository
import edu.watumull.presencify.core.presentation.toUiText
import edu.watumull.presencify.core.presentation.utils.BaseViewModel
import edu.watumull.presencify.feature.attendance.navigation.AttendanceRoutes
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class DynamicQRViewModel(
    private val attendanceRepository: AttendanceRepository,
    private val ntpClock: NtpClock,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<DynamicQRState, DynamicQREvent, DynamicQRAction>(
    initialState = DynamicQRState()
) {
    private val attendanceId: String = savedStateHandle.toRoute<AttendanceRoutes.DynamicQR>().attendanceId
    private var timerJob: Job? = null

    init {
        ntpClock.startPeriodicSync(viewModelScope)
        loadAttendance()
        startQrGeneration()
    }

    override fun handleAction(action: DynamicQRAction) {
        when (action) {
            DynamicQRAction.StopButtonClick -> stopQrGeneration()
            DynamicQRAction.ShowQRClick -> startQrGeneration()
            DynamicQRAction.NavigateToDetails -> sendEvent(DynamicQREvent.NavigateToDetails(attendanceId))
            DynamicQRAction.NavigateBack -> sendEvent(DynamicQREvent.NavigateBack)
        }
    }

    private fun loadAttendance() {
        viewModelScope.launch {
            attendanceRepository.getAttendanceById(attendanceId)
                .onSuccess { attendance ->
                    updateState {
                        it.copy(
                            viewState = DynamicQRState.ViewState.Content,
                            attendance = attendance
                        )
                    }
                }
                .onError { error ->
                    updateState {
                        it.copy(
                            viewState = DynamicQRState.ViewState.Error(error.toUiText())
                        )
                    }
                }
        }
    }

    private fun startQrGeneration() {
        if (timerJob?.isActive == true) return

        if (!ntpClock.isSynced()) {
            updateState {
                it.copy(
                    viewState = DynamicQRState.ViewState.Error(
                        edu.watumull.presencify.core.presentation.UiText.DynamicString(
                            "Unable to synchronize network time. Please check your internet connection and try again."
                        )
                    )
                )
            }
            return
        }

        timerJob = viewModelScope.launch {
            while (isActive) {
                val timestamp = ntpClock.now()
                val rawContent = "$attendanceId|$timestamp"
                val qrContent = DynamicQRCipher.encrypt(rawContent)
                updateState { it.copy(qrCodeContent = qrContent, isStopped = false) }
                delay(1000L)
            }
        }
    }

    private fun stopQrGeneration() {
        timerJob?.cancel()
        timerJob = null
        updateState { it.copy(isStopped = true, qrCodeContent = "") }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
