package edu.watumull.presencify.feature.academics.scheme_details

sealed interface SchemeDetailsAction {
    data object NavigateBack : SchemeDetailsAction
    data object DismissDialog : SchemeDetailsAction
    data object RemoveSchemeClick : SchemeDetailsAction
    data object ConfirmRemoveScheme : SchemeDetailsAction
    data object EditSchemeClick : SchemeDetailsAction
}

