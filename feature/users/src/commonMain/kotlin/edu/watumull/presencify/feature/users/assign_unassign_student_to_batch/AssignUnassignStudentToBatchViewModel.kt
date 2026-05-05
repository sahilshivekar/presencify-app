package edu.watumull.presencify.feature.users.assign_unassign_student_to_batch

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
import edu.watumull.presencify.core.presentation.validation.validateAsAcademicEndYear
import edu.watumull.presencify.core.presentation.validation.validateAsAcademicStartYear
import edu.watumull.presencify.core.presentation.validation.validateAsBatch
import edu.watumull.presencify.core.presentation.validation.validateAsBranch
import edu.watumull.presencify.core.presentation.validation.validateAsSemesterNumber
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.launch

class AssignUnassignStudentToBatchViewModel(
    private val branchRepository: BranchRepository,
    private val divisionRepository: DivisionRepository,
    private val batchRepository: BatchRepository,
) : BaseViewModel<AssignUnassignStudentToBatchState, AssignUnassignStudentToBatchEvent, AssignUnassignStudentToBatchAction>(
    initialState = AssignUnassignStudentToBatchState()
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
                            dialogState = AssignUnassignStudentToBatchState.DialogState(
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
        val branchValidation = state.selectedBranch.validateAsBranch()
        val semesterValidation = state.selectedSemesterNumber.validateAsSemesterNumber()
        val startYearValidation = state.startYear.validateAsAcademicStartYear(endYear = state.endYear)
        val endYearValidation = state.endYear.validateAsAcademicEndYear(startYear = state.startYear)

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

    private fun findDivisions() {
        viewModelScope.launch {
            if (!validateParametersForm()) return@launch

            val branchId = state.selectedBranch?.id ?: return@launch
            val semesterNumber = state.selectedSemesterNumber ?: return@launch
            val startYear = state.startYear.toIntOrNull() ?: return@launch
            val endYear = state.endYear.toIntOrNull() ?: return@launch

            updateState { it.copy(isLookingDivisions = true) }

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
                                isLookingDivisions = false,
                                dialogState = AssignUnassignStudentToBatchState.DialogState(
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
                            isLookingDivisions = false,
                            dialogState = AssignUnassignStudentToBatchState.DialogState(
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

        updateState { it.copy(isLookingDivisions = false) }

        if (hasError) {
            updateState {
                it.copy(
                    dialogState = AssignUnassignStudentToBatchState.DialogState(
                        dialogType = DialogType.ERROR,
                        title = "Error",
                        message = errorMessage ?: UiText.DynamicString("Failed to fetch batches"),
                        dialogIntention = DialogIntention.GENERIC
                    )
                )
            }
        } else if (allBatches.isEmpty()) {
            updateState {
                it.copy(
                    dialogState = AssignUnassignStudentToBatchState.DialogState(
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
            // Show all batches in a single dropdown
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

    private fun validateBatchSelection(): Boolean {
        val batchValidation = state.selectedBatch.validateAsBatch()

        updateState {
            it.copy(batchError = batchValidation.errorMessage)
        }

        return batchValidation.successful
    }

    private fun navigateToSearchStudent() {
        if (!validateBatchSelection()) return

        val batchId = state.selectedBatch?.id ?: return
        val branchId = state.selectedBranch?.id ?: return
        val academicStartYear = state.startYear.toIntOrNull() ?: return
        val academicEndYear = state.endYear.toIntOrNull() ?: return
        val semesterNumber = state.selectedSemesterNumber?.value ?: return

        sendEvent(AssignUnassignStudentToBatchEvent.NavigateToSearchStudent(
            batchId = batchId,
            branchId = branchId,
            academicStartYear = academicStartYear,
            academicEndYear = academicEndYear,
            semesterNumber = semesterNumber
        ))
    }

    override fun handleAction(action: AssignUnassignStudentToBatchAction) {
        when (action) {
            is AssignUnassignStudentToBatchAction.BackButtonClick -> {
                sendEvent(AssignUnassignStudentToBatchEvent.NavigateBack)
            }

            is AssignUnassignStudentToBatchAction.DismissDialog -> {
                updateState { it.copy(dialogState = null) }
            }

            is AssignUnassignStudentToBatchAction.SelectBranch -> {
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

            is AssignUnassignStudentToBatchAction.SelectSemesterNumber -> {
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

            is AssignUnassignStudentToBatchAction.UpdateStartYear -> {
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

            is AssignUnassignStudentToBatchAction.UpdateEndYear -> {
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

            is AssignUnassignStudentToBatchAction.ChangeBranchDropDownVisibility -> {
                updateState { it.copy(isBranchDropdownOpen = action.isOpen) }
            }

            is AssignUnassignStudentToBatchAction.ChangeSemesterNumberDropDownVisibility -> {
                updateState { it.copy(isSemesterNumberDropdownOpen = action.isOpen) }
            }

            is AssignUnassignStudentToBatchAction.FindBatchesClick -> {
                findDivisions()
            }

            is AssignUnassignStudentToBatchAction.SelectBatch -> {
                updateState {
                    it.copy(
                        selectedBatch = action.batch,
                        batchError = null
                    )
                }
            }

            is AssignUnassignStudentToBatchAction.ChangeBatchDropDownVisibility -> {
                updateState { it.copy(isBatchDropdownOpen = action.isOpen) }
            }

            is AssignUnassignStudentToBatchAction.NavigateToSearchStudentClick -> {
                navigateToSearchStudent()
            }
        }
    }
}
