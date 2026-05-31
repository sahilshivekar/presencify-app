package edu.watumull.presencify.feature.academics.division_details

sealed interface DivisionDetailsAction {
    data object NavigateBack : DivisionDetailsAction
    data object DismissDialog : DivisionDetailsAction
    data object RemoveDivisionClick : DivisionDetailsAction
    data object ConfirmRemoveDivision : DivisionDetailsAction
    data object EditDivisionClick : DivisionDetailsAction
}

