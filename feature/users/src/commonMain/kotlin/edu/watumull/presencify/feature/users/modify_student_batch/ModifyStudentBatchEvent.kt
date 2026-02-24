package edu.watumull.presencify.feature.users.modify_student_batch

sealed interface ModifyStudentBatchEvent {
    data object NavigateBack : ModifyStudentBatchEvent
    data class NavigateToSearchStudent(
        val batchId: String,
        val branchId: String,
        val academicStartYear: Int,
        val academicEndYear: Int,
        val semesterNumber: Int,
        val newStartDate: String
    ) : ModifyStudentBatchEvent
}
