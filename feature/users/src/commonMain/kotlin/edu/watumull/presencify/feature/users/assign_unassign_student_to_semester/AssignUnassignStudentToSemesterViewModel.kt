package edu.watumull.presencify.feature.users.assign_unassign_student_to_semester

import androidx.lifecycle.viewModelScope
import edu.watumull.presencify.core.designsystem.components.dialog.DialogType
import edu.watumull.presencify.core.domain.onError
import edu.watumull.presencify.core.domain.onSuccess
import edu.watumull.presencify.core.domain.repository.academics.BranchRepository
import edu.watumull.presencify.core.domain.repository.academics.SemesterRepository
import edu.watumull.presencify.core.presentation.UiText
import edu.watumull.presencify.core.presentation.components.dialog.DialogState
import edu.watumull.presencify.core.presentation.toUiText
import edu.watumull.presencify.core.presentation.utils.BaseViewModel
import edu.watumull.presencify.core.presentation.validation.validateAsAcademicEndYear
import edu.watumull.presencify.core.presentation.validation.validateAsAcademicStartYear
import edu.watumull.presencify.core.presentation.validation.validateAsBranch
import edu.watumull.presencify.core.presentation.validation.validateAsSemesterNumber
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
                            dialogState = DialogState(
                                title = UiText.DynamicString("Error"),
                                message = error.toUiText(),
                                dialogType = DialogType.ERROR,
                            )
                        )
                    }
                }
        }
    }

    private fun validateForm(): Boolean {
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
                        updateState {
                            it.copy(
                                dialogState = DialogState(
                                    title = UiText.DynamicString("Semester Not Found"),
                                    message = UiText.DynamicString(
                                        "No semester found for the selected branch, semester number, and academic year. Please check your selection."
                                    ),
                                    dialogType = DialogType.ERROR,
                                )
                            )
                        }
                    } else {
                        val semesterId = semesterListWithTotalCount.semesters.first().id
                        val branchId = state.selectedBranch?.id ?: return@launch
                        sendEvent(AssignUnassignStudentToSemesterEvent.NavigateToSearchStudent(semesterId, branchId))
                    }
                }
                .onError { error ->
                    updateState {
                        it.copy(
                            isLookingSemester = false,
                            dialogState = DialogState(
                                title = UiText.DynamicString("Error"),
                                message = error.toUiText(),
                                dialogType = DialogType.ERROR,
                            )
                        )
                    }
                }
        }
    }

    override fun handleAction(action: AssignUnassignStudentToSemesterAction) {
        when (action) {
            is AssignUnassignStudentToSemesterAction.NavigateBack -> {
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
