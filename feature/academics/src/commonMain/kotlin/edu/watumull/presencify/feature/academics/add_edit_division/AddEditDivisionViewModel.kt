package edu.watumull.presencify.feature.academics.add_edit_division

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import edu.watumull.presencify.core.designsystem.components.dialog.DialogType
import edu.watumull.presencify.core.domain.enums.SemesterNumber
import edu.watumull.presencify.core.domain.onError
import edu.watumull.presencify.core.domain.onSuccess
import edu.watumull.presencify.core.domain.repository.academics.BranchRepository
import edu.watumull.presencify.core.domain.repository.academics.CourseRepository
import edu.watumull.presencify.core.domain.repository.academics.DivisionRepository
import edu.watumull.presencify.core.domain.repository.academics.SemesterRepository
import edu.watumull.presencify.core.presentation.UiText
import edu.watumull.presencify.core.presentation.components.dialog.DialogState
import edu.watumull.presencify.core.presentation.global_snackbar.SnackbarController
import edu.watumull.presencify.core.presentation.global_snackbar.SnackbarEvent
import edu.watumull.presencify.core.presentation.toUiText
import edu.watumull.presencify.core.presentation.utils.BaseViewModel
import edu.watumull.presencify.core.presentation.validation.validateAsAcademicEndYear
import edu.watumull.presencify.core.presentation.validation.validateAsAcademicStartYear
import edu.watumull.presencify.core.presentation.validation.validateAsDivisionCode
import edu.watumull.presencify.core.presentation.validation.validateAsSemesterNumber
import edu.watumull.presencify.core.presentation.validation.validateAsUUID
import edu.watumull.presencify.feature.academics.navigation.AcademicsRoutes
import kotlinx.coroutines.launch

