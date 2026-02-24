package edu.watumull.presencify.feature.users.assign_unassign_student_to_division

sealed interface AssignUnassignStudentToDivisionEvent {
    data object NavigateBack : AssignUnassignStudentToDivisionEvent
    data class NavigateToSearchStudent(
        val divisionId: String,
        val branchId: String,
        val academicStartYear: Int,
        val academicEndYear: Int,
        val semesterNumber: Int
    ) : AssignUnassignStudentToDivisionEvent
}
