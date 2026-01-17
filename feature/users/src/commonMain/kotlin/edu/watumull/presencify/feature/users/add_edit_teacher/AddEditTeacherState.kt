package edu.watumull.presencify.feature.users.add_edit_teacher

import edu.watumull.presencify.core.design.systems.components.dialog.DialogType
import edu.watumull.presencify.core.domain.enums.Gender
import edu.watumull.presencify.core.domain.enums.TeacherRole
import edu.watumull.presencify.core.presentation.UiText

data class AddEditTeacherState(
    val viewState: ViewState = ViewState.Content,
    val dialogState: DialogState? = null,
    val teacherId: String? = null,
    val isEditMode: Boolean = false,
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,

    // Personal Details
    val firstName: String = "",
    val firstNameError: String? = null,
    val middleName: String = "",
    val middleNameError: String? = null,
    val lastName: String = "",
    val lastNameError: String? = null,
    val gender: Gender? = null,
    val genderError: String? = null,
    val isGenderDropdownOpen: Boolean = false,
    val highestQualification: String = "",
    val highestQualificationError: String? = null,
    val role: TeacherRole? = null,
    val roleError: String? = null,
    val isRoleDropdownOpen: Boolean = false,

    // Contact Details
    val email: String = "",
    val emailError: String? = null,
    val phoneNumber: String = "",
    val phoneNumberError: String? = null,

    // Image
    val teacherImageBytes: ByteArray? = null,
    val teacherImageUrl: String? = null,
    val isImageDialogVisible: Boolean = false,
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
    CONFIRM_NAVIGATION_WITH_UNSAVED_CHANGES,
}

