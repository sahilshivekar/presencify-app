package edu.watumull.presencify.core.domain.model.shedule

data class CancelledClassListWithTotalCount(
    val cancelledClasses: List<CancelledClass>,
    val totalCount: Int
)