package edu.watumull.presencify.feature.academics.batch_details

import edu.watumull.presencify.core.domain.model.academics.Batch
import edu.watumull.presencify.core.presentation.UiText
import edu.watumull.presencify.core.presentation.components.dialog.DialogState

data class BatchDetailsState(
    val viewState: ViewState = ViewState.Loading,
    val dialogState: DialogState? = null,
    val batchId: String = "",
    val batch: Batch? = null,
    val isRemovingBatch: Boolean = false,
) {
    sealed interface ViewState {
        data object Loading : ViewState
        data class Error(val message: UiText) : ViewState
        data object Content : ViewState
    }
}

