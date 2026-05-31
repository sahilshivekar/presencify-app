package edu.watumull.presencify.feature.academics.add_edit_branch

import edu.watumull.presencify.core.presentation.components.dialog.DialogState

data class AddEditBranchState(
    val branchId: String? = null,
    val isEditMode: Boolean = false,
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,

    val name: String = "",
    val abbreviation: String = "",

    val nameError: String? = null,
    val abbreviationError: String? = null,

    val dialogState: DialogState? = null,
) {
    sealed interface ViewState
}

