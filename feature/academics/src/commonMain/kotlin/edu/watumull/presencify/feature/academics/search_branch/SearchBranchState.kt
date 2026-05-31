package edu.watumull.presencify.feature.academics.search_branch

import androidx.compose.runtime.Stable
import edu.watumull.presencify.core.designsystem.components.dialog.DialogType
import edu.watumull.presencify.core.domain.model.academics.Branch
import edu.watumull.presencify.core.presentation.UiText
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

@Stable
data class SearchBranchState(
    val viewState: ViewState = ViewState.Content,

    // Search
    val searchQuery: String = "",
    val isRefreshing: Boolean = false,

    // Branches List
    val branches: PersistentList<Branch> = persistentListOf(),
    val isLoadingBranches: Boolean = true
) {
    sealed interface ViewState {
        data object Loading : ViewState
        data class Error(val message: UiText) : ViewState
        data object Content : ViewState
    }
}

