package edu.watumull.presencify.core.domain.model.student

import edu.watumull.presencify.core.domain.model.academics.Division

data class StudentDivision(
    val id: String,
    val studentId: String,
    val divisionId: String,
    val fromDate: String,
    val toDate: String?,
    val student: Student? = null,
    val division: Division? = null
)
