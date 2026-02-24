package edu.watumull.presencify.feature.users.assign_unassign_student_to_batch

sealed interface AssignUnassignStudentToBatchEvent {
    data object NavigateBack : AssignUnassignStudentToBatchEvent
    data class NavigateToSearchStudent(
        val batchId: String,
        val branchId: String,
        val academicStartYear: Int,
        val academicEndYear: Int,
        val semesterNumber: Int
    ) : AssignUnassignStudentToBatchEvent
}
