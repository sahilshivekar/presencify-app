package edu.watumull.presencify.feature.academics.batch_details

sealed interface BatchDetailsAction {
    data object NavigateBack : BatchDetailsAction
    data object DismissDialog : BatchDetailsAction
    data object RemoveBatchClick : BatchDetailsAction
    data object ConfirmRemoveBatch : BatchDetailsAction
    data object EditBatchClick : BatchDetailsAction
}

