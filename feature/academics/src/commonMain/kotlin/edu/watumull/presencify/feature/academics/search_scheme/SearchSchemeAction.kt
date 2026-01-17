package edu.watumull.presencify.feature.academics.search_scheme

sealed interface SearchSchemeAction {
    data object BackButtonClick : SearchSchemeAction
    data object DismissDialog : SearchSchemeAction

    // Search & Refresh
    data class UpdateSearchQuery(val query: String) : SearchSchemeAction
    data object Search : SearchSchemeAction
    data object Refresh : SearchSchemeAction

    // Scheme Card Click
    data class SchemeCardClick(val schemeId: String) : SearchSchemeAction

    data object ClickFloatingActionButton : SearchSchemeAction
}

