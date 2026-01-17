package edu.watumull.presencify.feature.academics.search_batch

import edu.watumull.presencify.core.domain.enums.SemesterNumber
import edu.watumull.presencify.core.domain.model.academics.Branch
import edu.watumull.presencify.core.domain.model.academics.Division

sealed interface SearchBatchAction {
    data object BackButtonClick : SearchBatchAction
    data object DismissDialog : SearchBatchAction

    data object ShowBottomSheet: SearchBatchAction
    data object HideBottomSheet: SearchBatchAction

    // Search & Refresh
    data class UpdateSearchQuery(val query: String) : SearchBatchAction
    data object Search : SearchBatchAction
    data object Refresh : SearchBatchAction

    // Filters
    data class SelectSemesterNumber(val semesterNumber: SemesterNumber?) : SearchBatchAction
    data class SelectAcademicYear(val year: String?) : SearchBatchAction
    data class SelectBranch(val branch: Branch?) : SearchBatchAction
    data class SelectDivision(val division: Division?) : SearchBatchAction

    data object ResetFilters : SearchBatchAction
    data object ApplyFilters : SearchBatchAction

    // Batch Card Click
    data class BatchCardClick(val batchId: String) : SearchBatchAction

    // Pagination
    data object LoadMoreBatches : SearchBatchAction

    data object ClickFloatingActionButton : SearchBatchAction
}

