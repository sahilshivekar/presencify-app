package edu.watumull.presencify.feature.users.mark_unmark_student_dropout

sealed interface MarkUnmarkStudentAsDropoutEvent {
    data object NavigateBack : MarkUnmarkStudentAsDropoutEvent

    data class NavigateToSearchStudent(
        val dropoutAcademicStartYear: Int,
        val dropoutAcademicEndYear: Int
    ) : MarkUnmarkStudentAsDropoutEvent
}
