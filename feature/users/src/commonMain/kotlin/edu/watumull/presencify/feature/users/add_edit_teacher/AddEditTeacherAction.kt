package edu.watumull.presencify.feature.users.add_edit_teacher

sealed interface AddEditTeacherAction {
    data object BackButtonClick : AddEditTeacherAction
    data object DismissDialog : AddEditTeacherAction

    // Personal Details
    data class UpdateFirstName(val firstName: String) : AddEditTeacherAction
    data class UpdateMiddleName(val middleName: String) : AddEditTeacherAction
    data class UpdateLastName(val lastName: String) : AddEditTeacherAction
    data class UpdateGender(val gender: edu.watumull.presencify.core.domain.enums.Gender) : AddEditTeacherAction
    data class UpdateHighestQualification(val qualification: String) : AddEditTeacherAction
    data class UpdateRole(val role: edu.watumull.presencify.core.domain.enums.TeacherRole) : AddEditTeacherAction
    data class ChangeGenderDropDownVisibility(val isVisible: Boolean) : AddEditTeacherAction
    data class ChangeRoleDropDownVisibility(val isVisible: Boolean) : AddEditTeacherAction

    // Contact Details
    data class UpdateEmail(val email: String) : AddEditTeacherAction
    data class UpdatePhoneNumber(val phoneNumber: String) : AddEditTeacherAction

    // Image
    data class UpdateTeacherImage(val imageBytes: ByteArray?) : AddEditTeacherAction
    data object ToggleImageDialog : AddEditTeacherAction

    // Navigation
    data object SubmitClick : AddEditTeacherAction
}

