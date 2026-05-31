package edu.watumull.presencify.feature.academics.search_scheme

import androidx.compose.runtime.Stable
import edu.watumull.presencify.core.domain.model.academics.Scheme
import edu.watumull.presencify.core.presentation.UiText
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

@Stable
data class SearchSchemeState(
    val viewState: ViewState = ViewState.Content,

    // Search
    val searchQuery: String = "",
    val isRefreshing: Boolean = false,

    // Schemes List
    val schemes: PersistentList<Scheme> = persistentListOf(),
    val isLoadingSchemes: Boolean = true
) {
    sealed interface ViewState {
        data object Loading : ViewState
        data class Error(val message: UiText) : ViewState
        data object Content : ViewState
    }
}

