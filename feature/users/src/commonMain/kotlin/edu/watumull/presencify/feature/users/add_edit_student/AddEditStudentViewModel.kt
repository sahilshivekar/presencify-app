package edu.watumull.presencify.feature.users.add_edit_student

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import edu.watumull.presencify.core.design.systems.components.dialog.DialogType
import edu.watumull.presencify.core.domain.onError
import edu.watumull.presencify.core.domain.onSuccess
import edu.watumull.presencify.core.domain.repository.academics.BranchRepository
import edu.watumull.presencify.core.domain.repository.academics.SchemeRepository
import edu.watumull.presencify.core.domain.repository.student.StudentRepository
import edu.watumull.presencify.core.presentation.global_snackbar.SnackbarController
import edu.watumull.presencify.core.presentation.global_snackbar.SnackbarEvent
import edu.watumull.presencify.core.presentation.toUiText
import edu.watumull.presencify.core.presentation.utils.BaseViewModel
import edu.watumull.presencify.core.presentation.utils.DateTimeUtils
import edu.watumull.presencify.core.presentation.validation.*
import edu.watumull.presencify.feature.users.navigation.UsersRoutes
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class AddEditStudentViewModel(
    private val studentRepository: StudentRepository,
    private val branchRepository: BranchRepository,
    private val schemeRepository: SchemeRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<AddEditStudentState, AddEditStudentEvent, AddEditStudentAction>(
    initialState = AddEditStudentState(
        studentId = savedStateHandle.toRoute<UsersRoutes.AddEditStudent>().studentId,
        isEditMode = savedStateHandle.toRoute<UsersRoutes.AddEditStudent>().studentId != null
    )
) {

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            loadBranches()
            loadSchemes()
            loadAdmissionYearOptions()

            // If in edit mode, load student details
            state.studentId?.let { studentId ->
                loadStudentDetails(studentId)
            }
        }
    }

    private suspend fun loadBranches() {
        branchRepository.getBranches()
            .onSuccess { branches ->
                updateState { it.copy(branchOptions = branches) }
            }
            .onError { error ->
                updateState {
                    it.copy(
                        dialogState = AddEditStudentState.DialogState(
                            dialogType = DialogType.ERROR,
                            dialogIntention = DialogIntention.GENERIC,
                            title = "Error Loading Branches",
                            message = error.toUiText()
                        )
                    )
                }
            }
    }

    private suspend fun loadSchemes() {
        schemeRepository.getSchemes()
            .onSuccess { schemes ->
                updateState { it.copy(schemeOptions = schemes) }
            }
            .onError { error ->
                updateState {
                    it.copy(
                        dialogState = AddEditStudentState.DialogState(
                            dialogType = DialogType.ERROR,
                            dialogIntention = DialogIntention.GENERIC,
                            title = "Error Loading Schemes",
                            message = error.toUiText()
                        )
                    )
                }
            }
    }

    private fun loadAdmissionYearOptions() {
        val currentYear = DateTimeUtils.getCurrentDate().year
        val years = (currentYear downTo currentYear - 10).toList()
        updateState { it.copy(admissionYearOptions = years) }
    }

    private suspend fun loadStudentDetails(studentId: String) {
        updateState { it.copy(isLoading = true) }

        studentRepository.getStudentById(studentId)
            .onSuccess { student ->
                updateState {
                    it.copy(
                        isLoading = false,
                        prn = student.prn,
                        firstName = student.firstName,
                        middleName = student.middleName ?: "",
                        lastName = student.lastName,
                        gender = student.gender,
                        dob = student.dob?.let { dobString ->
                            try { LocalDate.parse(dobString) } catch (_: Exception) { null }
                        },
                        email = student.email,
                        parentEmail = student.parentEmail ?: "",
                        phoneNumber = student.phoneNumber,
                        admissionYear = student.admissionYear,
                        admissionType = student.admissionType,
                        selectedBranchId = student.branchId,
                        selectedSchemeId = student.schemeId,
                        studentImageUrl = student.studentImageUrl
                    )
                }
            }
            .onError { error ->
                updateState {
                    it.copy(
                        isLoading = false,
                        dialogState = AddEditStudentState.DialogState(
                            dialogType = DialogType.ERROR,
                            dialogIntention = DialogIntention.GENERIC,
                            title = "Error Loading Student",
                            message = error.toUiText()
                        )
                    )
                }
            }
    }

    override fun handleAction(action: AddEditStudentAction) {
        when (action) {
            is AddEditStudentAction.BackButtonClick -> handleBackNavigation()
            is AddEditStudentAction.DismissDialog -> updateState { it.copy(dialogState = null) }

            // Personal Details
            is AddEditStudentAction.UpdatePrn -> {
                val validation = action.prn.validateAsPrn()
                updateState {
                    it.copy(prn = action.prn, prnError = if (validation.successful) null else validation.errorMessage)
                }
            }
            is AddEditStudentAction.UpdateFirstName -> {
                val validation = action.firstName.validateAsFirstName()
                updateState {
                    it.copy(firstName = action.firstName, firstNameError = if (validation.successful) null else validation.errorMessage)
                }
            }
            is AddEditStudentAction.UpdateMiddleName -> {
                val validation = action.middleName.validateAsMiddleName()
                updateState {
                    it.copy(middleName = action.middleName, middleNameError = if (validation.successful) null else validation.errorMessage)
                }
            }
            is AddEditStudentAction.UpdateLastName -> {
                val validation = action.lastName.validateAsLastName()
                updateState {
                    it.copy(lastName = action.lastName, lastNameError = if (validation.successful) null else validation.errorMessage)
                }
            }
            is AddEditStudentAction.UpdateGender -> updateState {
                it.copy(gender = action.gender, genderError = null, isGenderDropdownOpen = false)
            }
            is AddEditStudentAction.UpdateDob -> {
                val validation = action.dob.validateAsDob()
                updateState {
                    it.copy(
                        dob = action.dob,
                        dobError = if (validation.successful) null else validation.errorMessage,
                        isDatePickerVisible = false
                    )
                }
            }
            is AddEditStudentAction.ChangeGenderDropDownVisibility -> updateState {
                it.copy(isGenderDropdownOpen = action.isVisible)
            }
            is AddEditStudentAction.ChangeDatePickerVisibility -> updateState {
                it.copy(isDatePickerVisible = action.isVisible)
            }

            // Contact Details
            is AddEditStudentAction.UpdateEmail -> {
                val validation = action.email.validateAsEmail()
                updateState {
                    it.copy(email = action.email, emailError = if (validation.successful) null else validation.errorMessage)
                }
            }
            is AddEditStudentAction.UpdateParentEmail -> {
                val validation = if (action.parentEmail.isNotBlank()) action.parentEmail.validateAsEmail() else null
                updateState {
                    it.copy(
                        parentEmail = action.parentEmail,
                        parentEmailError = if (validation == null || validation.successful) null else validation.errorMessage
                    )
                }
            }
            is AddEditStudentAction.UpdatePhoneNumber -> {
                val validation = action.phoneNumber.validateAsPhoneNumber()
                updateState {
                    it.copy(phoneNumber = action.phoneNumber, phoneNumberError = if (validation.successful) null else validation.errorMessage)
                }
            }

            // Academic Details
            is AddEditStudentAction.UpdateAdmissionYear -> updateState {
                it.copy(admissionYear = action.year, admissionYearError = null, isAdmissionYearDropdownOpen = false)
            }
            is AddEditStudentAction.UpdateAdmissionType -> updateState {
                it.copy(admissionType = action.type, admissionTypeError = null, isAdmissionTypeDropdownOpen = false)
            }
            is AddEditStudentAction.UpdateBranch -> updateState {
                it.copy(selectedBranchId = action.branchId, branchError = null, isBranchDropdownOpen = false)
            }
            is AddEditStudentAction.UpdateScheme -> updateState {
                it.copy(selectedSchemeId = action.schemeId, schemeError = null, isSchemeDropdownOpen = false)
            }
            is AddEditStudentAction.ChangeAdmissionYearDropDownVisibility -> updateState {
                it.copy(isAdmissionYearDropdownOpen = action.isVisible)
            }
            is AddEditStudentAction.ChangeAdmissionTypeDropDownVisibility -> updateState {
                it.copy(isAdmissionTypeDropdownOpen = action.isVisible)
            }
            is AddEditStudentAction.ChangeBranchDropDownVisibility -> updateState {
                it.copy(isBranchDropdownOpen = action.isVisible)
            }
            is AddEditStudentAction.ChangeSchemeDropDownVisibility -> updateState {
                it.copy(isSchemeDropdownOpen = action.isVisible)
            }

            // Image
            is AddEditStudentAction.UpdateStudentImage -> updateState {
                it.copy(studentImageBytes = action.imageBytes)
            }
            is AddEditStudentAction.ToggleImageDialog -> updateState {
                it.copy(isImageDialogVisible = !it.isImageDialogVisible)
            }

            // Navigation
            is AddEditStudentAction.ValidateAndNext -> validateAndNext()
            is AddEditStudentAction.BackStep -> backStep()
            is AddEditStudentAction.SubmitClick -> {
                viewModelScope.launch {
                    submitForm()
                }
            }
        }
    }

    private fun validateAndNext() {
        val currentStep = state.currentStep
        val isValid = when (currentStep) {
            StudentFormStep.PERSONAL_DETAILS -> validatePersonalDetails()
            StudentFormStep.CONTACT_DETAILS -> validateContactDetails()
            StudentFormStep.ACADEMIC_DETAILS -> validateAcademicDetails()
        }

        if (isValid) {
            when (currentStep) {
                StudentFormStep.PERSONAL_DETAILS -> updateState { it.copy(currentStep = StudentFormStep.CONTACT_DETAILS) }
                StudentFormStep.CONTACT_DETAILS -> updateState { it.copy(currentStep = StudentFormStep.ACADEMIC_DETAILS) }
                StudentFormStep.ACADEMIC_DETAILS -> {
                    viewModelScope.launch {
                        submitForm()
                    }
                }
            }
        }
    }

    private fun backStep() {
        when (state.currentStep) {
            StudentFormStep.CONTACT_DETAILS -> updateState { it.copy(currentStep = StudentFormStep.PERSONAL_DETAILS) }
            StudentFormStep.ACADEMIC_DETAILS -> updateState { it.copy(currentStep = StudentFormStep.CONTACT_DETAILS) }
            else -> {
                // Already on first step, handle back navigation
                handleBackNavigation()
            }
        }
    }

    private fun handleBackNavigation() {
        if (hasUnsavedChanges()) {
            updateState {
                it.copy(
                    dialogState = AddEditStudentState.DialogState(
                        dialogType = DialogType.CONFIRM_NORMAL_ACTION,
                        dialogIntention = DialogIntention.CONFIRM_NAVIGATION_WITH_UNSAVED_CHANGES,
                        title = "Unsaved Changes",
                        message = edu.watumull.presencify.core.presentation.UiText.DynamicString(
                            "You have unsaved changes. Are you sure you want to leave?"
                        )
                    )
                )
            }
        } else {
            sendEvent(AddEditStudentEvent.NavigateBack)
        }
    }

    fun confirmNavigateBack() {
        updateState { it.copy(dialogState = null) }
        sendEvent(AddEditStudentEvent.NavigateBack)
    }

    private fun hasUnsavedChanges(): Boolean {
        return state.firstName.isNotBlank() ||
                state.lastName.isNotBlank() ||
                state.email.isNotBlank() ||
                state.phoneNumber.isNotBlank() ||
                state.prn.isNotBlank()
    }

    private fun validatePersonalDetails(): Boolean {
        val firstNameValidation = state.firstName.validateAsFirstName()
        val firstNameError = if (firstNameValidation.successful) null else firstNameValidation.errorMessage

        val lastNameValidation = state.lastName.validateAsLastName()
        val lastNameError = if (lastNameValidation.successful) null else lastNameValidation.errorMessage

        val genderError = if (state.gender == null) "Gender is required" else null

        updateState {
            it.copy(
                firstNameError = firstNameError,
                lastNameError = lastNameError,
                genderError = genderError
            )
        }

        return firstNameError == null && lastNameError == null && genderError == null
    }

    private fun validateContactDetails(): Boolean {
        val emailValidation = state.email.validateAsEmail()
        val emailError = if (emailValidation.successful) null else emailValidation.errorMessage

        val phoneValidation = state.phoneNumber.validateAsPhoneNumber()
        val phoneNumberError = if (phoneValidation.successful) null else phoneValidation.errorMessage

        val parentEmailValidation = if (state.parentEmail.isNotBlank()) state.parentEmail.validateAsEmail() else null
        val parentEmailError = if (parentEmailValidation == null || parentEmailValidation.successful) null else parentEmailValidation.errorMessage

        updateState {
            it.copy(
                emailError = emailError,
                phoneNumberError = phoneNumberError,
                parentEmailError = parentEmailError
            )
        }

        return emailError == null && phoneNumberError == null && parentEmailError == null
    }

    private fun validateAcademicDetails(): Boolean {
        val prnValidation = state.prn.validateAsPrn()
        val prnError = if (prnValidation.successful) null else prnValidation.errorMessage

        val admissionYearError = if (state.admissionYear == null) "Admission year is required" else null
        val admissionTypeError = if (state.admissionType == null) "Admission type is required" else null
        val branchError = if (state.selectedBranchId.isBlank()) "Branch is required" else null
        val schemeError = if (state.selectedSchemeId.isBlank()) "Scheme is required" else null

        updateState {
            it.copy(
                prnError = prnError,
                admissionYearError = admissionYearError,
                admissionTypeError = admissionTypeError,
                branchError = branchError,
                schemeError = schemeError
            )
        }

        return prnError == null && admissionYearError == null && admissionTypeError == null &&
                branchError == null && schemeError == null
    }

    private suspend fun submitForm() {
        // Validate all sections
        if (!validatePersonalDetails() || !validateContactDetails() || !validateAcademicDetails()) {
            return
        }

        updateState { it.copy(isSubmitting = true) }

        val result = if (state.isEditMode && state.studentId != null) {
            updateStudent()
        } else {
            addStudent()
        }

        result
            .onSuccess {
                updateState { it.copy(isSubmitting = false) }
                viewModelScope.launch {
                    SnackbarController.sendEvent(
                        SnackbarEvent(
                            message = if (state.isEditMode) "Student updated successfully" else "Student added successfully"
                        )
                    )
                }
                sendEvent(AddEditStudentEvent.NavigateBack)
            }
            .onError { error ->
                updateState {
                    it.copy(
                        isSubmitting = false,
                        dialogState = AddEditStudentState.DialogState(
                            dialogType = DialogType.ERROR,
                            dialogIntention = DialogIntention.GENERIC,
                            title = if (state.isEditMode) "Error Updating Student" else "Error Adding Student",
                            message = error.toUiText()
                        )
                    )
                }
            }
    }

    private suspend fun addStudent() = studentRepository.addStudent(
        prn = state.prn,
        firstName = state.firstName,
        middleName = state.middleName.ifBlank { null },
        lastName = state.lastName,
        email = state.email,
        phoneNumber = state.phoneNumber,
        gender = state.gender!!,
        dob = state.dob?.toString(),
        schemeId = state.selectedSchemeId,
        admissionYear = state.admissionYear!!,
        admissionType = state.admissionType!!,
        branchId = state.selectedBranchId,
        parentEmail = state.parentEmail.ifBlank { null },
        studentImage = state.studentImageBytes
    )

    private suspend fun updateStudent() = studentRepository.updateStudentDetails(
        id = state.studentId!!,
        firstName = state.firstName,
        middleName = state.middleName.ifBlank { null },
        lastName = state.lastName,
        email = state.email,
        gender = state.gender,
        phoneNumber = state.phoneNumber,
        dob = state.dob?.toString(),
        schemeId = state.selectedSchemeId,
        branchId = state.selectedBranchId,
        parentEmail = state.parentEmail.ifBlank { null },
        prn = state.prn,
        admissionYear = state.admissionYear,
        admissionType = state.admissionType
    )
}

