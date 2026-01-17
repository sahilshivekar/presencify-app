package edu.watumull.presencify.core.domain.model.student

import edu.watumull.presencify.core.domain.model.academics.Division
import kotlinx.datetime.LocalDate

data class StudentDivision(
    val id: String,
    val studentId: String,
    val divisionId: String,
    val startDate: LocalDate,
    val endDate: LocalDate?,
    val student: Student? = null,
    val division: Division? = null
)
