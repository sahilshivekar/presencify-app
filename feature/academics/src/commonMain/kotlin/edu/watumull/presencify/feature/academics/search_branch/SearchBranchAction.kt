package edu.watumull.presencify.feature.academics.search_branch

sealed interface SearchBranchAction {
    data object NavigateBack : SearchBranchAction

    data class UpdateSearchQuery(val query: String) : SearchBranchAction
    data object Search : SearchBranchAction
    data object Refresh : SearchBranchAction

    data class BranchCardClick(val branchId: String) : SearchBranchAction

    data object ClickFloatingActionButton : SearchBranchAction
}

