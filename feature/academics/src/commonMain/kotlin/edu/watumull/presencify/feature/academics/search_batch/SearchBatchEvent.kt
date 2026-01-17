package edu.watumull.presencify.feature.academics.search_batch

sealed interface SearchBatchEvent {
    data object NavigateBack : SearchBatchEvent
    data class NavigateToBatchDetails(val batchId: String) : SearchBatchEvent
    data object NavigateToAddEditBatch : SearchBatchEvent
}

