package edu.watumull.presencify.feature.academics.search_branch

sealed interface SearchBranchEvent {
    data object NavigateBack : SearchBranchEvent
    data class NavigateToBranchDetails(val branchId: String) : SearchBranchEvent
    data object NavigateToAddEditBranch : SearchBranchEvent
}

