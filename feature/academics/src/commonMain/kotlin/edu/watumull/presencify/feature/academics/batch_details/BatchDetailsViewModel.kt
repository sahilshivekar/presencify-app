package edu.watumull.presencify.feature.academics.batch_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import edu.watumull.presencify.core.designsystem.components.dialog.DialogType
import edu.watumull.presencify.core.domain.onError
import edu.watumull.presencify.core.domain.onSuccess
import edu.watumull.presencify.core.domain.repository.academics.BatchRepository
import edu.watumull.presencify.core.presentation.UiText
import edu.watumull.presencify.core.presentation.components.dialog.DialogState
import edu.watumull.presencify.core.presentation.global_snackbar.SnackbarController
import edu.watumull.presencify.core.presentation.global_snackbar.SnackbarEvent
import edu.watumull.presencify.core.presentation.toUiText
import edu.watumull.presencify.core.presentation.utils.BaseViewModel
import edu.watumull.presencify.feature.academics.navigation.AcademicsRoutes
import kotlinx.coroutines.launch

class BatchDetailsViewModel(
    private val batchRepository: BatchRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<BatchDetailsState, BatchDetailsEvent, BatchDetailsAction>(
    initialState = BatchDetailsState(
        batchId = savedStateHandle.toRoute<AcademicsRoutes.BatchDetails>().batchId
    )
) {

    init {
        viewModelScope.launch {
            loadBatch()
        }
    }

    private suspend fun loadBatch() {
        val batchId = state.batchId

        batchRepository.getBatchById(batchId)
            .onSuccess { batch ->
                updateState { it.copy(viewState = BatchDetailsState.ViewState.Content, batch = batch) }
            }
            .onError { error ->
                updateState { it.copy(viewState = BatchDetailsState.ViewState.Error(error.toUiText())) }
            }
    }

    override fun handleAction(action: BatchDetailsAction) {
        when (action) {
            is BatchDetailsAction.NavigateBack -> sendEvent(BatchDetailsEvent.NavigateBack)
            is BatchDetailsAction.DismissDialog -> updateState { it.copy(dialogState = null) }
            is BatchDetailsAction.RemoveBatchClick -> updateState {
                it.copy(
                    dialogState = DialogState(
                        dialogType = DialogType.CONFIRM_RISKY_ACTION,
                        title = UiText.DynamicString("Remove Batch"),
                        message = UiText.DynamicString(
                            "Are you sure you want to remove this batch? This action cannot be undone and all associated attendance records will also be removed."
                        )
                    )
                )
            }
            is BatchDetailsAction.ConfirmRemoveBatch -> {
                viewModelScope.launch {
                    removeBatch()
                }
            }
            is BatchDetailsAction.EditBatchClick -> sendEvent(BatchDetailsEvent.NavigateToEditBatch(state.batchId))
        }
    }

    private suspend fun removeBatch() {
        val batchId = state.batch?.id ?: return

        updateState { it.copy(isRemovingBatch = true, dialogState = null) }

        batchRepository.removeBatch(batchId)
            .onSuccess {
                updateState { it.copy(isRemovingBatch = false) }
                viewModelScope.launch {
                    SnackbarController.sendEvent(
                        SnackbarEvent(message = "Batch removed successfully")
                    )
                }
                sendEvent(BatchDetailsEvent.NavigateBack)
            }
            .onError { error ->
                updateState {
                    it.copy(
                        isRemovingBatch = false,
                        dialogState = DialogState(
                            dialogType = DialogType.ERROR,
                            title = UiText.DynamicString("Error Removing Batch"),
                            message = error.toUiText()
                        )
                    )
                }
            }
    }
}

