package edu.watumull.presencify.feature.academics.add_edit_course

import edu.watumull.presencify.core.domain.model.academics.Scheme
import edu.watumull.presencify.core.presentation.components.dialog.DialogState

data class AddEditCourseState(
    val courseId: String? = null,
    val isEditMode: Boolean = false,
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,

    val code: String = "",
    val name: String = "",
    val optionalCourse: String = "",
    val selectedSchemeId: String = "",
    val schemeOptions: List<Scheme> = emptyList(),

    val codeError: String? = null,
    val nameError: String? = null,
    val schemeError: String? = null,

    val isSchemeDropdownOpen: Boolean = false,

    val dialogState: DialogState? = null,
)
