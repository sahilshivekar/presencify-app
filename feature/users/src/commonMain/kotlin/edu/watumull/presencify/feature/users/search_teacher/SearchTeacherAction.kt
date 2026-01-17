package edu.watumull.presencify.feature.users.search_teacher

sealed interface SearchTeacherAction {
    data object BackButtonClick : SearchTeacherAction
    data object DismissDialog : SearchTeacherAction

    // Search & Refresh
    data class UpdateSearchQuery(val query: String) : SearchTeacherAction
    data object Search : SearchTeacherAction
    data object Refresh : SearchTeacherAction

    // Teacher Selection
    data class ToggleTeacherSelection(val teacherId: String) : SearchTeacherAction
    data class TeacherCardClick(val teacherId: String) : SearchTeacherAction

    // Pagination
    data object LoadMoreTeachers : SearchTeacherAction

    // Done button for selection mode
    data object DoneButtonClick : SearchTeacherAction

    // Floating Action Button
    data object ClickFloatingActionButton : SearchTeacherAction
}
