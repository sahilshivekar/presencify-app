package edu.watumull.presencify.core.domain.model.academics

data class SemesterListWithTotalCount(
    val semesters: List<Semester>,
    val totalCount: Int
)