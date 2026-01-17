package edu.watumull.presencify.feature.academics.search_semester

sealed interface SearchSemesterEvent {
    data object NavigateBack : SearchSemesterEvent
    data class NavigateToSemesterDetails(val semesterId: String) : SearchSemesterEvent
    data object NavigateToAddEditSemester : SearchSemesterEvent
}

