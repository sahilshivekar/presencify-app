package edu.watumull.presencify.feature.academics.search_semester

import androidx.compose.runtime.Stable
import edu.watumull.presencify.core.domain.enums.SemesterNumber
import edu.watumull.presencify.core.domain.model.academics.Branch
import edu.watumull.presencify.core.domain.model.academics.Scheme
import edu.watumull.presencify.core.domain.model.academics.Semester
import edu.watumull.presencify.core.presentation.UiText
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

@Stable
data class SearchSemesterState(
    val viewState: ViewState = ViewState.Content,

    // Search & Filter
    val searchQuery: String = "",
    val isRefreshing: Boolean = false,

    // Semesters List
    val semesters: PersistentList<Semester> = persistentListOf(),
    val isLoadingSemesters: Boolean = true,

    // Filter Options - Semester Number
    val semesterNumberOptions: ImmutableList<SemesterNumber> = SemesterNumber.entries.toImmutableList(),
    val selectedSemesterNumber: SemesterNumber? = null,

    // Filter Options - Academic Year
    val academicStartYear: String = "",
    val academicEndYear: String = "",

    // Filter Options - Branches
    val branchOptions: PersistentList<Branch> = persistentListOf(),
    val selectedBranches: PersistentList<Branch> = persistentListOf(),
    val areBranchesLoading: Boolean = true,

    // Filter Options - Scheme
    val schemeOptions: PersistentList<Scheme> = persistentListOf(),
    val selectedScheme: Scheme? = null,
    val areSchemesLoading: Boolean = true,

    // Pagination
    val currentPage: Int = 1,
    val isLoadingMore: Boolean = false
) {
    sealed interface ViewState {
        data object Loading : ViewState
        data class Error(val message: UiText) : ViewState
        data object Content : ViewState
    }
}


