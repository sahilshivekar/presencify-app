package edu.watumull.presencify.feature.academics.search_batch

import edu.watumull.presencify.core.domain.enums.SemesterNumber
import edu.watumull.presencify.core.domain.model.academics.Branch
import edu.watumull.presencify.core.domain.model.academics.Division

sealed interface SearchBatchAction {
    data object NavigateBack : SearchBatchAction


    data class UpdateSearchQuery(val query: String) : SearchBatchAction
    data object Search : SearchBatchAction
    data object Refresh : SearchBatchAction

    data class SelectSemesterNumber(val semesterNumber: SemesterNumber?) : SearchBatchAction
    data class UpdateAcademicStartYear(val year: String) : SearchBatchAction
    data class UpdateAcademicEndYear(val year: String) : SearchBatchAction
    data class SelectBranch(val branch: Branch?) : SearchBatchAction
    data class SelectDivision(val division: Division?) : SearchBatchAction

    data object ResetFilters : SearchBatchAction
    data object ApplyFilters : SearchBatchAction

    data class BatchCardClick(val batchId: String) : SearchBatchAction

    data object LoadMoreBatches : SearchBatchAction

    data object ClickFloatingActionButton : SearchBatchAction
}

