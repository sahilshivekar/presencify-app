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

    // Search & Filter
    val searchQuery: String = "",
    val isRefreshing: Boolean = false,

    // Timetables List
    val timetables: PersistentList<Timetable> = persistentListOf(),
    val isLoadingTimetables: Boolean = true,

    // Filter Options - Branch
    val branchOptions: PersistentList<Branch> = persistentListOf(),
    val selectedBranches: PersistentList<Branch> = persistentListOf(),
    val areBranchesLoading: Boolean = true,

    // Filter Options - Semester
    val semesterOptions: ImmutableList<SemesterNumber> = SemesterNumber.entries.toImmutableList(),
    val selectedSemesters: PersistentList<SemesterNumber> = persistentListOf(),

    // Filter Options - Academic Year of Semester
    val academicStartYear: String = "",
    val academicEndYear: String = "",

    // Pagination
    val currentPage: Int = 1,
    val isLoadingMore: Boolean = false
) {
    // Computed property to get filtered timetables based on selected branches
    val filteredTimetables: List<Timetable>
        get() {
            var filtered = timetables

            // Filter by branch if any branches are selected
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

