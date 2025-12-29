package edu.watumull.presencify.core.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class CancelledClassListWithTotalCountDto(
    val cancelledClasses: List<CancelledClassDto>,
    val totalCount: Int
)
