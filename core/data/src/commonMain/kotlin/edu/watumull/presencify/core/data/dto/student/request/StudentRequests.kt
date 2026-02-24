package edu.watumull.presencify.core.data.dto.student.request

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class UpdateStudentDetailsRequest(
    val id: String,
    val firstName: String? = null,
    val middleName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val gender: String? = null,
    val phoneNumber: String? = null,
    val dob: String? = null,
    val schemeId: String? = null,
    val branchId: String? = null,
    val parentEmail: String? = null,
    val prn: String? = null,
    val admissionYear: String? = null,
    val admissionType: String? = null
)

@Serializable
data class UpdateStudentPasswordRequest(
    val id: String,
    val password: String,
    val confirmPassword: String
)

@Serializable
data class AddStudentToSemesterRequest(
    val studentId: String,
    val semesterId: String
)

@Serializable
data class AddStudentToDivisionRequest(
    val divisionId: String,
    val studentId: String
)

@Serializable
data class ChangeStudentDivisionRequest(
    val studentDivisionId: String,
    val divisionId: String,
    val newDivisionStartDate: LocalDate
)

@Serializable
data class ChangeStudentBatchRequest(
    val studentBatchId: String,
    val batchId: String,
    val newBatchStartDate: LocalDate
)

@Serializable
data class RevertAddStudentToDivisionRequest(
    val studentDivisionId: String
)
