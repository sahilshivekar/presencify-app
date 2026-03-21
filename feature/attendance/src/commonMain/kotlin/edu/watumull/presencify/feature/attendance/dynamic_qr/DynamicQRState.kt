package edu.watumull.presencify.feature.attendance.dynamic_qr

import edu.watumull.presencify.core.domain.model.attendance.Attendance
import edu.watumull.presencify.core.presentation.UiText

data class DynamicQRState(
    val viewState: ViewState = ViewState.Loading,
    val attendance: Attendance? = null,
    val qrCodeContent: String = "",
    val isStopped: Boolean = false,
) {
    sealed interface ViewState {
        data object Loading : ViewState
        data object Content : ViewState
        data class Error(val message: UiText) : ViewState
    }
}
