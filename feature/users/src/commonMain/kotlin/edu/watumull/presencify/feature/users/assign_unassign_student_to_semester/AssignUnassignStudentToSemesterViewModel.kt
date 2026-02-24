package edu.watumull.presencify.feature.users.assign_unassign_student_to_semester

import androidx.lifecycle.viewModelScope
import edu.watumull.presencify.core.design.systems.components.dialog.DialogType
import edu.watumull.presencify.core.domain.onError
import edu.watumull.presencify.core.domain.onSuccess
import edu.watumull.presencify.core.domain.repository.academics.BranchRepository
import edu.watumull.presencify.core.domain.repository.academics.SemesterRepository
import edu.watumull.presencify.core.presentation.UiText
import edu.watumull.presencify.core.presentation.toUiText
import edu.watumull.presencify.core.presentation.utils.BaseViewModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.launch

class AssignUnassignStudentToSemesterViewModel(
    private val branchRepository: BranchRepository,
    private val semesterRepository: SemesterRepository,
) : BaseViewModel<AssignUnassignStudentToSemesterState, AssignUnassignStudentToSemesterEvent, AssignUnassignStudentToSemesterAction>(
    initialState = AssignUnassignStudentToSemesterState()
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
                            dialogState = AssignUnassignStudentToSemesterState.DialogState(
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

    private fun validateForm(): Boolean {
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

    private fun findSemesterAndNavigate() {
        viewModelScope.launch {
            if (!validateForm()) return@launch

            val branchId = state.selectedBranch?.id ?: return@launch
            val semesterNumber = state.selectedSemesterNumber ?: return@launch
            val startYear = state.startYear.toIntOrNull() ?: return@launch
            val endYear = state.endYear.toIntOrNull() ?: return@launch

            updateState { it.copy(isLookingSemester = true) }

            semesterRepository.getSemesters(
                branchId = branchId,
                semesterNumber = semesterNumber,
                academicStartYear = startYear,
                academicEndYear = endYear,
                page = 1,
                limit = 1
            )
                .onSuccess { semesterListWithTotalCount ->
                    updateState { it.copy(isLookingSemester = false) }

                    if (semesterListWithTotalCount.semesters.isEmpty()) {
                        // Semester not found
                        updateState {
                            it.copy(
                                dialogState = AssignUnassignStudentToSemesterState.DialogState(
                                    dialogType = DialogType.ERROR,
                                    title = "Semester Not Found",
                                    message = UiText.DynamicString(
                                        "No semester found for the selected branch, semester number, and academic year. Please check your selection."
                                    ),
                                    dialogIntention = DialogIntention.GENERIC
                                )
                            )
                        }
                    } else {
                        // Semester found, navigate to search student screen
                        val semesterId = semesterListWithTotalCount.semesters.first().id
                        val branchId = state.selectedBranch?.id ?: return@launch
                        sendEvent(AssignUnassignStudentToSemesterEvent.NavigateToSearchStudent(semesterId, branchId))
                    }
                }
                .onError { error ->
                    updateState {
                        it.copy(
                            isLookingSemester = false,
                            dialogState = AssignUnassignStudentToSemesterState.DialogState(
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

    override fun handleAction(action: AssignUnassignStudentToSemesterAction) {
        when (action) {
            is AssignUnassignStudentToSemesterAction.BackButtonClick -> {
                sendEvent(AssignUnassignStudentToSemesterEvent.NavigateBack)
            }

            is AssignUnassignStudentToSemesterAction.DismissDialog -> {
                updateState { it.copy(dialogState = null) }
            }

            is AssignUnassignStudentToSemesterAction.SelectBranch -> {
                updateState {
                    it.copy(
                        selectedBranch = action.branch,
                        branchError = null
                    )
                }
            }

            is AssignUnassignStudentToSemesterAction.SelectSemesterNumber -> {
                updateState {
                    it.copy(
                        selectedSemesterNumber = action.semesterNumber,
                        semesterNumberError = null
                    )
                }
            }

            is AssignUnassignStudentToSemesterAction.UpdateStartYear -> {
                updateState {
                    it.copy(
                        startYear = action.year,
                        startYearError = null
                    )
                }
            }

            is AssignUnassignStudentToSemesterAction.UpdateEndYear -> {
                updateState {
                    it.copy(
                        endYear = action.year,
                        endYearError = null
                    )
                }
            }

            is AssignUnassignStudentToSemesterAction.ChangeBranchDropDownVisibility -> {
                updateState { it.copy(isBranchDropdownOpen = action.isOpen) }
            }

            is AssignUnassignStudentToSemesterAction.ChangeSemesterNumberDropDownVisibility -> {
                updateState { it.copy(isSemesterNumberDropdownOpen = action.isOpen) }
            }

            is AssignUnassignStudentToSemesterAction.FindAndNavigateClick -> {
                findSemesterAndNavigate()
            }
        }
    }
}
