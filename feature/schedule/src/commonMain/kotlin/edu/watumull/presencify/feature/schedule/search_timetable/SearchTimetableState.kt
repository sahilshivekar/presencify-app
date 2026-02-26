package edu.watumull.presencify.feature.schedule.search_timetable

import edu.watumull.presencify.core.design.systems.components.dialog.DialogType
import edu.watumull.presencify.core.domain.enums.SemesterNumber
import edu.watumull.presencify.core.domain.model.academics.Branch
import edu.watumull.presencify.core.domain.model.schedule.Timetable
import edu.watumull.presencify.core.presentation.UiText
import edu.watumull.presencify.core.presentation.utils.DateTimeUtils
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList

data class SearchTimetableState(
    val viewState: ViewState = ViewState.Content,
    val dialogState: DialogState? = null,

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
    val academicYearOfSemesterOptions: ImmutableList<String> = generateAcademicYears(),
    val selectedAcademicYearOfSemester: String? = null,

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

    data class DialogState(
        val isVisible: Boolean = true,
        val dialogType: DialogType = DialogType.INFO,
        val dialogIntention: DialogIntention = DialogIntention.GENERIC,
        val title: String = "",
        val message: UiText = UiText.DynamicString(""),
    )
}

enum class DialogIntention {
    GENERIC,
}

private fun generateAcademicYears(): ImmutableList<String> {
    val currentYear = DateTimeUtils.getCurrentDate().year
    return (0..9).map { offset ->
        val startYear = currentYear - offset - 1
        val endYear = currentYear - offset
        "$startYear - $endYear"
    }.toImmutableList()
}
