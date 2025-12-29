package edu.watumull.presencify.core.domain.model.shedule

data class ClassListWithTotalCount(
    val classes: List<ClassSession>,
    val totalCount: Int
)