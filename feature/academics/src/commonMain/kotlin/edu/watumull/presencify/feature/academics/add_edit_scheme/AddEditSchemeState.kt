package edu.watumull.presencify.feature.academics.add_edit_scheme

import edu.watumull.presencify.core.domain.model.academics.University
import edu.watumull.presencify.core.presentation.components.dialog.DialogState

data class AddEditSchemeState(
    val schemeId: String? = null,
    val isEditMode: Boolean = false,
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,

    val name: String = "",
    val selectedUniversityId: String = "",
    val universityOptions: List<University> = emptyList(),

    val nameError: String? = null,
    val universityError: String? = null,

    val isUniversityDropdownOpen: Boolean = false,

    val dialogState: DialogState? = null,
)
