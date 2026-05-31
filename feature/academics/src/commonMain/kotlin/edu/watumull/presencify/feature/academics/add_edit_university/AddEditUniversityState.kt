package edu.watumull.presencify.feature.academics.add_edit_university

import edu.watumull.presencify.core.designsystem.components.dialog.DialogType
import edu.watumull.presencify.core.presentation.UiText
import edu.watumull.presencify.core.presentation.components.dialog.DialogState

data class AddEditUniversityState(
    val universityId: String? = null,
    val isEditMode: Boolean = false,
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,

    val name: String = "",
    val abbreviation: String = "",

    val nameError: String? = null,
    val abbreviationError: String? = null,

    val dialogState: DialogState? = null,
)