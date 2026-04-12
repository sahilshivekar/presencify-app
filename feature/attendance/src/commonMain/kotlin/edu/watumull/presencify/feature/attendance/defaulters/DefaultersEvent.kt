package edu.watumull.presencify.feature.attendance.defaulters

sealed interface DefaultersEvent {
    data object NavigateBack : DefaultersEvent
}
