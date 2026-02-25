package edu.watumull.presencify.feature.users.mark_unmark_student_dropout

import edu.watumull.presencify.core.design.systems.components.dialog.DialogType
import edu.watumull.presencify.core.presentation.UiText

data class MarkUnmarkStudentAsDropoutState(
    val viewState: ViewState = ViewState.Content,

    val startYear: String = "",
    val endYear: String = "",

    val startYearError: String? = null,
    val endYearError: String? = null,

    val dialogState: DialogState? = null,
) {
    sealed interface ViewState {
        data object Loading : ViewState
        data class Error(val message: UiText) : ViewState
        data object Content : ViewState
    }

    data class DialogState(
        val isVisible: Boolean = true,
        val dialogType: DialogType,
        val title: String,
        val message: UiText,
        val dialogIntention: DialogIntention
    )
}

enum class DialogIntention {
    GENERIC
}
