package edu.watumull.presencify.feature.users.mark_unmark_student_dropout

import androidx.lifecycle.viewModelScope
import edu.watumull.presencify.core.design.systems.components.dialog.DialogType
import edu.watumull.presencify.core.presentation.UiText
import edu.watumull.presencify.core.presentation.utils.BaseViewModel
import kotlinx.coroutines.launch

class MarkUnmarkStudentAsDropoutViewModel : BaseViewModel<MarkUnmarkStudentAsDropoutState, MarkUnmarkStudentAsDropoutEvent, MarkUnmarkStudentAsDropoutAction>(
    initialState = MarkUnmarkStudentAsDropoutState()
) {
    override fun handleAction(action: MarkUnmarkStudentAsDropoutAction) {
        when (action) {
            MarkUnmarkStudentAsDropoutAction.BackButtonClick -> {
                sendEvent(MarkUnmarkStudentAsDropoutEvent.NavigateBack)
            }

            is MarkUnmarkStudentAsDropoutAction.UpdateStartYear -> {
                updateState {
                    it.copy(
                        startYear = action.year,
                        startYearError = validateYear(action.year, "Start Year")
                    )
                }
            }

            is MarkUnmarkStudentAsDropoutAction.UpdateEndYear -> {
                updateState {
                    it.copy(
                        endYear = action.year,
                        endYearError = validateYear(action.year, "End Year")
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

    private fun validateYear(year: String, fieldName: String): String? {
        return when {
            year.isBlank() -> "$fieldName is required"
            year.toIntOrNull() == null -> "$fieldName must be a valid number"
            year.length != 4 -> "$fieldName must be 4 digits"
            year.toInt() < 2000 || year.toInt() > 2100 -> "$fieldName must be between 2000 and 2100"
            else -> null
        }
    }

    private fun validateAndNavigate() {
        val state = stateFlow.value

        // Validate both years
        val startYearError = validateYear(state.startYear, "Start Year")
        val endYearError = validateYear(state.endYear, "End Year")

        // Check if end year is exactly start year + 1
        val yearRelationError = if (startYearError == null && endYearError == null) {
            val startYearInt = state.startYear.toInt()
            val endYearInt = state.endYear.toInt()

            when {
                endYearInt != startYearInt + 1 -> "End year must be exactly one year after start year"
                else -> null
            }
        } else null

        // Update errors
        updateState {
            it.copy(
                startYearError = startYearError,
                endYearError = yearRelationError ?: endYearError
            )
        }

        // If there are any errors, show dialog
        if (startYearError != null || endYearError != null || yearRelationError != null) {
            updateState {
                it.copy(
                    dialogState = MarkUnmarkStudentAsDropoutState.DialogState(
                        dialogType = DialogType.ERROR,
                        title = "Validation Error",
                        message = UiText.DynamicString(
                            startYearError ?: endYearError ?: yearRelationError ?: "Please fix the errors"
                        ),
                        dialogIntention = DialogIntention.GENERIC
                    )
                )
            }
            return
        }

        // All validation passed, navigate
        sendEvent(
            MarkUnmarkStudentAsDropoutEvent.NavigateToSearchStudent(
                dropoutAcademicStartYear = state.startYear.toInt(),
                dropoutAcademicEndYear = state.endYear.toInt()
            )
        )
    }
}
