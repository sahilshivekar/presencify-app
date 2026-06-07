package edu.watumull.presencify.feature.users.mark_unmark_student_dropout

import androidx.lifecycle.viewModelScope
import edu.watumull.presencify.core.designsystem.components.dialog.DialogType
import edu.watumull.presencify.core.presentation.UiText
import edu.watumull.presencify.core.presentation.components.dialog.DialogState
import edu.watumull.presencify.core.presentation.utils.BaseViewModel
import edu.watumull.presencify.core.presentation.validation.validateAsAcademicEndYear
import edu.watumull.presencify.core.presentation.validation.validateAsAcademicStartYear
import kotlinx.coroutines.launch

class MarkUnmarkStudentAsDropoutViewModel : BaseViewModel<MarkUnmarkStudentAsDropoutState, MarkUnmarkStudentAsDropoutEvent, MarkUnmarkStudentAsDropoutAction>(
    initialState = MarkUnmarkStudentAsDropoutState()
) {
    override fun handleAction(action: MarkUnmarkStudentAsDropoutAction) {
        when (action) {
            MarkUnmarkStudentAsDropoutAction.NavigateBack -> {
                sendEvent(MarkUnmarkStudentAsDropoutEvent.NavigateBack)
            }

            is MarkUnmarkStudentAsDropoutAction.UpdateStartYear -> {
                val startYearValidation = action.year.validateAsAcademicStartYear(endYear = state.endYear)
                updateState {
                    it.copy(
                        startYear = action.year,
                        startYearError = startYearValidation.errorMessage
                    )
                }
            }

            is MarkUnmarkStudentAsDropoutAction.UpdateEndYear -> {
                val endYearValidation = action.year.validateAsAcademicEndYear(startYear = state.startYear)
                updateState {
                    it.copy(
                        endYear = action.year,
                        endYearError = endYearValidation.errorMessage
                    )
                }
            }

            MarkUnmarkStudentAsDropoutAction.ContinueClick -> {
                viewModelScope.launch {
                    validateAndNavigate()
                }
            }

            MarkUnmarkStudentAsDropoutAction.DismissDialog -> {
                updateState { it.copy(dialogState = null) }
            }
        }
    }

    private fun validateAndNavigate() {
        val current = stateFlow.value

        val startYearValidation = current.startYear.validateAsAcademicStartYear(endYear = current.endYear)
        val endYearValidation = current.endYear.validateAsAcademicEndYear(startYear = current.startYear)

        val yearRelationError = if (startYearValidation.successful && endYearValidation.successful) {
            val startYearInt = current.startYear.toIntOrNull()
            val endYearInt = current.endYear.toIntOrNull()

            when {
                startYearInt == null || endYearInt == null -> null // already covered by validators
                endYearInt != startYearInt + 1 -> "End year must be exactly one year after start year"
                else -> null
            }
        } else {
            null
        }

        updateState {
            it.copy(
                startYearError = startYearValidation.errorMessage,
                endYearError = yearRelationError ?: endYearValidation.errorMessage
            )
        }

        if (!startYearValidation.successful || !endYearValidation.successful || yearRelationError != null) {
            return
        }

        sendEvent(
            MarkUnmarkStudentAsDropoutEvent.NavigateToSearchStudent(
                dropoutAcademicStartYear = current.startYear.toInt(),
                dropoutAcademicEndYear = current.endYear.toInt()
            )
        )
    }
}
