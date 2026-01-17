package edu.watumull.presencify.feature.academics.search_scheme

sealed interface SearchSchemeEvent {
    data object NavigateBack : SearchSchemeEvent
    data class NavigateToSchemeDetails(val schemeId: String) : SearchSchemeEvent
    data object NavigateToAddEditScheme : SearchSchemeEvent
}

