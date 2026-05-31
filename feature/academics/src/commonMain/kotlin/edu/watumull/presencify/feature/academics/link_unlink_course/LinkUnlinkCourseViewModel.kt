package edu.watumull.presencify.feature.academics.link_unlink_course

import androidx.lifecycle.viewModelScope
import edu.watumull.presencify.core.designsystem.components.dialog.DialogType
import edu.watumull.presencify.core.domain.onError
import edu.watumull.presencify.core.domain.onSuccess
import edu.watumull.presencify.core.domain.repository.academics.BranchRepository
import edu.watumull.presencify.core.presentation.UiText
import edu.watumull.presencify.core.presentation.components.dialog.DialogState
import edu.watumull.presencify.core.presentation.toUiText
import edu.watumull.presencify.core.presentation.utils.BaseViewModel
import edu.watumull.presencify.core.presentation.validation.validateAsBranch
import edu.watumull.presencify.core.presentation.validation.validateAsSemesterNumber
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.launch

class LinkUnlinkCourseViewModel(
    private val branchRepository: BranchRepository,
) : BaseViewModel<LinkUnlinkCourseState, LinkUnlinkCourseEvent, LinkUnlinkCourseAction>(
    initialState = LinkUnlinkCourseState()
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

        updateState {
            it.copy(
                branchError = branchValidation.errorMessage,
                semesterError = semesterValidation.errorMessage
            )
        }

        return branchValidation.successful && semesterValidation.successful
    }

    override fun handleAction(action: LinkUnlinkCourseAction) {
        when (action) {
            is LinkUnlinkCourseAction.NavigateBack -> {
                sendEvent(LinkUnlinkCourseEvent.NavigateBack)
            }

            is LinkUnlinkCourseAction.DismissDialog -> {
                updateState { it.copy(dialogState = null) }
            }

            is LinkUnlinkCourseAction.SelectBranch -> {
                updateState {
                    it.copy(
                        selectedBranch = action.branch,
                        branchError = null
                    )
                }
            }

            is LinkUnlinkCourseAction.SelectSemesterNumber -> {
                updateState {
                    it.copy(
                        selectedSemesterNumber = action.semesterNumber,
                        semesterError = null
                    )
                }
            }

            is LinkUnlinkCourseAction.ChangeBranchDropDownVisibility -> {
                updateState { it.copy(isBranchDropdownOpen = action.isOpen) }
            }

            is LinkUnlinkCourseAction.ChangeSemesterDropDownVisibility -> {
                updateState { it.copy(isSemesterDropdownOpen = action.isOpen) }
            }

            is LinkUnlinkCourseAction.LinkCoursesClick -> {
                if (!validateForm()) return

                val branchId = state.selectedBranch?.id ?: return
                val semesterNumber = state.selectedSemesterNumber?.value ?: return

                sendEvent(
                    LinkUnlinkCourseEvent.NavigateToSearchCourse(
                        branchId = branchId,
                        semesterNumber = semesterNumber
                    )
                )
            }
        }
    }
}
