package edu.watumull.presencify.feature.users.add_edit_student

sealed interface AddEditStudentAction {
    data object BackButtonClick : AddEditStudentAction
    data object DismissDialog : AddEditStudentAction

    // Personal Details
    data class UpdatePrn(val prn: String) : AddEditStudentAction
    data class UpdateFirstName(val firstName: String) : AddEditStudentAction
    data class UpdateMiddleName(val middleName: String) : AddEditStudentAction
    data class UpdateLastName(val lastName: String) : AddEditStudentAction
    data class UpdateGender(val gender: edu.watumull.presencify.core.domain.enums.Gender) : AddEditStudentAction
    data class UpdateDob(val dob: kotlinx.datetime.LocalDate) : AddEditStudentAction
    data class ChangeGenderDropDownVisibility(val isVisible: Boolean) : AddEditStudentAction
    data class ChangeDatePickerVisibility(val isVisible: Boolean) : AddEditStudentAction

    // Contact Details
    data class UpdateEmail(val email: String) : AddEditStudentAction
    data class UpdateParentEmail(val parentEmail: String) : AddEditStudentAction
    data class UpdatePhoneNumber(val phoneNumber: String) : AddEditStudentAction

    // Academic Details
    data class UpdateAdmissionYear(val year: Int) : AddEditStudentAction
    data class UpdateAdmissionType(val type: edu.watumull.presencify.core.domain.enums.AdmissionType) : AddEditStudentAction
    data class UpdateBranch(val branchId: String) : AddEditStudentAction
    data class UpdateScheme(val schemeId: String) : AddEditStudentAction
    data class ChangeAdmissionYearDropDownVisibility(val isVisible: Boolean) : AddEditStudentAction
    data class ChangeAdmissionTypeDropDownVisibility(val isVisible: Boolean) : AddEditStudentAction
    data class ChangeBranchDropDownVisibility(val isVisible: Boolean) : AddEditStudentAction
    data class ChangeSchemeDropDownVisibility(val isVisible: Boolean) : AddEditStudentAction

    // Image
    data class UpdateStudentImage(val imageBytes: ByteArray?) : AddEditStudentAction
    data object ToggleImageDialog : AddEditStudentAction

    // Navigation
    data object ValidateAndNext : AddEditStudentAction
    data object BackStep : AddEditStudentAction
    data object SubmitClick : AddEditStudentAction
}

