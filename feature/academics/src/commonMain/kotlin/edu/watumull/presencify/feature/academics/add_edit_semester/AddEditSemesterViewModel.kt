package edu.watumull.presencify.feature.academics.add_edit_semester

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import edu.watumull.presencify.core.designsystem.components.dialog.DialogType
import edu.watumull.presencify.core.domain.enums.SemesterNumber
import edu.watumull.presencify.core.domain.onError
import edu.watumull.presencify.core.domain.onSuccess
import edu.watumull.presencify.core.domain.repository.academics.BranchRepository
import edu.watumull.presencify.core.domain.repository.academics.SchemeRepository
import edu.watumull.presencify.core.domain.repository.academics.SemesterRepository
import edu.watumull.presencify.core.presentation.UiText
import edu.watumull.presencify.core.presentation.components.dialog.DialogState
import edu.watumull.presencify.core.presentation.global_snackbar.SnackbarController
import edu.watumull.presencify.core.presentation.global_snackbar.SnackbarEvent
import edu.watumull.presencify.core.presentation.toUiText
import edu.watumull.presencify.core.presentation.utils.BaseViewModel
import edu.watumull.presencify.core.presentation.validation.validateAsAcademicEndYear
import edu.watumull.presencify.core.presentation.validation.validateAsAcademicStartYear
import edu.watumull.presencify.core.presentation.validation.validateAsEndDate
import edu.watumull.presencify.core.presentation.validation.validateAsSemesterNumber
import edu.watumull.presencify.core.presentation.validation.validateAsStartDate
import edu.watumull.presencify.core.presentation.validation.validateAsUUID
import edu.watumull.presencify.feature.academics.navigation.AcademicsRoutes
import kotlinx.coroutines.launch

