package edu.watumull.presencify.feature.academics.add_edit_scheme

sealed interface AddEditSchemeAction {
    data object NavigateBack : AddEditSchemeAction
    data object DismissDialog : AddEditSchemeAction
    data object ConfirmNavigateBack : AddEditSchemeAction
    data object SubmitClick : AddEditSchemeAction

    data class UpdateName(val name: String) : AddEditSchemeAction
    data class UpdateSelectedUniversity(val universityId: String) : AddEditSchemeAction
    data class ChangeUniversityDropDownVisibility(val isVisible: Boolean) : AddEditSchemeAction
}
