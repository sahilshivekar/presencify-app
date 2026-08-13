package edu.watumull.presencify.feature.academics.search_scheme

sealed interface SearchSchemeAction {
    data object NavigateBack : SearchSchemeAction

    data class UpdateSearchQuery(val query: String) : SearchSchemeAction
    data object Search : SearchSchemeAction
    data object Refresh : SearchSchemeAction

    data class SchemeCardClick(val schemeId: String) : SearchSchemeAction

    data object ClickFloatingActionButton : SearchSchemeAction
}

