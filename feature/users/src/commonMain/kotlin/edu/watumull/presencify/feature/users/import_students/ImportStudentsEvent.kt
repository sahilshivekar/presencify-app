package edu.watumull.presencify.feature.users.import_students

sealed interface ImportStudentsEvent {
    data object NavigateBack : ImportStudentsEvent
}
