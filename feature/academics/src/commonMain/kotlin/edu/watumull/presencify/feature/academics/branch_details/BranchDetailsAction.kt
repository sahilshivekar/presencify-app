package edu.watumull.presencify.feature.academics.branch_details

sealed interface BranchDetailsAction {
    data object NavigateBack : BranchDetailsAction
    data object DismissDialog : BranchDetailsAction
    data object RemoveBranchClick : BranchDetailsAction
    data object ConfirmRemoveBranch : BranchDetailsAction
    data object EditBranchClick : BranchDetailsAction
}

