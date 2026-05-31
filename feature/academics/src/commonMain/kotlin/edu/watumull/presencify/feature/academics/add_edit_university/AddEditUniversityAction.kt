package edu.watumull.presencify.feature.academics.add_edit_university

import edu.watumull.presencify.feature.academics.add_edit_semester.AddEditSemesterAction

sealed interface AddEditUniversityAction {
    data object NavigateBack : AddEditUniversityAction
    data object ConfirmNavigateBack : AddEditUniversityAction
    data object DismissDialog : AddEditUniversityAction
    data object SubmitClick : AddEditUniversityAction

    data class UpdateName(val name: String) : AddEditUniversityAction
    data class UpdateAbbreviation(val abbreviation: String) : AddEditUniversityAction
}
