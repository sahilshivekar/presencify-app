package edu.watumull.presencify.feature.users.import_teachers

import edu.watumull.presencify.core.presentation.utils.CsvPickerResult

sealed interface ImportTeachersAction {
    data object ClickBackButton : ImportTeachersAction
    data object ClickSelectCsvFile : ImportTeachersAction
    data class CsvFileSelected(val result: CsvPickerResult) : ImportTeachersAction
    data object ClickSubmit : ImportTeachersAction
    data object DismissDialog : ImportTeachersAction
}
