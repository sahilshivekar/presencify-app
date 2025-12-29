package edu.watumull.presencify.core.domain.model.student

import edu.watumull.presencify.core.domain.model.academics.Semester

data class StudentSemester(
    val id: String,
    val studentId: String,
    val semesterId: String,
    val fromDate: String,
    val toDate: String?,
    val student: Student? = null,
    val semester: Semester? = null
)