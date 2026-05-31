package edu.watumull.presencify.feature.attendance.dynamic_qr


sealed interface DynamicQREvent {
    data class NavigateToDetails(val attendanceId: String) : DynamicQREvent
    data object NavigateBack : DynamicQREvent
}

sealed interface DynamicQRAction {
    data object StopButtonClick : DynamicQRAction
    data object ShowQRClick : DynamicQRAction
    data object NavigateToDetails : DynamicQRAction
    data object NavigateBack : DynamicQRAction
}
