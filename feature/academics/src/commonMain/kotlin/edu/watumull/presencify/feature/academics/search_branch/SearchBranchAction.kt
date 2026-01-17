package edu.watumull.presencify.feature.academics.search_branch

sealed interface SearchBranchAction {
    data object BackButtonClick : SearchBranchAction
    data object DismissDialog : SearchBranchAction

    // Search & Refresh
    data class UpdateSearchQuery(val query: String) : SearchBranchAction
    data object Search : SearchBranchAction
    data object Refresh : SearchBranchAction

    // Branch Card Click
    data class BranchCardClick(val branchId: String) : SearchBranchAction

    data object ClickFloatingActionButton : SearchBranchAction
}

