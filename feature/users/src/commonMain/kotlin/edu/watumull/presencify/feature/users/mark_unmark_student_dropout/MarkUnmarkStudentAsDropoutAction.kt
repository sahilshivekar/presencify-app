package edu.watumull.presencify.feature.users.mark_unmark_student_dropout

sealed interface MarkUnmarkStudentAsDropoutAction {
    data object BackButtonClick : MarkUnmarkStudentAsDropoutAction

    data class UpdateStartYear(val year: String) : MarkUnmarkStudentAsDropoutAction
    data class UpdateEndYear(val year: String) : MarkUnmarkStudentAsDropoutAction

    data object ContinueClick : MarkUnmarkStudentAsDropoutAction

    data object DismissDialog : MarkUnmarkStudentAsDropoutAction
}
