package edu.watumull.presencify.feature.academics.add_edit_batch

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import edu.watumull.presencify.core.designsystem.components.dialog.DialogType
import edu.watumull.presencify.core.domain.onError
import edu.watumull.presencify.core.domain.onSuccess
import edu.watumull.presencify.core.domain.repository.academics.BatchRepository
import edu.watumull.presencify.core.domain.repository.academics.BranchRepository
import edu.watumull.presencify.core.domain.repository.academics.DivisionRepository
import edu.watumull.presencify.core.domain.repository.academics.SemesterRepository
import edu.watumull.presencify.core.presentation.global_snackbar.SnackbarController
import edu.watumull.presencify.core.presentation.global_snackbar.SnackbarEvent
import edu.watumull.presencify.core.presentation.toUiText
import edu.watumull.presencify.core.presentation.utils.BaseViewModel
import edu.watumull.presencify.core.presentation.validation.ValidationResult
import edu.watumull.presencify.core.presentation.validation.validateAsAcademicEndYear
import edu.watumull.presencify.core.presentation.validation.validateAsAcademicStartYear
import edu.watumull.presencify.core.presentation.validation.validateAsBatchCode
import edu.watumull.presencify.core.presentation.validation.validateAsSemesterNumber
import edu.watumull.presencify.core.presentation.validation.validateAsUUID
import edu.watumull.presencify.feature.academics.navigation.AcademicsRoutes
import kotlinx.coroutines.launch

