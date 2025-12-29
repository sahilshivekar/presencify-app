package edu.watumull.presencify.core.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class BatchListWithTotalCountDto(
    val batches: List<BatchDto>,
    val totalCount: Int
)
