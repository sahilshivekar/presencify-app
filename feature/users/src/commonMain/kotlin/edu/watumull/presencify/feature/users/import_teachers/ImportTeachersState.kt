package edu.watumull.presencify.feature.users.import_teachers

import edu.watumull.presencify.core.presentation.UiText
import edu.watumull.presencify.core.presentation.components.dialog.DialogState

data class ImportTeachersState(
    val viewState: ViewState = ViewState.Content,
    val selectedFile: ByteArray? = null,
    val selectedFileName: String? = null,
    val isSubmitting: Boolean = false,
    val error: UiText? = null,
    val dialogState: DialogState? = null
) {
    sealed interface ViewState {
        data object Loading : ViewState
        data object Content : ViewState
    }
}
