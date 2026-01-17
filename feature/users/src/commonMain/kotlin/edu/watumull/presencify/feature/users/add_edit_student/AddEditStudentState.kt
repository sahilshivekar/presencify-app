package edu.watumull.presencify.feature.users.add_edit_student

import edu.watumull.presencify.core.design.systems.components.dialog.DialogType
import edu.watumull.presencify.core.domain.enums.AdmissionType
import edu.watumull.presencify.core.domain.enums.Gender
import edu.watumull.presencify.core.domain.model.academics.Branch
import edu.watumull.presencify.core.domain.model.academics.Scheme
import edu.watumull.presencify.core.presentation.UiText
import kotlinx.datetime.LocalDate

data class AddEditStudentState(
    val dialogState: DialogState? = null,
    val studentId: String? = null,
    val isEditMode: Boolean = false,
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,

    // Current Step
    val currentStep: StudentFormStep = StudentFormStep.PERSONAL_DETAILS,

    // Personal Details
    val prn: String = "",
    val prnError: String? = null,
    val firstName: String = "",
    val firstNameError: String? = null,
    val middleName: String = "",
    val middleNameError: String? = null,
    val lastName: String = "",
    val lastNameError: String? = null,
    val gender: Gender? = null,
    val genderError: String? = null,
    val isGenderDropdownOpen: Boolean = false,
    val dob: LocalDate? = null,
    val dobError: String? = null,
    val isDatePickerVisible: Boolean = false,

    // Contact Details
    val email: String = "",
    val emailError: String? = null,
    val parentEmail: String = "",
    val parentEmailError: String? = null,
    val phoneNumber: String = "",
    val phoneNumberError: String? = null,

    // Academic Details
    val admissionYear: Int? = null,
    val admissionYearError: String? = null,
    val isAdmissionYearDropdownOpen: Boolean = false,
    val admissionYearOptions: List<Int> = emptyList(),
    val admissionType: AdmissionType? = null,
    val admissionTypeError: String? = null,
    val isAdmissionTypeDropdownOpen: Boolean = false,
    val selectedBranchId: String = "",
    val branchError: String? = null,
    val isBranchDropdownOpen: Boolean = false,
    val branchOptions: List<Branch> = emptyList(),
    val selectedSchemeId: String = "",
    val schemeError: String? = null,
    val isSchemeDropdownOpen: Boolean = false,
    val schemeOptions: List<Scheme> = emptyList(),

    // Image
    val studentImageBytes: ByteArray? = null,
    val studentImageUrl: String? = null,
    val isImageDialogVisible: Boolean = false,
) {
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

enum class StudentFormStep {
    PERSONAL_DETAILS,
    CONTACT_DETAILS,
    ACADEMIC_DETAILS
}

