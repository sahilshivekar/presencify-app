package edu.watumull.presencify.feature.users.modify_student_division

sealed interface ModifyStudentDivisionEvent {
    data object NavigateBack : ModifyStudentDivisionEvent
    data class NavigateToSearchStudent(
        val divisionId: String,
        val branchId: String,
        val academicStartYear: Int,
        val academicEndYear: Int,
        val semesterNumber: Int,
        val newStartDate: String
    ) : ModifyStudentDivisionEvent
}
