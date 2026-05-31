package edu.watumull.presencify.feature.academics.university_details

import edu.watumull.presencify.core.domain.model.academics.University
import edu.watumull.presencify.core.presentation.UiText
import edu.watumull.presencify.core.presentation.components.dialog.DialogState

data class UniversityDetailsState(
    val viewState: ViewState = ViewState.Loading,
    val dialogState: DialogState? = null,
    val universities: List<University> = emptyList(),
    val removingUniversityId: String? = null,
    val universityIdToDelete: String? = null,
) {
    sealed interface ViewState {
        data object Loading : ViewState
        data class Error(val message: UiText) : ViewState
        data object Content : ViewState
    }
}
