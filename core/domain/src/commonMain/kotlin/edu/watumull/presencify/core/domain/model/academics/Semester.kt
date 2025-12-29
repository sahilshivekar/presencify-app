package edu.watumull.presencify.core.domain.model.academics

import edu.watumull.presencify.core.domain.model.student.StudentSemester

data class Semester(
    val id: String,
    val branchId: String,
    val semesterNumber: Int,
    val academicStartYear: Int,
    val academicEndYear: Int,
    val startDate: String,
    val endDate: String,
    val schemeId: String,
    val branch: Branch? = null,
    val scheme: Scheme? = null,
    val divisions: List<Division>? = null,
    val studentSemesters: List<StudentSemester>? = null,
    val courses: List<Course>? = null
)
