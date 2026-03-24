package edu.watumull.presencify.feature.users.import_students

import edu.watumull.presencify.core.presentation.utils.CsvPickerResult

sealed interface ImportStudentsAction {
    data object ClickBackButton : ImportStudentsAction
    data object ClickSelectCsvFile : ImportStudentsAction
    data class CsvFileSelected(val result: CsvPickerResult) : ImportStudentsAction
    data object ClickSubmit : ImportStudentsAction
    data object DismissDialog : ImportStudentsAction
}
