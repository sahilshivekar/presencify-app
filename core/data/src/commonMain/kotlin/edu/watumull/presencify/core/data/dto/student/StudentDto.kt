package edu.watumull.presencify.core.data.dto.student

import edu.watumull.presencify.core.data.dto.academics.BranchDto
import edu.watumull.presencify.core.data.dto.academics.SchemeDto
import edu.watumull.presencify.core.data.dto.attendance.AttendanceStudentDto
import edu.watumull.presencify.core.domain.enums.AdmissionType
import edu.watumull.presencify.core.domain.enums.Gender
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StudentDto(
    val id: String,
    val firstName: String,
    val lastName: String,
    val middleName: String?,
    val gender: Gender,
    val email: String,
    val parentEmail: String?,
    val phoneNumber: String,
    val prn: String,
    val password: String? = null,
    val rollNo: Int? = null,
    val dob: String? = null,
    val admissionYear: Int? = null,
    val admissionType: AdmissionType,
    val branchId: String,
    val schemeId: String,
    val createdAt: String,
    val updatedAt: String,
    val refreshToken: String? = null,
    val studentImageUrl: String? = null,
    val studentImgPublicId: String? = null,
    @SerialName("Branch")
    val branch: BranchDto? = null,
    @SerialName("Scheme")
    val scheme: SchemeDto? = null,
    @SerialName("StudentSemesters")
    val studentSemesters: List<StudentSemesterDto>? = null,
    @SerialName("StudentDivisions")
    val studentDivisions: List<StudentDivisionDto>? = null,
    @SerialName("StudentBatches")
    val studentBatches: List<StudentBatchDto>? = null,
    @SerialName("AttendanceStudents")
    val attendanceStudents: List<AttendanceStudentDto>? = null
)
