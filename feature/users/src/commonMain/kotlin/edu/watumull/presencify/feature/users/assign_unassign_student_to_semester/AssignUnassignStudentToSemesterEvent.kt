package edu.watumull.presencify.feature.users.assign_unassign_student_to_semester

sealed interface AssignUnassignStudentToSemesterEvent {
    data object NavigateBack : AssignUnassignStudentToSemesterEvent
    data class NavigateToSearchStudent(val semesterId: String, val branchId: String) : AssignUnassignStudentToSemesterEvent
}
