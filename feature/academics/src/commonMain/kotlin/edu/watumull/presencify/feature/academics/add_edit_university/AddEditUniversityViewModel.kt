package edu.watumull.presencify.feature.academics.add_edit_university

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import edu.watumull.presencify.core.designsystem.components.dialog.DialogType
import edu.watumull.presencify.core.domain.onError
import edu.watumull.presencify.core.domain.onSuccess
import edu.watumull.presencify.core.domain.repository.academics.UniversityRepository
import edu.watumull.presencify.core.presentation.UiText
import edu.watumull.presencify.core.presentation.components.dialog.DialogState
import edu.watumull.presencify.core.presentation.global_snackbar.SnackbarController
import edu.watumull.presencify.core.presentation.global_snackbar.SnackbarEvent
import edu.watumull.presencify.core.presentation.toUiText
import edu.watumull.presencify.core.presentation.utils.BaseViewModel
import edu.watumull.presencify.core.presentation.validation.validateAsUniversityAbbreviation
import edu.watumull.presencify.core.presentation.validation.validateAsUniversityName
import edu.watumull.presencify.feature.academics.navigation.AcademicsRoutes
import kotlinx.coroutines.launch

class AddEditUniversityViewModel(
    private val universityRepository: UniversityRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<AddEditUniversityState, AddEditUniversityEvent, AddEditUniversityAction>(
    initialState = AddEditUniversityState(
        universityId = savedStateHandle.toRoute<AcademicsRoutes.AddEditUniversity>().universityId,
        isEditMode = savedStateHandle.toRoute<AcademicsRoutes.AddEditUniversity>().universityId != null
    )
) {

    init {
        viewModelScope.launch {
            state.universityId?.let { id -> loadUniversity(id) }
        }
    }

    private suspend fun loadUniversity(id: String) {
        updateState { it.copy(isLoading = true) }

        universityRepository.getUniversityById(id)
            .onSuccess { university ->
                updateState {
                    it.copy(
                        isLoading = false,
                        name = university.name,
                        abbreviation = university.abbreviation
                    )
                }
            }
            .onError { error ->
                updateState {
                    it.copy(
                        isLoading = false,
                        dialogState = DialogState(
                            dialogType = DialogType.ERROR,
                            title = UiText.DynamicString("Error Loading University"),
                            message = error.toUiText()
                        )
                    )
                }
            }
    }

    override fun handleAction(action: AddEditUniversityAction) {
        when (action) {
            is AddEditUniversityAction.NavigateBack -> handleBackNavigation()
            is AddEditUniversityAction.ConfirmNavigateBack -> confirmNavigateBack()
            is AddEditUniversityAction.DismissDialog -> updateState { it.copy(dialogState = null) }
            is AddEditUniversityAction.UpdateName -> updateState { it.copy(name = action.name, nameError = null) }
            is AddEditUniversityAction.UpdateAbbreviation -> updateState {
                it.copy(
                    abbreviation = action.abbreviation,
                    abbreviationError = null
                )
            }

            is AddEditUniversityAction.SubmitClick -> {
                viewModelScope.launch { submitForm() }
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
            sendEvent(AddEditUniversityEvent.NavigateBack)
        }
    }

    private fun confirmNavigateBack() {
        updateState { it.copy(dialogState = null) }
        sendEvent(AddEditUniversityEvent.NavigateBack)
    }

    private fun hasUnsavedChanges(): Boolean {
        return state.name.isNotBlank() || state.abbreviation.isNotBlank()
    }

    private fun validateForm(): Boolean {
        val nameValidation = state.name.validateAsUniversityName()
        val abbreviationValidation = state.abbreviation.validateAsUniversityAbbreviation()

        updateState {
            it.copy(
                nameError = nameValidation.errorMessage,
                abbreviationError = abbreviationValidation.errorMessage
            )
        }

        return nameValidation.successful && abbreviationValidation.successful
    }

    private suspend fun submitForm() {
        if (!validateForm()) return

        updateState { it.copy(isSubmitting = true) }

        val result = if (state.isEditMode && state.universityId != null) {
            val id = state.universityId!!
            universityRepository.updateUniversity(
                id = id,
                name = state.name,
                abbreviation = state.abbreviation
            )
        } else {
            universityRepository.addUniversity(
                name = state.name,
                abbreviation = state.abbreviation
            )
        }

        result
            .onSuccess {
                updateState { it.copy(isSubmitting = false) }
                viewModelScope.launch {
                    SnackbarController.sendEvent(
                        SnackbarEvent(message = if (state.isEditMode) "University updated successfully" else "University added successfully")
                    )
                }
                sendEvent(AddEditUniversityEvent.NavigateBack)
            }
            .onError { error ->
                updateState {
                    it.copy(
                        isSubmitting = false,
                        dialogState = DialogState(
                            dialogType = DialogType.ERROR,
                            title = UiText.DynamicString(if (state.isEditMode) "Error Updating University" else "Error Adding University"),
                            message = error.toUiText()
                        )
                    )
                }
            }
    }
}
