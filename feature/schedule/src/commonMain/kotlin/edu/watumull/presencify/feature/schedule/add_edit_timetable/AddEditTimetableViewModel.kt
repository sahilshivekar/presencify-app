package edu.watumull.presencify.feature.schedule.add_edit_timetable

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import edu.watumull.presencify.core.designsystem.components.dialog.DialogType
import edu.watumull.presencify.core.domain.onError
import edu.watumull.presencify.core.domain.onSuccess
import edu.watumull.presencify.core.domain.repository.academics.BatchRepository
import edu.watumull.presencify.core.domain.repository.academics.BranchRepository
import edu.watumull.presencify.core.domain.repository.academics.DivisionRepository
import edu.watumull.presencify.core.domain.repository.schedule.TimetableRepository
import edu.watumull.presencify.core.presentation.UiText
import edu.watumull.presencify.core.presentation.components.dialog.DialogState
import edu.watumull.presencify.core.presentation.global_snackbar.SnackbarController
import edu.watumull.presencify.core.presentation.global_snackbar.SnackbarEvent
import edu.watumull.presencify.core.presentation.toUiText
import edu.watumull.presencify.core.presentation.utils.BaseViewModel
import edu.watumull.presencify.core.presentation.validation.ValidationResult
import edu.watumull.presencify.core.presentation.validation.validateAsAcademicEndYear
import edu.watumull.presencify.core.presentation.validation.validateAsAcademicStartYear
import edu.watumull.presencify.core.presentation.validation.validateAsBranch
import edu.watumull.presencify.core.presentation.validation.validateAsDivision
import edu.watumull.presencify.core.presentation.validation.validateAsSemesterNumber
import edu.watumull.presencify.core.presentation.validation.validateAsTimetableVersion
import edu.watumull.presencify.feature.schedule.navigation.ScheduleRoutes
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.launch

