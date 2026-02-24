package edu.watumull.presencify.feature.users.modify_student_batch

import androidx.lifecycle.viewModelScope
import edu.watumull.presencify.core.design.systems.components.dialog.DialogType
import edu.watumull.presencify.core.domain.onError
import edu.watumull.presencify.core.domain.onSuccess
import edu.watumull.presencify.core.domain.repository.academics.BatchRepository
import edu.watumull.presencify.core.domain.repository.academics.BranchRepository
import edu.watumull.presencify.core.domain.repository.academics.DivisionRepository
import edu.watumull.presencify.core.presentation.UiText
import edu.watumull.presencify.core.presentation.toUiText
import edu.watumull.presencify.core.presentation.utils.BaseViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.launch

class ModifyStudentBatchViewModel(
    private val branchRepository: BranchRepository,
    private val divisionRepository: DivisionRepository,
    private val batchRepository: BatchRepository,
) : BaseViewModel<ModifyStudentBatchState, ModifyStudentBatchEvent, ModifyStudentBatchAction>(
    initialState = ModifyStudentBatchState()
) {

    init {
        loadBranches()
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
                            dialogState = ModifyStudentBatchState.DialogState(
                                dialogType = DialogType.ERROR,
                                title = "Error",
                                message = error.toUiText(),
                                dialogIntention = DialogIntention.GENERIC
                            )
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

        var hasError = false

        if (branch == null) {
            updateState { it.copy(branchError = "Please select a branch") }
            hasError = true
        } else {
            updateState { it.copy(branchError = null) }
        }

        if (semesterNumber == null) {
            updateState { it.copy(semesterNumberError = "Please select a semester number") }
            hasError = true
        } else {
            updateState { it.copy(semesterNumberError = null) }
        }

        if (startYear.isBlank()) {
            updateState { it.copy(startYearError = "Please enter a start year") }
            hasError = true
        } else {
            updateState { it.copy(startYearError = null) }
        }

        if (endYear.isBlank()) {
            updateState { it.copy(endYearError = "Please enter an end year") }
            hasError = true
        } else {
            updateState { it.copy(endYearError = null) }
        }

        return !hasError
    }

    private fun findBatches() {
        viewModelScope.launch {
            if (!validateParametersForm()) return@launch

            val branchId = state.selectedBranch?.id ?: return@launch
            val semesterNumber = state.selectedSemesterNumber ?: return@launch
            val startYear = state.startYear.toIntOrNull() ?: return@launch
            val endYear = state.endYear.toIntOrNull() ?: return@launch

            updateState { it.copy(isLookingBatches = true) }

            divisionRepository.getDivisions(
                branchId = branchId,
                semesterNumber = semesterNumber,
                academicStartYear = startYear,
                academicEndYear = endYear,
                getAll = true
            )
                .onSuccess { divisionListWithTotalCount ->
                    if (divisionListWithTotalCount.divisions.isEmpty()) {
                        // No divisions found
                        updateState {
                            it.copy(
                                isLookingBatches = false,
                                dialogState = ModifyStudentBatchState.DialogState(
                                    dialogType = DialogType.ERROR,
                                    title = "Divisions Not Found",
                                    message = UiText.DynamicString(
                                        "No divisions found for the selected branch, semester number, and academic year. Please check your selection."
                                    ),
                                    dialogIntention = DialogIntention.GENERIC
                                )
                            )
                        }
                    } else {
                        // Divisions found, now fetch all batches for all divisions
                        fetchAllBatchesForDivisions(
                            divisions = divisionListWithTotalCount.divisions,
                            branchId = branchId,
                            semesterNumber = semesterNumber,
                            startYear = startYear,
                            endYear = endYear
                        )
                    }
                }
                .onError { error ->
                    updateState {
                        it.copy(
                            isLookingBatches = false,
                            dialogState = ModifyStudentBatchState.DialogState(
                                dialogType = DialogType.ERROR,
                                title = "Error",
                                message = error.toUiText(),
                                dialogIntention = DialogIntention.GENERIC
                            )
                        )
                    }
                }
        }
    }

    private suspend fun fetchAllBatchesForDivisions(
        divisions: List<edu.watumull.presencify.core.domain.model.academics.Division>,
        branchId: String,
        semesterNumber: edu.watumull.presencify.core.domain.enums.SemesterNumber,
        startYear: Int,
        endYear: Int
    ) {
        // Fetch batches for all divisions in parallel
        val allBatches = mutableListOf<edu.watumull.presencify.core.domain.model.academics.Batch>()
        var hasError = false
        var errorMessage: UiText? = null

        divisions.forEach { division ->
            batchRepository.getBatches(
                divisionId = division.id,
                branchId = branchId,
                semesterNumber = semesterNumber,
                academicStartYear = startYear,
                academicEndYear = endYear,
                getAll = true
            )
                .onSuccess { batchListWithTotalCount ->
                    allBatches.addAll(batchListWithTotalCount.batches)
                }
                .onError { error ->
                    hasError = true
                    errorMessage = error.toUiText()
                }
        }

        updateState { it.copy(isLookingBatches = false) }

        if (hasError) {
            updateState {
                it.copy(
                    dialogState = ModifyStudentBatchState.DialogState(
                        dialogType = DialogType.ERROR,
                        title = "Error",
                        message = errorMessage ?: UiText.DynamicString("Failed to fetch batches"),
                        dialogIntention = DialogIntention.GENERIC
                    )
                )
            }
        } else if (allBatches.isEmpty()) {
            // No batches found
            updateState {
                it.copy(
                    dialogState = ModifyStudentBatchState.DialogState(
                        dialogType = DialogType.ERROR,
                        title = "Batches Not Found",
                        message = UiText.DynamicString(
                            "No batches found for the selected parameters. Please check your selection."
                        ),
                        dialogIntention = DialogIntention.GENERIC
                    )
                )
            }
        } else {
            // Batches found, show them in dropdown
            updateState {
                it.copy(
                    batchOptions = allBatches.toPersistentList(),
                    areBatchesVisible = true,
                    selectedBatch = null,
                    batchError = null
                )
            }
        }
    }

    private fun validateFinalSelection(): Boolean {
        val batch = state.selectedBatch
        val newStartDate = state.newBatchStartDate

        var hasError = false

        if (batch == null) {
            updateState { it.copy(batchError = "Please select a batch") }
            hasError = true
        } else {
            updateState { it.copy(batchError = null) }
        }

        if (newStartDate == null) {
            updateState { it.copy(newBatchStartDateError = "Please select a start date") }
            hasError = true
        } else {
            updateState { it.copy(newBatchStartDateError = null) }
        }

        return !hasError
    }

    private fun navigateToSearchStudent() {
        if (!validateFinalSelection()) return

        val batchId = state.selectedBatch?.id ?: return
        val branchId = state.selectedBranch?.id ?: return
        val academicStartYear = state.startYear.toIntOrNull() ?: return
        val academicEndYear = state.endYear.toIntOrNull() ?: return
        val semesterNumber = state.selectedSemesterNumber?.value ?: return
        val newStartDate = state.newBatchStartDate.toString()

        sendEvent(
            ModifyStudentBatchEvent.NavigateToSearchStudent(
                batchId = batchId,
                branchId = branchId,
                academicStartYear = academicStartYear,
                academicEndYear = academicEndYear,
                semesterNumber = semesterNumber,
                newStartDate = newStartDate
            )
        )
    }

    override fun handleAction(action: ModifyStudentBatchAction) {
        when (action) {
            is ModifyStudentBatchAction.BackButtonClick -> {
                sendEvent(ModifyStudentBatchEvent.NavigateBack)
            }

            is ModifyStudentBatchAction.DismissDialog -> {
                updateState { it.copy(dialogState = null) }
            }

            is ModifyStudentBatchAction.SelectBranch -> {
                updateState {
                    it.copy(
                        selectedBranch = action.branch,
                        branchError = null,
                        // Reset batches when branch changes
                        areBatchesVisible = false,
                        batchOptions = persistentListOf(),
                        selectedBatch = null
                    )
                }
            }

            is ModifyStudentBatchAction.SelectSemesterNumber -> {
                updateState {
                    it.copy(
                        selectedSemesterNumber = action.semesterNumber,
                        semesterNumberError = null,
                        // Reset batches when semester changes
                        areBatchesVisible = false,
                        batchOptions = persistentListOf(),
                        selectedBatch = null
                    )
                }
            }

            is ModifyStudentBatchAction.UpdateStartYear -> {
                updateState {
                    it.copy(
                        startYear = action.year,
                        startYearError = null,
                        // Reset batches when year changes
                        areBatchesVisible = false,
                        batchOptions = persistentListOf(),
                        selectedBatch = null
                    )
                }
            }

            is ModifyStudentBatchAction.UpdateEndYear -> {
                updateState {
                    it.copy(
                        endYear = action.year,
                        endYearError = null,
                        // Reset batches when year changes
                        areBatchesVisible = false,
                        batchOptions = persistentListOf(),
                        selectedBatch = null
                    )
                }
            }

            is ModifyStudentBatchAction.ChangeBranchDropDownVisibility -> {
                updateState { it.copy(isBranchDropdownOpen = action.isOpen) }
            }

            is ModifyStudentBatchAction.ChangeSemesterNumberDropDownVisibility -> {
                updateState { it.copy(isSemesterNumberDropdownOpen = action.isOpen) }
            }

            is ModifyStudentBatchAction.FindBatchesClick -> {
                findBatches()
            }

            is ModifyStudentBatchAction.SelectBatch -> {
                updateState {
                    it.copy(
                        selectedBatch = action.batch,
                        batchError = null
                    )
                }
            }

            is ModifyStudentBatchAction.ChangeBatchDropDownVisibility -> {
                updateState { it.copy(isBatchDropdownOpen = action.isOpen) }
            }

            is ModifyStudentBatchAction.ChangeDatePickerVisibility -> {
                updateState { it.copy(isDatePickerVisible = action.isVisible) }
            }

            is ModifyStudentBatchAction.UpdateNewBatchStartDate -> {
                updateState {
                    it.copy(
                        newBatchStartDate = action.date,
                        newBatchStartDateError = null,
                        isDatePickerVisible = false
                    )
                }
            }

            is ModifyStudentBatchAction.NavigateToSearchStudentClick -> {
                navigateToSearchStudent()
            }
        }
    }
}
