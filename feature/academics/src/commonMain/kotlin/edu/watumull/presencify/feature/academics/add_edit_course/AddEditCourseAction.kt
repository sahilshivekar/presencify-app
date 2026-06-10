package edu.watumull.presencify.feature.academics.add_edit_course

import edu.watumull.presencify.core.domain.enums.CourseType

sealed interface AddEditCourseAction {
    data object NavigateBack : AddEditCourseAction
    data object DismissDialog : AddEditCourseAction
    data object ConfirmNavigateBack : AddEditCourseAction
    data object SubmitClick : AddEditCourseAction

    data class UpdateCode(val code: String) : AddEditCourseAction
    data class UpdateName(val name: String) : AddEditCourseAction
    data class UpdateOptionalCourse(val optionalCourse: String) : AddEditCourseAction
    data class UpdateSelectedScheme(val schemeId: String) : AddEditCourseAction
    data class UpdateSelectedCourseType(val courseType: CourseType) : AddEditCourseAction
    data class ChangeSchemeDropDownVisibility(val isVisible: Boolean) : AddEditCourseAction
    data class ChangeCourseTypeDropDownVisibility(val isVisible: Boolean) : AddEditCourseAction
}