class AddEditTimetableViewModel(
    private val branchRepository: BranchRepository,
    private val divisionRepository: DivisionRepository,
    private val batchRepository: BatchRepository,
    private val timetableRepository: TimetableRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<AddEditTimetableState, AddEditTimetableEvent, AddEditTimetableAction>(
    initialState = AddEditTimetableState()
) {

    private val route = savedStateHandle.toRoute<ScheduleRoutes.AddEditTimetable>()

    init {
        loadBranches()
        route.timetableId?.let { timetableId ->
            updateState { it.copy(isEditMode = true, timetableId = timetableId) }
            loadTimetableData(timetableId)
        }
    }

    private fun loadBranches() {
        viewModelScope.launch {
            updateState { it.copy(areBranchesLoading = true) }
            branchRepository.getBranches(searchQuery = null)
                .onSuccess { branches ->
                    updateState {
                        it.copy(
                            branchOptions = branches.toPersistentList(),
                            areBranchesLoading = false
                        )
                    }
                }
                .onError { error ->
                    updateState {
                        it.copy(
                            areBranchesLoading = false,
                            dialogState = DialogState(
                                dialogType = DialogType.ERROR,
                                title = UiText.DynamicString("Error"),
                                message = error.toUiText()
                            )
                        )
                    }
                }
        }
    }

    private fun loadTimetableData(timetableId: String) {
        viewModelScope.launch {
            updateState { it.copy(viewState = AddEditTimetableState.ViewState.Loading) }
            timetableRepository.getTimetableById(timetableId)
                .onSuccess { timetable ->
                    timetable.division?.let { division ->
                        division.semester?.let { semester ->
                            updateState {
                                it.copy(
                                    viewState = AddEditTimetableState.ViewState.Content,
                                    selectedDivision = division,
                                    timetableVersion = timetable.timetableVersion.toString(),
                                    selectedBranch = state.branchOptions.firstOrNull { b -> b.id == semester.branchId },
                                    selectedSemesterNumber = semester.semesterNumber,
                                    startYear = semester.academicStartYear.toString(),
                                    endYear = semester.academicEndYear.toString(),
                                    areDivisionsVisible = true,
                                    divisionOptions = persistentListOf(division)
                                )
                            }
                        } ?: run {
                            updateState {
                                it.copy(
                                    viewState = AddEditTimetableState.ViewState.Error(
                                        UiText.DynamicString("Semester data not found")
                                    )
                                )
                            }
                        }
                    } ?: run {
                        updateState {
                            it.copy(
                                viewState = AddEditTimetableState.ViewState.Error(
                                    UiText.DynamicString("Division data not found")
                                )
                            )
                        }
                    }
                }
                .onError { error ->
                    updateState {
                        it.copy(
                            viewState = AddEditTimetableState.ViewState.Error(error.toUiText())
                        )
                    }
                }
        }
    }

    private fun validateParametersForm(): Boolean {
        val branch = state.selectedBranch
        val semesterNumber = state.selectedSemesterNumber
        val startYear = state.startYear
        val endYear = state.endYear

        val branchValidation = branch.validateAsBranch()
        val semesterValidation = semesterNumber.validateAsSemesterNumber()
        val startYearValidation = startYear.validateAsAcademicStartYear(endYear = endYear)
        val endYearValidation = endYear.validateAsAcademicEndYear(startYear = startYear)

        updateState {
            it.copy(
                branchError = branchValidation.errorMessage,
                semesterNumberError = semesterValidation.errorMessage,
                startYearError = startYearValidation.errorMessage,
                endYearError = endYearValidation.errorMessage
            )
        }

        return branchValidation.successful &&
                semesterValidation.successful &&
                startYearValidation.successful &&
                endYearValidation.successful
    }

    private fun findDivisionsAndBatches() {
        viewModelScope.launch {
            if (!validateParametersForm()) return@launch

            val branchId = state.selectedBranch?.id ?: return@launch
            val semesterNumber = state.selectedSemesterNumber ?: return@launch
            val startYear = state.startYear.toIntOrNull() ?: return@launch
            val endYear = state.endYear.toIntOrNull() ?: return@launch

            updateState { it.copy(isLookingDivisions = true, isLookingBatches = true) }

            divisionRepository.getDivisions(
                branchId = branchId,
                semesterNumber = semesterNumber,
                academicStartYear = startYear,
                academicEndYear = endYear,
                getAll = true
            )
                .onSuccess { divisionListWithTotalCount ->
                    updateState { it.copy(isLookingDivisions = false) }

                    if (divisionListWithTotalCount.divisions.isEmpty()) {
                        updateState {
                            it.copy(
                                isLookingBatches = false,
                                dialogState = DialogState(
                                    dialogType = DialogType.ERROR,
                                    title = UiText.DynamicString("Divisions Not Found"),
                                    message = UiText.DynamicString(
                                        "No divisions found for the selected branch, semester number, and academic year. Please check your selection."
                                    )
                                )
                            )
                        }
                    } else {
                        updateState {
                            it.copy(
                                divisionOptions = divisionListWithTotalCount.divisions.toPersistentList(),
                                areDivisionsVisible = true,
                                selectedDivision = null,
                                divisionError = null
                            )
                        }

                        loadBatches(branchId, semesterNumber, startYear, endYear)
                    }
                }
                .onError { error ->
                    updateState {
                        it.copy(
                            isLookingDivisions = false,
                            isLookingBatches = false,
                            dialogState = DialogState(
                                dialogType = DialogType.ERROR,
                                title = UiText.DynamicString("Error"),
                                message = error.toUiText()
                            )
                        )
                    }
                }
        }
    }

    private suspend fun loadBatches(
        branchId: String,
        semesterNumber: edu.watumull.presencify.core.domain.enums.SemesterNumber,
        startYear: Int,
        endYear: Int
    ) {
        batchRepository.getBatches(
            branchId = branchId,
            semesterNumber = semesterNumber,
            academicStartYear = startYear,
            academicEndYear = endYear,
            getAll = true
        )
            .onSuccess { batchListWithTotalCount ->
                updateState {
                    it.copy(
                        isLookingBatches = false,
                        batchOptions = batchListWithTotalCount.batches.toPersistentList(),
                        areBatchesVisible = batchListWithTotalCount.batches.isNotEmpty()
                    )
                }
            }
            .onError { error ->
                updateState {
                    it.copy(
                        isLookingBatches = false,
                        dialogState = DialogState(
                            dialogType = DialogType.ERROR,
                            title = UiText.DynamicString("Error Loading Batches"),
                            message = error.toUiText()
                        )
                    )
                }
            }
    }

    private fun validateTimetableForm(): Boolean {
        val division = state.selectedDivision
        val timetableVersion = state.timetableVersion

        val divisionValidation = division.validateAsDivision()

        val versionValidation = if (!state.isEditMode) {
            timetableVersion.toIntOrNull().validateAsTimetableVersion()
        } else {
            ValidationResult(successful = true)
        }

        updateState {
            it.copy(
                divisionError = divisionValidation.errorMessage,
                timetableVersionError = versionValidation.errorMessage
            )
        }

        return divisionValidation.successful && versionValidation.successful
    }

    private fun saveTimetable() {
        viewModelScope.launch {
            if (!validateTimetableForm()) return@launch

            val divisionId = state.selectedDivision?.id ?: return@launch
            val version = state.timetableVersion.toIntOrNull()

            updateState { it.copy(isSaving = true) }

            val result = if (state.isEditMode && state.timetableId != null) {
                if (version == null) {
                    updateState {
                        it.copy(
                            isSaving = false,
                            dialogState = DialogState(
                                dialogType = DialogType.ERROR,
                                title = UiText.DynamicString("Error"),
                                message = UiText.DynamicString("Invalid version number")
                            )
                        )
                    }
                    return@launch
                }
                timetableRepository.updateTimetable(state.timetableId!!, version)
            } else {
                timetableRepository.addTimetable(divisionId, version)
            }

            result
                .onSuccess { _ ->
                    updateState { it.copy(isSaving = false) }
                    viewModelScope.launch {
                        SnackbarController.sendEvent(
                            SnackbarEvent(
                                message = if (state.isEditMode) "Timetable updated successfully" else "Timetable created successfully"
                            )
                        )
                    }
                    sendEvent(AddEditTimetableEvent.NavigateBack)
                }
                .onError { error ->
                    updateState {
                        it.copy(
                            isSaving = false,
                            dialogState = DialogState(
                                dialogType = DialogType.ERROR,
                                title = UiText.DynamicString("Error"),
                                message = error.toUiText()
                            )
                        )
                    }
                }
        }
    }

    override fun handleAction(action: AddEditTimetableAction) {
        when (action) {
            is AddEditTimetableAction.NavigateBack -> handleBackNavigation()
            is AddEditTimetableAction.ConfirmNavigateBack -> confirmNavigateBack()
            is AddEditTimetableAction.DismissDialog -> {
                updateState { it.copy(dialogState = null) }
            }

            is AddEditTimetableAction.SelectBranch -> {
                updateState {
                    it.copy(
                        selectedBranch = action.branch,
                        branchError = null,
                        areDivisionsVisible = false,
                        divisionOptions = persistentListOf(),
                        selectedDivision = null,
                        areBatchesVisible = false,
                        batchOptions = persistentListOf(),
                        selectedBatches = persistentListOf()
                    )
                }
            }

            is AddEditTimetableAction.SelectSemesterNumber -> {
                updateState {
                    it.copy(
                        selectedSemesterNumber = action.semesterNumber,
                        semesterNumberError = null,
                        areDivisionsVisible = false,
                        divisionOptions = persistentListOf(),
                        selectedDivision = null,
                        areBatchesVisible = false,
                        batchOptions = persistentListOf(),
                        selectedBatches = persistentListOf()
                    )
                }
            }

            is AddEditTimetableAction.UpdateStartYear -> {
                updateState {
                    it.copy(
                        startYear = action.year,
                        startYearError = null,
                        areDivisionsVisible = false,
                        divisionOptions = persistentListOf(),
                        selectedDivision = null,
                        areBatchesVisible = false,
                        batchOptions = persistentListOf(),
                        selectedBatches = persistentListOf()
                    )
                }
            }

            is AddEditTimetableAction.UpdateEndYear -> {
                updateState {
                    it.copy(
                        endYear = action.year,
                        endYearError = null,
                        areDivisionsVisible = false,
                        divisionOptions = persistentListOf(),
                        selectedDivision = null,
                        areBatchesVisible = false,
                        batchOptions = persistentListOf(),
                        selectedBatches = persistentListOf()
                    )
                }
            }

            is AddEditTimetableAction.ChangeBranchDropDownVisibility -> {
                updateState { it.copy(isBranchDropdownOpen = action.isOpen) }
            }

            is AddEditTimetableAction.ChangeSemesterNumberDropDownVisibility -> {
                updateState { it.copy(isSemesterNumberDropdownOpen = action.isOpen) }
            }

            is AddEditTimetableAction.FindDivisionsAndBatchesClick -> {
                findDivisionsAndBatches()
            }

            is AddEditTimetableAction.SelectDivision -> {
                updateState {
                    it.copy(
                        selectedDivision = action.division,
                        divisionError = null
                    )
                }
            }

            is AddEditTimetableAction.ChangeDivisionDropDownVisibility -> {
                updateState { it.copy(isDivisionDropdownOpen = action.isOpen) }
            }

            is AddEditTimetableAction.ToggleBatchSelection -> {
                val currentBatches = state.selectedBatches
                val newBatches = if (currentBatches.contains(action.batch)) {
                    currentBatches.remove(action.batch)
                } else {
                    currentBatches.add(action.batch)
                }
                updateState { it.copy(selectedBatches = newBatches) }
            }

            is AddEditTimetableAction.UpdateTimetableVersion -> {
                updateState {
                    it.copy(
                        timetableVersion = action.version,
                        timetableVersionError = null
                    )
                }
            }

            is AddEditTimetableAction.SaveTimetableClick -> {
                saveTimetable()
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
            sendEvent(AddEditTimetableEvent.NavigateBack)
        }
    }

    private fun confirmNavigateBack() {
        updateState { it.copy(dialogState = null) }
        sendEvent(AddEditTimetableEvent.NavigateBack)
    }

    private fun hasUnsavedChanges(): Boolean {
        return state.selectedBranch != null ||
               state.selectedSemesterNumber != null ||
               state.startYear.isNotBlank() ||
               state.endYear.isNotBlank() ||
               state.selectedDivision != null ||
               state.timetableVersion.isNotBlank() ||
               state.selectedBatches.isNotEmpty()
    }
}
