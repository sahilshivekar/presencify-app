package edu.watumull.presencify.feature.academics.semester_details

sealed interface SemesterDetailsAction {
    data object NavigateBack : SemesterDetailsAction
    data object DismissDialog : SemesterDetailsAction
    data object RemoveSemesterClick : SemesterDetailsAction
    data object ConfirmRemoveSemester : SemesterDetailsAction
    data object EditSemesterClick : SemesterDetailsAction
}

