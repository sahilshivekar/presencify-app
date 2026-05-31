package edu.watumull.presencify.feature.academics.branch_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import edu.watumull.presencify.core.designsystem.components.dialog.DialogType
import edu.watumull.presencify.core.domain.onError
import edu.watumull.presencify.core.domain.onSuccess
import edu.watumull.presencify.core.domain.repository.academics.BranchRepository
import edu.watumull.presencify.core.presentation.UiText
import edu.watumull.presencify.core.presentation.components.dialog.DialogState
import edu.watumull.presencify.core.presentation.global_snackbar.SnackbarController
import edu.watumull.presencify.core.presentation.global_snackbar.SnackbarController.sendEvent
import edu.watumull.presencify.core.presentation.global_snackbar.SnackbarEvent
import edu.watumull.presencify.core.presentation.toUiText
import edu.watumull.presencify.core.presentation.utils.BaseViewModel
import edu.watumull.presencify.feature.academics.navigation.AcademicsRoutes
import kotlinx.coroutines.launch

class BranchDetailsViewModel(
    private val branchRepository: BranchRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<BranchDetailsState, BranchDetailsEvent, BranchDetailsAction>(
    initialState = BranchDetailsState(
        branchId = savedStateHandle.toRoute<AcademicsRoutes.BranchDetails>().branchId
    )
) {

    init {
        viewModelScope.launch {
            loadBranch()
        }
    }

    private suspend fun loadBranch() {
        val branchId = state.branchId

        branchRepository.getBranchById(branchId)
            .onSuccess { branch ->
                updateState { it.copy(viewState = BranchDetailsState.ViewState.Content, branch = branch) }
            }
            .onError { error ->
                updateState { it.copy(viewState = BranchDetailsState.ViewState.Error(error.toUiText())) }
            }
    }

    override fun handleAction(action: BranchDetailsAction) {
        when (action) {
            is BranchDetailsAction.NavigateBack -> sendEvent(BranchDetailsEvent.NavigateBack)
            is BranchDetailsAction.DismissDialog -> updateState { it.copy(dialogState = null) }
            is BranchDetailsAction.RemoveBranchClick -> updateState {
                it.copy(
                    dialogState = DialogState(
                        dialogType = DialogType.CONFIRM_RISKY_ACTION,
                        title = UiText.DynamicString("Remove Branch"),
                        message = UiText.DynamicString(
                            "Are you sure you want to remove this branch? This action cannot be undone and all associated semesters, divisions, batches and attendance records will also be removed."
                        )
                    )
                )
            }
            is BranchDetailsAction.ConfirmRemoveBranch -> {
                viewModelScope.launch {
                    removeBranch()
                }
            }
            is BranchDetailsAction.EditBranchClick -> sendEvent(BranchDetailsEvent.NavigateToEditBranch(state.branchId))
        }
    }

    private suspend fun removeBranch() {
        val branchId = state.branch?.id ?: return

        updateState { it.copy(isRemovingBranch = true, dialogState = null) }

        branchRepository.removeBranch(branchId)
            .onSuccess {
                updateState { it.copy(isRemovingBranch = false) }
                viewModelScope.launch {
                    SnackbarController.sendEvent(
                        SnackbarEvent(message = "Branch removed successfully")
                    )
                }
                sendEvent(BranchDetailsEvent.NavigateBack)
            }
            .onError { error ->
                updateState {
                    it.copy(
                        isRemovingBranch = false,
                        dialogState = DialogState(
                            dialogType = DialogType.ERROR,
                            title = UiText.DynamicString("Error Removing Branch"),
                            message = error.toUiText()
                        )
                    )
                }
            }
    }
}

