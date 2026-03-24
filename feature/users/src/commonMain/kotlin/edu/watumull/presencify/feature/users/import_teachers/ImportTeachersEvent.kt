package edu.watumull.presencify.feature.users.import_teachers

sealed interface ImportTeachersEvent {
    data object NavigateBack : ImportTeachersEvent
}
