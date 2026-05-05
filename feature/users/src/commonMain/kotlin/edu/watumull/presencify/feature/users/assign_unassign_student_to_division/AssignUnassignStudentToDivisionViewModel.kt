package edu.watumull.presencify.feature.users.assign_unassign_student_to_division

import androidx.lifecycle.viewModelScope
import edu.watumull.presencify.core.design.systems.components.dialog.DialogType
import edu.watumull.presencify.core.domain.onError
import edu.watumull.presencify.core.domain.onSuccess
import edu.watumull.presencify.core.domain.repository.academics.BranchRepository
import edu.watumull.presencify.core.domain.repository.academics.DivisionRepository
import edu.watumull.presencify.core.presentation.UiText
import edu.watumull.presencify.core.presentation.toUiText
import edu.watumull.presencify.core.presentation.utils.BaseViewModel
import edu.watumull.presencify.core.presentation.validation.validateAsAcademicEndYear
import edu.watumull.presencify.core.presentation.validation.validateAsAcademicStartYear
import edu.watumull.presencify.core.presentation.validation.validateAsBranch
import edu.watumull.presencify.core.presentation.validation.validateAsDivision
import edu.watumull.presencify.core.presentation.validation.validateAsSemesterNumber
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.launch

class AssignUnassignStudentToDivisionViewModel(
    private val branchRepository: BranchRepository,
    private val divisionRepository: DivisionRepository,
) : BaseViewModel<AssignUnassignStudentToDivisionState, AssignUnassignStudentToDivisionEvent, AssignUnassignStudentToDivisionAction>(
    initialState = AssignUnassignStudentToDivisionState()
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
                            dialogState = AssignUnassignStudentToDivisionState.DialogState(
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
                    updateState { it.copy(isLookingDivisions = false) }

                    if (divisionListWithTotalCount.divisions.isEmpty()) {
                        // No divisions found
                        updateState {
                            it.copy(
                                dialogState = AssignUnassignStudentToDivisionState.DialogState(
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
                        // Divisions found, show them in dropdown
                        updateState {
                            it.copy(
                                divisionOptions = divisionListWithTotalCount.divisions.toPersistentList(),
                                areDivisionsVisible = true,
                                selectedDivision = null,
                                divisionError = null
                            )
                        }
                    }
                }
                .onError { error ->
                    updateState {
                        it.copy(
                            isLookingDivisions = false,
                            dialogState = AssignUnassignStudentToDivisionState.DialogState(
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

    private fun validateDivisionSelection(): Boolean {
        val divisionValidation = state.selectedDivision.validateAsDivision()

        updateState {
            it.copy(divisionError = divisionValidation.errorMessage)
        }

        return divisionValidation.successful
    }

    private fun navigateToSearchStudent() {
        if (!validateDivisionSelection()) return

        val divisionId = state.selectedDivision?.id ?: return
        val branchId = state.selectedBranch?.id ?: return
        val academicStartYear = state.startYear.toIntOrNull() ?: return
        val academicEndYear = state.endYear.toIntOrNull() ?: return
        val semesterNumber = state.selectedSemesterNumber?.value ?: return

        sendEvent(AssignUnassignStudentToDivisionEvent.NavigateToSearchStudent(
            divisionId = divisionId,
            branchId = branchId,
            academicStartYear = academicStartYear,
            academicEndYear = academicEndYear,
            semesterNumber = semesterNumber
        ))
    }

    override fun handleAction(action: AssignUnassignStudentToDivisionAction) {
        when (action) {
            is AssignUnassignStudentToDivisionAction.BackButtonClick -> {
                sendEvent(AssignUnassignStudentToDivisionEvent.NavigateBack)
            }

            is AssignUnassignStudentToDivisionAction.DismissDialog -> {
                updateState { it.copy(dialogState = null) }
            }

            is AssignUnassignStudentToDivisionAction.SelectBranch -> {
                updateState {
                    it.copy(
                        selectedBranch = action.branch,
                        branchError = null,
                        // Reset divisions when branch changes
                        areDivisionsVisible = false,
                        divisionOptions = persistentListOf(),
                        selectedDivision = null
                    )
                }
            }

            is AssignUnassignStudentToDivisionAction.SelectSemesterNumber -> {
                updateState {
                    it.copy(
                        selectedSemesterNumber = action.semesterNumber,
                        semesterNumberError = null,
                        // Reset divisions when semester changes
                        areDivisionsVisible = false,
                        divisionOptions = persistentListOf(),
                        selectedDivision = null
                    )
                }
            }

            is AssignUnassignStudentToDivisionAction.UpdateStartYear -> {
                updateState {
                    it.copy(
                        startYear = action.year,
                        startYearError = null,
                        // Reset divisions when year changes
                        areDivisionsVisible = false,
                        divisionOptions = persistentListOf(),
                        selectedDivision = null
                    )
                }
            }

            is AssignUnassignStudentToDivisionAction.UpdateEndYear -> {
                updateState {
                    it.copy(
                        endYear = action.year,
                        endYearError = null,
                        // Reset divisions when year changes
                        areDivisionsVisible = false,
                        divisionOptions = persistentListOf(),
                        selectedDivision = null
                    )
                }
            }

            is AssignUnassignStudentToDivisionAction.ChangeBranchDropDownVisibility -> {
                updateState { it.copy(isBranchDropdownOpen = action.isOpen) }
            }

            is AssignUnassignStudentToDivisionAction.ChangeSemesterNumberDropDownVisibility -> {
                updateState { it.copy(isSemesterNumberDropdownOpen = action.isOpen) }
            }

            is AssignUnassignStudentToDivisionAction.FindDivisionsClick -> {
                findDivisions()
            }

            is AssignUnassignStudentToDivisionAction.SelectDivision -> {
                updateState {
                    it.copy(
                        selectedDivision = action.division,
                        divisionError = null
                    )
                }
            }

            is AssignUnassignStudentToDivisionAction.ChangeDivisionDropDownVisibility -> {
                updateState { it.copy(isDivisionDropdownOpen = action.isOpen) }
            }

            is AssignUnassignStudentToDivisionAction.NavigateToSearchStudentClick -> {
                navigateToSearchStudent()
            }
        }
    }
}