class AddEditBatchViewModel(
    private val batchRepository: BatchRepository,
    private val divisionRepository: DivisionRepository,
    private val branchRepository: BranchRepository,
    private val semesterRepository: SemesterRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<AddEditBatchState, AddEditBatchEvent, AddEditBatchAction>(
    initialState = AddEditBatchState(
        batchId = savedStateHandle.toRoute<AcademicsRoutes.AddEditBatch>().batchId,
        isEditMode = savedStateHandle.toRoute<AcademicsRoutes.AddEditBatch>().batchId != null
    )
) {

    init {
        if (state.isEditMode) {
            viewModelScope.launch {
                loadBatchDetails()
            }
        } else {
            viewModelScope.launch {
                loadInitialData()
                loadBranches()
            }
        }
    }

    private suspend fun loadBatchDetails() {
        val batchId = state.batchId ?: return

        batchRepository.getBatchById(batchId)
            .onSuccess { batch ->
                val divisionId = batch.divisionId

                divisionRepository.getDivisionById(divisionId)
                    .onSuccess { division ->
                        val semesterId = division.semesterId

                        semesterRepository.getSemesterById(semesterId)
                            .onSuccess { semester ->
                                updateState {
                                    it.copy(
                                        foundDivision = division,
                                        foundSemester = semester,
                                        batchCode = batch.batchCode,
                                        showBatchInput = true
                                    )
                                }
                            }
                            .onError { error ->
                                updateState {
                                    it.copy(
                                        dialogState = AddEditBatchState.DialogState(
                                            dialogType = DialogType.ERROR,
                                            dialogIntention = DialogIntention.GENERIC,
                                            title = "Error Loading Semester",
                                            message = error.toUiText()
                                        )
                                    )
                                }
                            }
                    }
                    .onError { error ->
                        updateState {
                            it.copy(
                                dialogState = AddEditBatchState.DialogState(
                                    dialogType = DialogType.ERROR,
                                    dialogIntention = DialogIntention.GENERIC,
                                    title = "Error Loading Division",
                                    message = error.toUiText()
                                )
                            )
                        }
                    }
            }
            .onError { error ->
                updateState {
                    it.copy(
                        dialogState = AddEditBatchState.DialogState(
                            dialogType = DialogType.ERROR,
                            dialogIntention = DialogIntention.GENERIC,
                            title = "Error Loading Batch",
                            message = error.toUiText()
                        )
                    )
                }
            }
    }

    private suspend fun loadInitialData() {
        state.batchId?.let { batchId ->
            batchRepository.getBatchById(batchId)
                .onSuccess { batch ->
                    val divisionId = batch.divisionId
                    divisionRepository.getDivisionById(divisionId)
                        .onSuccess { division ->
                            semesterRepository.getSemesterById(division.semesterId)
                                .onSuccess { semester ->
                                    updateState {
                                        it.copy(
                                            foundDivision = division,
                                            foundSemester = semester,
                                            batchCode = batch.batchCode,
                                            semesterNumber = semester.semesterNumber,
                                            academicStartYear = semester.academicStartYear.toString(),
                                            academicEndYear = semester.academicEndYear.toString()
                                        )
                                    }
                                }
                        }
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
                        dialogState = AddEditBatchState.DialogState(
                            dialogType = DialogType.ERROR,
                            dialogIntention = DialogIntention.GENERIC,
                            title = "Error Loading Branches",
                            message = error.toUiText()
                        )
                    )
                }
            }
    }

    override fun handleAction(action: AddEditBatchAction) {
        when (action) {
            is AddEditBatchAction.BackButtonClick -> handleBackNavigation()
            is AddEditBatchAction.DismissDialog -> updateState { it.copy(dialogState = null) }
            is AddEditBatchAction.UpdateSemesterNumber -> updateState {
                it.copy(
                    semesterNumber = action.semesterNumber,
                    semesterNumberError = null,
                    isSemesterNumberDropdownOpen = false
                )
            }

            is AddEditBatchAction.UpdateAcademicStartYear -> updateState {
                it.copy(
                    academicStartYear = action.year,
                    academicStartYearError = null
                )
            }

            is AddEditBatchAction.UpdateAcademicEndYear -> updateState {
                it.copy(
                    academicEndYear = action.year,
                    academicEndYearError = null
                )
            }

            is AddEditBatchAction.UpdateSelectedBranch -> updateState {
                it.copy(
                    selectedBranchId = action.branchId,
                    branchError = null,
                    isBranchDropdownOpen = false
                )
            }

            is AddEditBatchAction.UpdateSelectedDivision -> updateState {
                it.copy(
                    selectedDivisionId = action.divisionId,
                    divisionError = null,
                    isDivisionDropdownOpen = false,
                    showBatchInput = true
                )
            }

            is AddEditBatchAction.UpdateBatchCode -> updateState {
                it.copy(
                    batchCode = action.code,
                    batchCodeError = null
                )
            }

            is AddEditBatchAction.ChangeSemesterNumberDropDownVisibility -> updateState {
                it.copy(
                    isSemesterNumberDropdownOpen = action.isVisible
                )
            }

            is AddEditBatchAction.ChangeBranchDropDownVisibility -> updateState { it.copy(isBranchDropdownOpen = action.isVisible) }
            is AddEditBatchAction.ChangeDivisionDropDownVisibility -> updateState { it.copy(isDivisionDropdownOpen = action.isVisible) }
            is AddEditBatchAction.FindDivisionsClick -> {
                viewModelScope.launch { findDivisions() }
            }

            is AddEditBatchAction.SubmitClick -> {
                viewModelScope.launch { submitForm() }
            }
        }
    }

    private fun handleBackNavigation() {
        if (hasUnsavedChanges()) {
            updateState {
                it.copy(
                    dialogState = AddEditBatchState.DialogState(
                        dialogType = DialogType.CONFIRM_NORMAL_ACTION,
                        dialogIntention = DialogIntention.CONFIRM_NAVIGATION_WITH_UNSAVED_CHANGES,
                        title = "Unsaved Changes",
                        message = edu.watumull.presencify.core.presentation.UiText.DynamicString("You have unsaved changes. Are you sure you want to leave?")
                    )
                )
            }
        } else {
            sendEvent(AddEditBatchEvent.NavigateBack)
        }
    }

    fun confirmNavigateBack() {
        updateState { it.copy(dialogState = null) }
        sendEvent(AddEditBatchEvent.NavigateBack)
    }

    private fun hasUnsavedChanges(): Boolean {
        return state.batchCode.isNotBlank() || state.foundDivisions.isNotEmpty()
    }

    private suspend fun findDivisions() {
        if (!validateSemesterInputs()) return

        updateState { it.copy(isLoading = true) }

        val startYear = state.academicStartYear.toIntOrNull() ?: return
        val endYear = state.academicEndYear.toIntOrNull() ?: return
        val semNum = state.semesterNumber ?: return

        divisionRepository.getDivisions(
            semesterNumber = semNum,
            academicStartYear = startYear,
            academicEndYear = endYear,
            branchId = state.selectedBranchId,
            getAll = true
        )
            .onSuccess { result ->
                val divisions = result.divisions
                if (divisions.isNotEmpty()) {
                    // Use the first matching division as default
                    updateState {
                        it.copy(
                            isLoading = false,
                            foundDivisions = divisions,
                            showDivisionInput = true,
                            divisionError = null
                        )
                    }
                } else {
                    updateState {
                        it.copy(
                            isLoading = false,
                            dialogState = AddEditBatchState.DialogState(
                                dialogType = DialogType.INFO,
                                dialogIntention = DialogIntention.GENERIC,
                                title = "No Divisions Found",
                                message = edu.watumull.presencify.core.presentation.UiText.DynamicString("No divisions found with the selected criteria. Please check your inputs.")
                            )
                        )
                    }
                }
            }
            .onError { error ->
                updateState {
                    it.copy(
                        isLoading = false,
                        dialogState = AddEditBatchState.DialogState(
                            dialogType = DialogType.ERROR,
                            dialogIntention = DialogIntention.GENERIC,
                            title = "Error Finding Divisions",
                            message = error.toUiText()
                        )
                    )
                }
            }
    }

    private fun validateSemesterInputs(): Boolean {
        val semesterValidation = state.semesterNumber.validateAsSemesterNumber()
        val startYearValidation = state.academicStartYear.validateAsAcademicStartYear(endYear = state.academicEndYear)
        val endYearValidation = state.academicEndYear.validateAsAcademicEndYear(startYear = state.academicStartYear)

        val branchValidation = state.selectedBranchId.validateAsUUID()

        updateState {
            it.copy(
                semesterNumberError = semesterValidation.errorMessage,
                academicStartYearError = startYearValidation.errorMessage,
                academicEndYearError = endYearValidation.errorMessage,
                branchError = branchValidation.errorMessage
            )
        }

        return semesterValidation.successful &&
                startYearValidation.successful &&
                endYearValidation.successful &&
                branchValidation.successful
    }

    private fun validateDivisionAndBatchCode(): Boolean {
        val divisionValidation = if (state.isEditMode) {
            ValidationResult(successful = true)
        } else {
            state.selectedDivisionId.validateAsUUID()
        }

        val batchCodeValidation = state.batchCode.validateAsBatchCode()

        updateState {
            it.copy(
                divisionError = divisionValidation.errorMessage,
                batchCodeError = batchCodeValidation.errorMessage
            )
        }

        return divisionValidation.successful && batchCodeValidation.successful
    }

    private suspend fun submitForm() {
        if (!validateDivisionAndBatchCode()) return

        updateState { it.copy(isSubmitting = true) }

        val batchId = state.batchId
        val result = if (state.isEditMode && batchId != null) {
            batchRepository.updateBatch(
                id = batchId,
                batchCode = state.batchCode
            )
        } else {
            batchRepository.addBatch(
                batchCode = state.batchCode,
                divisionId = state.selectedDivisionId
            )
        }

        result
            .onSuccess {
                updateState { it.copy(isSubmitting = false) }
                viewModelScope.launch {
                    SnackbarController.sendEvent(
                        SnackbarEvent(
                            message = if (state.isEditMode) "Batch updated successfully" else "Batch added successfully"
                        )
                    )
                }
                sendEvent(AddEditBatchEvent.NavigateBack)
            }
            .onError { error ->
                updateState {
                    it.copy(
                        isSubmitting = false,
                        dialogState = AddEditBatchState.DialogState(
                            dialogType = DialogType.ERROR,
                            dialogIntention = DialogIntention.GENERIC,
                            title = if (state.isEditMode) "Error Updating Batch" else "Error Adding Batch",
                            message = error.toUiText()
                        )
                    )
                }
            }
    }
}
