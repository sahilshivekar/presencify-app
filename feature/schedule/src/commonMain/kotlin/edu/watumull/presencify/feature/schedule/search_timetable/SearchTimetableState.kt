package edu.watumull.presencify.feature.schedule.search_timetable

import edu.watumull.presencify.core.domain.enums.SemesterNumber
import edu.watumull.presencify.core.domain.model.academics.Branch
import edu.watumull.presencify.core.domain.model.schedule.Timetable
import edu.watumull.presencify.core.presentation.UiText
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList

data class SearchTimetableState(
    val viewState: ViewState = ViewState.Content,

    val searchQuery: String = "",
    val isRefreshing: Boolean = false,

    val timetables: PersistentList<Timetable> = persistentListOf(),
    val isLoadingTimetables: Boolean = true,

    val branchOptions: PersistentList<Branch> = persistentListOf(),
    val selectedBranches: PersistentList<Branch> = persistentListOf(),
    val areBranchesLoading: Boolean = true,

    val semesterOptions: ImmutableList<SemesterNumber> = SemesterNumber.entries.toImmutableList(),
    val selectedSemesters: PersistentList<SemesterNumber> = persistentListOf(),

    val academicStartYear: String = "",
    val academicEndYear: String = "",

    val currentPage: Int = 1,
    val isLoadingMore: Boolean = false
) {
    val filteredTimetables: List<Timetable>
        get() {
            var filtered = timetables

            if (selectedBranches.isNotEmpty()) {
                val selectedBranchIds = selectedBranches.map { it.id }.toSet()
                filtered = filtered.filter { timetable ->
                    timetable.division?.semester?.branchId in selectedBranchIds
                }.toPersistentList()
            }

            return filtered
        }

    sealed interface ViewState {
        data object Loading : ViewState
        data class Error(val message: UiText) : ViewState
        data object Content : ViewState
    }
}

