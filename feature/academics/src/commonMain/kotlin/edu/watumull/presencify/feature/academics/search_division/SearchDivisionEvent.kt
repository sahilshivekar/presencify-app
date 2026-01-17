package edu.watumull.presencify.feature.academics.search_division

sealed interface SearchDivisionEvent {
    data object NavigateBack : SearchDivisionEvent
    data class NavigateToDivisionDetails(val divisionId: String) : SearchDivisionEvent
    data object NavigateToAddEditDivision : SearchDivisionEvent
}

