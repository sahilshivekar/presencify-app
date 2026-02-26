package edu.watumull.presencify.feature.schedule.add_edit_class

sealed interface AddEditClassEvent {
    data object NavigateBack : AddEditClassEvent
}
