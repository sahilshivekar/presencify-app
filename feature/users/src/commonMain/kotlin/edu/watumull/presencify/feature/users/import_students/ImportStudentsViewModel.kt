package edu.watumull.presencify.feature.users.import_students

import androidx.lifecycle.viewModelScope
import edu.watumull.presencify.core.designsystem.components.dialog.DialogType
import edu.watumull.presencify.core.domain.DataError
import edu.watumull.presencify.core.domain.onError
import edu.watumull.presencify.core.domain.onSuccess
import edu.watumull.presencify.core.domain.repository.student.StudentRepository
import edu.watumull.presencify.core.presentation.UiText
import edu.watumull.presencify.core.presentation.global_snackbar.SnackbarController
import edu.watumull.presencify.core.presentation.global_snackbar.SnackbarEvent
import edu.watumull.presencify.core.presentation.toUiText
import edu.watumull.presencify.core.presentation.utils.BaseViewModel
import edu.watumull.presencify.core.presentation.utils.CsvPickerResult
import edu.watumull.presencify.core.presentation.utils.MimeType
import edu.watumull.presencify.core.presentation.utils.ShareFileModel
import edu.watumull.presencify.core.presentation.utils.ShareUtils
import kotlinx.coroutines.launch

class ImportStudentsViewModel(
    private val studentRepository: StudentRepository
) : BaseViewModel<ImportStudentsState, ImportStudentsEvent, ImportStudentsAction>(
    initialState = ImportStudentsState()
) {

    override fun handleAction(action: ImportStudentsAction) {
        when (action) {
            is ImportStudentsAction.ClickBackButton -> {
                sendEvent(ImportStudentsEvent.NavigateBack)
            }

            is ImportStudentsAction.ClickSelectCsvFile -> {
                // Handled in UI via launch, or we can't trigger UI picker from VM easily
            }

            is ImportStudentsAction.CsvFileSelected -> {
                when (val result = action.result) {
                    is CsvPickerResult.Success -> {
                        updateState {
                            it.copy(
                                selectedFile = result.csvData,
                                selectedFileName = "Selected file",
                                error = null
                            )
                        }
                    }

                    is CsvPickerResult.Error -> {
                        updateState {
                            it.copy(
                                error = UiText.DynamicString(result.message)
                            )
                        }
                    }

                    else -> {} // Cancelled
                }
            }

            is ImportStudentsAction.ClickSubmit -> {
                importStudents()
            }

            is ImportStudentsAction.ClickDownloadSampleFiles -> {
                downloadSampleFiles()
            }

            is ImportStudentsAction.DismissDialog -> {
                updateState { it.copy(dialogState = null) }
            }
        }
    }

    private fun downloadSampleFiles() {
        val sampleCsv =
            """prn,firstName,middleName,lastName,email,phoneNumber,gender,dob,scheme,admissionYear,admissionType,branch,parentEmail
PRN2023111,John,William,Doe,john.doe@example.com,="9876543210",Male,="2003/05/15",REV-2019 'C' Scheme,2023,FE,Computer Engineering,parent.john@example.com
PRN2023222,Jane,,Smith,jane.smith@example.com,="1234567890",Female,="2004/08/20",REV-2019 'C' Scheme,2023,DSE,Computer Engineering,
PRN2024333,Alex,Kumar,Johnson,alex.j@example.com,="5556667777",Other,,REV-2019 'C' Scheme,2024,FE,Civil Engineering,""".trimIndent()

        val rulesTxt = """Student Import CSV Validation Rules:

prn: Required string, max 100 characters.
firstName: Required string, 1-100 characters.
middleName: Optional string.
lastName: Required string, 1-100 characters.
email: Required string, must be a valid email format.
phoneNumber: Required string.
gender: Required string, must be one of 'Male', 'Female', or 'Other'.
dob: Optional string.
scheme: Required string. (it must be a scheme name that already exists in the system, e.g. "REV-2019 'C' Scheme", "NEP-2020 Scheme")
admissionYear: Required number between 1900 and 3000.
admissionType: Required string, must be 'DSE' or 'FE'.
branch: Required string. (it must be branch name that already exists in the system, e.g. "Computer Engineering", "Civil Engineering")
parentEmail: Optional string, must be a valid email format if provided.""".trimIndent()

        viewModelScope.launch {

            // Using ShareUtils to export these to the device
            ShareUtils.shareFile(
                ShareFileModel(
                    mime = MimeType.CSV,
                    fileName = "sample_students.csv",
                    bytes = sampleCsv.encodeToByteArray()
                )
            )

            ShareUtils.shareFile(
                ShareFileModel(
                    mime = MimeType.TEXT,
                    fileName = "student_import_rules.txt",
                    bytes = rulesTxt.encodeToByteArray()
                )
            )
        }
    }

    private fun importStudents() {
        val fileData = state.selectedFile ?: return

        updateState { it.copy(isSubmitting = true, error = null, businessErrorText = null) }

        viewModelScope.launch {
            studentRepository.bulkCreateStudentsFromCSV(fileData)
                .onSuccess {
                    updateState { it.copy(isSubmitting = false, selectedFile = null, selectedFileName = null) }
                    SnackbarController.sendEvent(
                        SnackbarEvent(
                            message = "Students imported successfully"
                        )
                    )
                    sendEvent(ImportStudentsEvent.NavigateBack)
                }
                .onError { error ->
                    if (error is DataError.Remote.BusinessLogicError) {
                        updateState {
                            it.copy(
                                isSubmitting = false,
                                businessErrorText = error.message
                            )
                        }
                    } else {
                        updateState {
                            it.copy(
                                isSubmitting = false,
                                dialogState = ImportStudentsState.DialogState(
                                    isVisible = true,
                                    title = UiText.DynamicString("Import Failed"),
                                    message = error.toUiText(),
                                    dialogType = DialogType.ERROR,
                                    dialogIntention = ImportStudentsState.DialogIntention.GENERIC
                                )
                            )
                        }
                    }
                }
        }
    }
}
