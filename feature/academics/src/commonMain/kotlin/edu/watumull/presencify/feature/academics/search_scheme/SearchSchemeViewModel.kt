package edu.watumull.presencify.feature.academics.search_scheme

import androidx.lifecycle.viewModelScope
import edu.watumull.presencify.core.design.systems.components.dialog.DialogType
import edu.watumull.presencify.core.domain.onError
import edu.watumull.presencify.core.domain.onSuccess
import edu.watumull.presencify.core.domain.repository.academics.SchemeRepository
import edu.watumull.presencify.core.presentation.toUiText
import edu.watumull.presencify.core.presentation.utils.BaseViewModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SearchSchemeViewModel(
    private val schemeRepository: SchemeRepository,
) : BaseViewModel<SearchSchemeState, SearchSchemeEvent, SearchSchemeAction>(
    initialState = SearchSchemeState()
) {

    init {
        // Load initial data
        loadSchemes()

        // Setup debounced search
        setupDebouncedSearch()
    }

    @OptIn(FlowPreview::class)
    private fun setupDebouncedSearch() {
        viewModelScope.launch {
            stateFlow
                .map { it.searchQuery }
                .debounce(300)
                .distinctUntilChanged()
                .collectLatest { _ ->
                    loadSchemes()
                }
        }
    }

    private fun loadSchemes() {
        viewModelScope.launch {
            updateState { it.copy(isLoadingSchemes = true) }

            schemeRepository.getSchemes(
                searchQuery = stateFlow.value.searchQuery.ifBlank { null }
            )
                .onSuccess { schemes ->
                    updateState {
                        it.copy(
                            schemes = schemes.toPersistentList(),
                            isLoadingSchemes = false,
                            isRefreshing = false
                        )
                    }
                }
                .onError { error ->
                    updateState {
                        it.copy(
                            isLoadingSchemes = false,
                            isRefreshing = false,
                            dialogState = SearchSchemeState.DialogState(
                                dialogType = DialogType.ERROR,
                                title = "Error",
                                message = error.toUiText(),
                                dialogIntention = DialogIntention.GENERIC
                            )
                        )
                    }
                }
        }
    }

    override fun handleAction(action: SearchSchemeAction) {
        when (action) {

            is SearchSchemeAction.BackButtonClick -> {
                sendEvent(SearchSchemeEvent.NavigateBack)
            }

            is SearchSchemeAction.DismissDialog -> {
                updateState { it.copy(dialogState = null) }
            }

            is SearchSchemeAction.UpdateSearchQuery -> {
                updateState { it.copy(searchQuery = action.query) }
            }

            is SearchSchemeAction.Search -> {
                loadSchemes()
            }

            is SearchSchemeAction.Refresh -> {
                updateState { it.copy(isRefreshing = true) }
                loadSchemes()
            }

            is SearchSchemeAction.SchemeCardClick -> {
                sendEvent(SearchSchemeEvent.NavigateToSchemeDetails(action.schemeId))
            }

            SearchSchemeAction.ClickFloatingActionButton -> {
                sendEvent(SearchSchemeEvent.NavigateToAddEditScheme)
            }
        }
    }
}

