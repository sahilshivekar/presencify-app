package edu.watumull.presencify.feature.schedule.class_details

import edu.watumull.presencify.core.designsystem.components.dialog.DialogType
import edu.watumull.presencify.core.domain.model.schedule.ClassSession
import edu.watumull.presencify.core.presentation.UiText

data class ClassDetailsState(
    val viewState: ViewState = ViewState.Loading,
    val dialogState: DialogState? = null,
    val classId: String = "",
    val classSession: ClassSession? = null,
    val isRemovingClass: Boolean = false,
) {
    sealed interface ViewState {
        data object Loading : ViewState
        data class Error(val message: UiText) : ViewState
        data object Content : ViewState
    }

    data class DialogState(
        val isVisible: Boolean = true,
        val dialogType: DialogType = DialogType.INFO,
        val dialogIntention: DialogIntention = DialogIntention.GENERIC,
        val title: String = "",
        val message: UiText? = null,
    )
}

enum class DialogIntention {
    GENERIC,
    CONFIRM_REMOVE_CLASS,
}
