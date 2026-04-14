package edu.watumull.presencify.feature.users.import_students

import edu.watumull.presencify.core.presentation.UiText
import edu.watumull.presencify.core.design.systems.components.dialog.DialogType

data class ImportStudentsState(
    val viewState: ViewState = ViewState.Content,
    val selectedFile: ByteArray? = null,
    val selectedFileName: String? = null,
    val isSubmitting: Boolean = false,
    val error: UiText? = null,
    val businessErrorText: String? = null,
    val dialogState: DialogState? = null
) {
    sealed interface ViewState {
        data object Loading : ViewState
        data object Content : ViewState
    }

    data class DialogState(
        val isVisible: Boolean,
        val title: UiText,
        val message: UiText?,
        val dialogType: DialogType,
        val dialogIntention: DialogIntention
    )

    enum class DialogIntention {
        GENERIC
    }
}
