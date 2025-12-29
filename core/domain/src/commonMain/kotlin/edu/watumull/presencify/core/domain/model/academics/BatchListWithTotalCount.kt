package edu.watumull.presencify.core.domain.model.academics

data class BatchListWithTotalCount(
    val batches: List<Batch>,
    val totalCount: Int
)