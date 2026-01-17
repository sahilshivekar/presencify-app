package edu.watumull.presencify.core.domain.model.student

import edu.watumull.presencify.core.domain.enums.AdmissionType
import edu.watumull.presencify.core.domain.enums.Gender
import edu.watumull.presencify.core.domain.model.academics.Branch
import edu.watumull.presencify.core.domain.model.academics.Scheme
import edu.watumull.presencify.core.domain.model.attendance.AttendanceStudent

data class Student(
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
    val branch: Branch? = null,
    val scheme: Scheme? = null,
    val studentSemesters: List<StudentSemester>? = null,
    val studentDivisions: List<StudentDivision>? = null,
    val studentBatches: List<StudentBatch>? = null,
    val attendanceStudents: List<AttendanceStudent>? = null
)
