package edu.watumull.presencify.core.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class DivisionListWithTotalCountDto(
    val divisions: List<DivisionDto>,
    val totalCount: Int
)
