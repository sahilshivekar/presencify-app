package edu.watumull.presencify.feature.users.import_teachers

import androidx.lifecycle.viewModelScope
import edu.watumull.presencify.core.designsystem.components.dialog.DialogType
import edu.watumull.presencify.core.domain.onError
import edu.watumull.presencify.core.domain.onSuccess
import edu.watumull.presencify.core.domain.repository.teacher.TeacherRepository
import edu.watumull.presencify.core.presentation.UiText
import edu.watumull.presencify.core.presentation.components.dialog.DialogState
import edu.watumull.presencify.core.presentation.global_snackbar.SnackbarController
import edu.watumull.presencify.core.presentation.global_snackbar.SnackbarEvent
import edu.watumull.presencify.core.presentation.toUiText
import edu.watumull.presencify.core.presentation.utils.BaseViewModel
import edu.watumull.presencify.core.presentation.utils.CsvPickerResult
import edu.watumull.presencify.core.presentation.utils.isValidCsv
import edu.watumull.presencify.core.presentation.utils.parseCsv
import edu.watumull.presencify.core.presentation.utils.toCsvString
import kotlinx.coroutines.launch

class ImportTeachersViewModel(
    private val teacherRepository: TeacherRepository
) : BaseViewModel<ImportTeachersState, ImportTeachersEvent, ImportTeachersAction>(
        initialState = ImportTeachersState()
    ) {

    override fun handleAction(action: ImportTeachersAction) {
        when (action) {
            is ImportTeachersAction.ClickBackButton -> {
                sendEvent(ImportTeachersEvent.NavigateBack)
            }
            is ImportTeachersAction.ClickSelectCsvFile -> {
                // Handled in UI
            }
             is ImportTeachersAction.CsvFileSelected -> {
                when(val result = action.result) {
                    is CsvPickerResult.Success -> {
                        val csvContent = result.csvData.toCsvString()

                        if (!csvContent.isValidCsv()) {
                             updateState { it.copy(
                                error = UiText.DynamicString("Invalid file format. Please upload a valid CSV file."),
                                selectedFile = null,
                                selectedFileName = null
                            ) }
                            return
                        }

                        val validationError = validateTeacherCsv(csvContent)

                        if (validationError != null) {
                            updateState { it.copy(
                                error = UiText.DynamicString(validationError),
                                selectedFile = null,
                                selectedFileName = null
                            ) }
                        } else {
                            updateState { it.copy(
                                selectedFile = result.csvData,
                                selectedFileName = "Selected file",
                                error = null
                            ) }
                        }
                    }
                    is CsvPickerResult.Error -> {
                         updateState { it.copy(
                             error = UiText.DynamicString(result.message)
                         ) }
                    }
                    else -> {} // Cancelled
                }
            }
            is ImportTeachersAction.ClickSubmit -> {
                importTeachers()
            }
            is ImportTeachersAction.DismissDialog -> {
                updateState { it.copy(dialogState = null) }
            }
        }
    }

    private fun validateTeacherCsv(csvContent: String): String? {
        if (csvContent.isBlank()) return "File is empty"

        val rows = csvContent.parseCsv()
        if (rows.isEmpty()) return "File is empty"

        val headers = rows.first().map { it.trim() }
        val requiredColumns = listOf(
            "firstName", "lastName", "email", "phoneNumber", "gender", "role"
        )

        val missingColumns = requiredColumns.filter { required ->
            headers.none { it.equals(required, ignoreCase = true) }
        }

        if (missingColumns.isNotEmpty()) {
            return "Missing required columns: ${missingColumns.joinToString(", ")}"
        }

        if (rows.size <= 1) {
             return "File contains no data rows"
        }

        return null
    }

    private fun importTeachers() {
        val fileData = state.selectedFile ?: return

        updateState { it.copy(isSubmitting = true, error = null) }

        viewModelScope.launch {
            teacherRepository.bulkCreateTeachersFromCSV(fileData)
                .onSuccess {
                    updateState { it.copy(isSubmitting = false, selectedFile = null, selectedFileName = null) }
                    SnackbarController.sendEvent(
                        SnackbarEvent(
                            message = "Teachers imported successfully"
                        )
                    )
                    sendEvent(ImportTeachersEvent.NavigateBack)
                }
                .onError { error ->
                    updateState { it.copy(
                        isSubmitting = false,
                        dialogState = DialogState(
                            title = UiText.DynamicString("Import Failed"),
                            message = error.toUiText(),
                            dialogType = DialogType.ERROR
                        )
                    ) }
                }
        }
    }
}
