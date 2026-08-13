package edu.watumull.presencify.feature.users.search_teacher

sealed interface SearchTeacherAction {
    data object NavigateBack : SearchTeacherAction

    data class UpdateSearchQuery(val query: String) : SearchTeacherAction
    data object Search : SearchTeacherAction
    data object Refresh : SearchTeacherAction

    data class ToggleTeacherSelection(val teacherId: String) : SearchTeacherAction
    data class TeacherCardClick(val teacherId: String) : SearchTeacherAction

    data object LoadMoreTeachers : SearchTeacherAction

    data object DoneButtonClick : SearchTeacherAction

    data object ClickFloatingActionButton : SearchTeacherAction
}
