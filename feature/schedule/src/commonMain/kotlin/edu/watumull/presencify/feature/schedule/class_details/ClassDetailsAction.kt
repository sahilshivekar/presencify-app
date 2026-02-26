package edu.watumull.presencify.feature.schedule.class_details

sealed interface ClassDetailsAction {
    data object BackButtonClick : ClassDetailsAction
    data object DismissDialog : ClassDetailsAction
    data object RemoveClassClick : ClassDetailsAction
    data object ConfirmRemoveClass : ClassDetailsAction
    data object EditClassClick : ClassDetailsAction
}