class AddEditSemesterViewModel(
    private val semesterRepository: SemesterRepository,
    private val branchRepository: BranchRepository,
    private val schemeRepository: SchemeRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<AddEditSemesterState, AddEditSemesterEvent, AddEditSemesterAction>(
    initialState = AddEditSemesterState(
        semesterId = savedStateHandle.toRoute<AcademicsRoutes.AddEditSemester>().semesterId,
        isEditMode = savedStateHandle.toRoute<AcademicsRoutes.AddEditSemester>().semesterId != null
    )
) {

    init {
        viewModelScope.launch {
            loadBranches()
            loadSchemes()
            state.semesterId?.let { id -> loadSemester(id) }
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
                        dialogState = DialogState(
                            dialogType = DialogType.ERROR,
                            title = UiText.DynamicString("Error Loading Branches"),
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
                        dialogState = DialogState(
                            dialogType = DialogType.ERROR,
                            title = UiText.DynamicString("Error Loading Schemes"),
                            message = error.toUiText()
                        )
                    )
                }
            }
    }

    private suspend fun loadSemester(id: String) {
        updateState { it.copy(isLoading = true) }

        semesterRepository.getSemesterById(id)
            .onSuccess { semester ->
                updateState {
                    it.copy(
                        isLoading = false,
                        semesterNumber = semester.semesterNumber,
                        academicStartYear = semester.academicStartYear.toString(),
                        academicEndYear = semester.academicEndYear.toString(),
                        startDate = semester.startDate,
                        endDate = semester.endDate,
                        selectedBranchId = semester.branchId,
                        selectedSchemeId = semester.schemeId
                    )
                }

            }
            .onError { error ->
                updateState {
                    it.copy(
                        isLoading = false,
                        dialogState = DialogState(
                            dialogType = DialogType.ERROR,
                            title = UiText.DynamicString("Error Loading Semester"),
                            message = error.toUiText()
                        )
                    )
                }
            }
    }

    override fun handleAction(action: AddEditSemesterAction) {
        when (action) {
            is AddEditSemesterAction.NavigateBack -> handleBackNavigation()
            is AddEditSemesterAction.ConfirmNavigateBack -> confirmNavigateBack()
            is AddEditSemesterAction.DismissDialog -> updateState { it.copy(dialogState = null) }
            is AddEditSemesterAction.UpdateSemesterNumber -> {
                updateState {
                    it.copy(
                        semesterNumber = action.semesterNumber,
                        semesterNumberError = null,
                        isSemesterNumberDropdownOpen = false
                    )
                }
            }

            is AddEditSemesterAction.UpdateAcademicStartYear -> updateState {
                it.copy(
                    academicStartYear = action.year,
                    academicStartYearError = null
                )
            }

            is AddEditSemesterAction.UpdateAcademicEndYear -> updateState {
                it.copy(
                    academicEndYear = action.year,
                    academicEndYearError = null
                )
            }

            is AddEditSemesterAction.UpdateStartDate -> updateState {
                it.copy(
                    startDate = action.date,
                    startDateError = null,
                    isStartDatePickerVisible = false
                )
            }

            is AddEditSemesterAction.UpdateEndDate -> updateState {
                it.copy(
                    endDate = action.date,
                    endDateError = null,
                    isEndDatePickerVisible = false
                )
            }

            is AddEditSemesterAction.UpdateSelectedBranch -> {
                updateState {
                    it.copy(
                        selectedBranchId = action.branchId,
                        branchError = null,
                        isBranchDropdownOpen = false
                    )
                }
            }

            is AddEditSemesterAction.UpdateSelectedScheme -> {
                updateState {
                    it.copy(
                        selectedSchemeId = action.schemeId,
                        schemeError = null,
                        isSchemeDropdownOpen = false
                    )
                }
            }

            is AddEditSemesterAction.ChangeSemesterNumberDropDownVisibility -> updateState {
                it.copy(
                    isSemesterNumberDropdownOpen = action.isVisible
                )
            }

            is AddEditSemesterAction.ChangeBranchDropDownVisibility -> updateState { it.copy(isBranchDropdownOpen = action.isVisible) }
            is AddEditSemesterAction.ChangeSchemeDropDownVisibility -> updateState { it.copy(isSchemeDropdownOpen = action.isVisible) }
            is AddEditSemesterAction.ChangeStartDatePickerVisibility -> updateState { it.copy(isStartDatePickerVisible = action.isVisible) }
            is AddEditSemesterAction.ChangeEndDatePickerVisibility -> updateState { it.copy(isEndDatePickerVisible = action.isVisible) }
            is AddEditSemesterAction.SubmitClick -> {
                viewModelScope.launch { submitForm() }
            }
        }
    }

    private fun handleBackNavigation() {
        if (hasUnsavedChanges()) {
            updateState {
                it.copy(
                    dialogState = DialogState(
                        dialogType = DialogType.CONFIRM_NORMAL_ACTION,
                        title = UiText.DynamicString("Unsaved Changes"),
                        message = UiText.DynamicString("You have unsaved changes. Are you sure you want to leave?")
                    )
                )
            }
        } else {
            sendEvent(AddEditSemesterEvent.NavigateBack)
        }
    }

    private fun confirmNavigateBack() {
        updateState { it.copy(dialogState = null) }
        sendEvent(AddEditSemesterEvent.NavigateBack)
    }

    private fun hasUnsavedChanges(): Boolean {
        return state.semesterNumber != null || state.academicStartYear.isNotBlank() || state.academicEndYear.isNotBlank() || state.startDate != null || state.endDate != null
    }

    private fun validateForm(): Boolean {
        val semesterValidation = state.semesterNumber.validateAsSemesterNumber()
        val startYearValidation = state.academicStartYear.validateAsAcademicStartYear(endYear = state.academicEndYear)
        val endYearValidation = state.academicEndYear.validateAsAcademicEndYear(startYear = state.academicStartYear)

        val startDateValidation = state.startDate.validateAsStartDate(endDate = state.endDate)
        val endDateValidation = state.endDate.validateAsEndDate(startDate = state.startDate)

        val branchValidation = state.selectedBranchId.validateAsUUID()
        val schemeValidation = state.selectedSchemeId.validateAsUUID()

        updateState {
            it.copy(
                semesterNumberError = semesterValidation.errorMessage,
                academicStartYearError = startYearValidation.errorMessage,
                academicEndYearError = endYearValidation.errorMessage,
                startDateError = startDateValidation.errorMessage,
                endDateError = endDateValidation.errorMessage,
                branchError = branchValidation.errorMessage,
                schemeError = schemeValidation.errorMessage
            )
        }

        return semesterValidation.successful &&
            startYearValidation.successful &&
            endYearValidation.successful &&
            startDateValidation.successful &&
            endDateValidation.successful &&
            branchValidation.successful &&
            schemeValidation.successful
    }

    private suspend fun submitForm() {
        if (!validateForm()) return

        updateState { it.copy(isSubmitting = true) }

        val startYear = state.academicStartYear.toIntOrNull() ?: return
        val endYear = state.academicEndYear.toIntOrNull() ?: return
        val semNum = state.semesterNumber ?: return
        val startDt = state.startDate ?: return
        val endDt = state.endDate ?: return

        val result = if (state.isEditMode && state.semesterId != null) {
            val id = state.semesterId!!
            semesterRepository.updateSemester(
                id = id,
                startDate = startDt,
                endDate = endDt
            )
        } else {
            semesterRepository.addSemester(
                branchId = state.selectedBranchId,
                semesterNumber = semNum,
                academicStartYear = startYear,
                academicEndYear = endYear,
                startDate = startDt,
                endDate = endDt,
                schemeId = state.selectedSchemeId
            )
        }

        result
            .onSuccess {
                updateState { it.copy(isSubmitting = false) }
                viewModelScope.launch {
                    SnackbarController.sendEvent(SnackbarEvent(message = if (state.isEditMode) "Semester updated successfully" else "Semester added successfully"))
                }
                sendEvent(AddEditSemesterEvent.NavigateBack)
            }
            .onError { error ->
                updateState {
                    it.copy(
                        isSubmitting = false,
                        dialogState = DialogState(
                            dialogType = DialogType.ERROR,
                            title = UiText.DynamicString(if (state.isEditMode) "Error Updating Semester" else "Error Adding Semester"),
                            message = error.toUiText()
                        )
                    )
                }
            }
    }
}