class AddEditDivisionViewModel(
    private val divisionRepository: DivisionRepository,
    private val semesterRepository: SemesterRepository,
    private val branchRepository: BranchRepository,
    private val courseRepository: CourseRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<AddEditDivisionState, AddEditDivisionEvent, AddEditDivisionAction>(
    initialState = AddEditDivisionState(
        divisionId = savedStateHandle.toRoute<AcademicsRoutes.AddEditDivision>().divisionId,
        isEditMode = savedStateHandle.toRoute<AcademicsRoutes.AddEditDivision>().divisionId != null,
        isLoadingDivision = savedStateHandle.toRoute<AcademicsRoutes.AddEditDivision>().divisionId != null
    )
) {

    init {
        viewModelScope.launch {
            loadBranches()
            val divisionId = state.divisionId
            if (state.isEditMode && divisionId != null) {
                loadDivisionForEdit(divisionId)
            }
        }
    }

    private suspend fun loadDivisionForEdit(divisionId: String) {
        updateState { it.copy(isLoadingDivision = true) }

        divisionRepository.getDivisionById(divisionId)
            .onSuccess { division ->
                semesterRepository.getSemesterById(division.semesterId)
                    .onSuccess { semester ->
                        loadOptionalCourses(
                            semesterNumber = semester.semesterNumber,
                            branchId = semester.branchId,
                            schemeId = semester.schemeId,
                            divisionId = division.id
                        )
                        updateState {
                            it.copy(
                                isLoadingDivision = false,
                                divisionCode = division.divisionCode,
                                foundSemester = semester,
                                showDivisionInput = true
                            )
                        }
                    }
                    .onError { error ->
                        updateState {
                            it.copy(
                                isLoadingDivision = false,
                                dialogState = DialogState(
                                    dialogType = DialogType.ERROR,
                                    title = UiText.DynamicString("Error Loading Semester"),
                                    message = error.toUiText()
                                )
                            )
                        }
                    }
            }
            .onError { error ->
                updateState {
                    it.copy(
                        isLoadingDivision = false,
                        dialogState = DialogState(
                            dialogType = DialogType.ERROR,
                            title = UiText.DynamicString("Error Loading Division"),
                            message = error.toUiText()
                        )
                    )
                }
            }
    }

    private suspend fun loadBranches() {
        branchRepository.getBranches()
            .onSuccess { branches ->
                updateState { it.copy(branchOptions = branches) }
            }
            .onError { error ->
                updateState {
                    it.copy(
                        dialogState = DialogState(
                            dialogType = DialogType.ERROR,
                            title = UiText.DynamicString("Error Loading Branches"),
                            message = error.toUiText()
                        )
                    )
                }
            }
    }

    override fun handleAction(action: AddEditDivisionAction) {
        when (action) {
            is AddEditDivisionAction.NavigateBack -> handleBackNavigation()
            is AddEditDivisionAction.ConfirmNavigateBack -> confirmNavigateBack()
            is AddEditDivisionAction.DismissDialog -> updateState { it.copy(dialogState = null) }
            is AddEditDivisionAction.UpdateSemesterNumber -> updateState { it.copy(semesterNumber = action.semesterNumber, semesterNumberError = null, isSemesterNumberDropdownOpen = false) }
            is AddEditDivisionAction.UpdateAcademicStartYear -> updateState { it.copy(academicStartYear = action.year, academicStartYearError = null) }
            is AddEditDivisionAction.UpdateAcademicEndYear -> updateState { it.copy(academicEndYear = action.year, academicEndYearError = null) }
            is AddEditDivisionAction.UpdateSelectedBranch -> updateState { it.copy(selectedBranchId = action.branchId, branchError = null, isBranchDropdownOpen = false) }
            is AddEditDivisionAction.UpdateDivisionCode -> updateState { it.copy(divisionCode = action.code, divisionCodeError = null) }
            is AddEditDivisionAction.SelectOptionalCourse -> {
                updateState {
                    it.copy(
                        selectedOptionalCourses = it.selectedOptionalCourses + (action.optionalCourse to action.courseId),
                        openOptionalDropdowns = it.openOptionalDropdowns - action.optionalCourse
                    )
                }
            }
            is AddEditDivisionAction.ChangeSemesterNumberDropDownVisibility -> updateState { it.copy(isSemesterNumberDropdownOpen = action.isVisible) }
            is AddEditDivisionAction.ChangeBranchDropDownVisibility -> updateState { it.copy(isBranchDropdownOpen = action.isVisible) }
            is AddEditDivisionAction.ChangeOptionalCourseDropdownVisibility -> {
                updateState {
                    val newSet = if (action.isVisible) {
                        it.openOptionalDropdowns + action.optionalCourse
                    } else {
                        it.openOptionalDropdowns - action.optionalCourse
                    }
                    it.copy(openOptionalDropdowns = newSet)
                }
            }
            is AddEditDivisionAction.FindSemesterClick -> { viewModelScope.launch { findSemester() } }
            is AddEditDivisionAction.SubmitClick -> { viewModelScope.launch { submitForm() } }
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
            sendEvent(AddEditDivisionEvent.NavigateBack)
        }
    }

    private fun confirmNavigateBack() {
        updateState { it.copy(dialogState = null) }
        sendEvent(AddEditDivisionEvent.NavigateBack)
    }

    private fun hasUnsavedChanges(): Boolean {
        return state.divisionCode.isNotBlank() || state.foundSemester != null || state.selectedOptionalCourses.isNotEmpty()
    }

    private suspend fun findSemester() {
        if (!validateSemesterInputs()) return

        updateState { it.copy(isLoading = true) }

        val startYear = state.academicStartYear.toIntOrNull() ?: return
        val endYear = state.academicEndYear.toIntOrNull() ?: return
        val semNum = state.semesterNumber ?: return

        semesterRepository.getSemesters(
            semesterNumber = semNum,
            academicStartYear = startYear,
            academicEndYear = endYear,
            branchId = state.selectedBranchId,
            getAll = true
        )
            .onSuccess { result ->
                val semesters = result.semesters
                if (semesters.isNotEmpty()) {
                    val semester = semesters[0]
                    updateState { it.copy(isLoading = false, foundSemester = semester, showDivisionInput = true, divisionCodeError = null) }
                    loadOptionalCourses(
                        semesterNumber = semester.semesterNumber,
                        branchId = semester.branchId,
                        schemeId = semester.schemeId
                    )
                } else {
                    updateState {
                        it.copy(
                            isLoading = false,
                            dialogState = DialogState(
                                dialogType = DialogType.INFO,
                                title = UiText.DynamicString("No Semester Found"),
                                message = UiText.DynamicString("No semester found with the selected criteria. Please check your inputs.")
                            )
                        )
                    }
                }
            }
            .onError { error ->
                updateState {
                    it.copy(
                        isLoading = false,
                        dialogState = DialogState(
                            dialogType = DialogType.ERROR,
                            title = UiText.DynamicString("Error Finding Semester"),
                            message = error.toUiText()
                        )
                    )
                }
            }
    }

    private fun validateSemesterInputs(): Boolean {
        val semesterValidation = state.semesterNumber.validateAsSemesterNumber()
        val startYearValidation = state.academicStartYear.validateAsAcademicStartYear(endYear = state.academicEndYear)
        val endYearValidation = state.academicEndYear.validateAsAcademicEndYear(startYear = state.academicStartYear)

        val branchValidation = state.selectedBranchId.validateAsUUID()

        updateState {
            it.copy(
                semesterNumberError = semesterValidation.errorMessage,
                academicStartYearError = startYearValidation.errorMessage,
                academicEndYearError = endYearValidation.errorMessage,
                branchError = branchValidation.errorMessage
            )
        }

        return semesterValidation.successful &&
                startYearValidation.successful &&
                endYearValidation.successful &&
                branchValidation.successful
    }

    private fun validateDivisionCode(): Boolean {
        val codeError = state.divisionCode.validateAsDivisionCode().errorMessage
        updateState { it.copy(divisionCodeError = codeError) }
        return codeError == null
    }

    private suspend fun loadOptionalCourses(
        semesterNumber: SemesterNumber,
        branchId: String,
        schemeId: String,
        divisionId: String? = null
    ) {
        updateState { it.copy(isFetchingOptionalCourses = true) }

        courseRepository.getCourses(
            semesterNumber = semesterNumber,
            branchId = branchId,
            schemeId = schemeId,
            onlyOptional = true,
            getAll = true
        )
            .onSuccess { coursesResult ->
                val groupedCourses = coursesResult.courses
                    .filter { it.optionalCourse != null }
                    .groupBy { it.optionalCourse!! }

                if (divisionId == null) {
                    updateState {
                        it.copy(
                            isFetchingOptionalCourses = false,
                            optionalCourseGroups = groupedCourses,
                            selectedOptionalCourses = emptyMap(),
                            openOptionalDropdowns = emptySet()
                        )
                    }
                } else {
                    divisionRepository.getCoursesOfDivision(divisionId)
                        .onSuccess { divisionCourses ->
                            updateState {
                                it.copy(
                                    isFetchingOptionalCourses = false,
                                    optionalCourseGroups = groupedCourses,
                                    selectedOptionalCourses = divisionCourses.optionalCourses
                                        .mapNotNull { it.course }
                                        .filter { it.optionalCourse != null }
                                        .associate { course ->
                                            course.optionalCourse!! to course.id
                                        },
                                    openOptionalDropdowns = emptySet()
                                )
                            }
                        }
                        .onError { error ->
                            updateState {
                                it.copy(
                                    isFetchingOptionalCourses = false,
                                    dialogState = DialogState(
                                        dialogType = DialogType.ERROR,
                                        title = UiText.DynamicString("Error Loading Division Courses"),
                                        message = error.toUiText()
                                    )
                                )
                            }
                        }
                }
            }
            .onError { error ->
                updateState {
                    it.copy(
                        isFetchingOptionalCourses = false,
                        dialogState = DialogState(
                            dialogType = DialogType.ERROR,
                            title = UiText.DynamicString("Error Loading Optional Courses"),
                            message = error.toUiText()
                        )
                    )
                }
            }
    }

    private suspend fun submitForm() {
        if (!validateDivisionCode() || state.foundSemester == null) return

        updateState { it.copy(isSubmitting = true) }

        val divisionId = state.divisionId
        val optionalCourseIds = if (state.selectedOptionalCourses.isNotEmpty()) {
            state.selectedOptionalCourses.values.toList()
        } else {
            null
        }
        val result = if (state.isEditMode && divisionId != null) {
            divisionRepository.updateDivision(
                id = divisionId,
                divisionCode = state.divisionCode,
                optionalCourseIds = optionalCourseIds
            )
        } else {
            divisionRepository.addDivision(
                divisionCode = state.divisionCode,
                semesterId = state.foundSemester!!.id,
                optionalCourseIds = optionalCourseIds
            )
        }

        result
            .onSuccess {
                updateState { it.copy(isSubmitting = false) }
                viewModelScope.launch {
                    SnackbarController.sendEvent(
                        SnackbarEvent(
                            message = if (state.isEditMode) "Division updated successfully" else "Division added successfully"
                        )
                    )
                }
                sendEvent(AddEditDivisionEvent.NavigateBack)
            }
            .onError { error ->
                updateState {
                    it.copy(
                        isSubmitting = false,
                        dialogState = DialogState(
                            dialogType = DialogType.ERROR,
                            title = UiText.DynamicString(if (state.isEditMode) "Error Updating Division" else "Error Adding Division"),
                            message = error.toUiText()
                        )
                    )
                }
            }
    }
}
