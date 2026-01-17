package edu.watumull.presencify.feature.academics.search_division

import androidx.compose.runtime.Stable
import edu.watumull.presencify.core.design.systems.components.dialog.DialogType
import edu.watumull.presencify.core.domain.enums.SemesterNumber
import edu.watumull.presencify.core.domain.model.academics.Branch
import edu.watumull.presencify.core.domain.model.academics.Division
import edu.watumull.presencify.core.presentation.UiText
import edu.watumull.presencify.core.presentation.utils.DateTimeUtils
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

@Stable
data class SearchDivisionState(
    val viewState: ViewState = ViewState.Content,
    val dialogState: DialogState? = null,

    // Search & Filter
    val searchQuery: String = "",
    val isRefreshing: Boolean = false,

    // Divisions List
    val divisions: PersistentList<Division> = persistentListOf(),
    val isLoadingDivisions: Boolean = true,

    // Filter Options - Semester Number
    val semesterNumberOptions: ImmutableList<SemesterNumber> = SemesterNumber.entries.toImmutableList(),
    val selectedSemesterNumber: SemesterNumber? = null,

    // Filter Options - Academic Year
    val academicYearOptions: ImmutableList<String> = generateAcademicYears(),
    val selectedAcademicYear: String? = null,

    // Filter Options - Branches
    val branchOptions: PersistentList<Branch> = persistentListOf(),
    val selectedBranch: Branch? = null,
    val areBranchesLoading: Boolean = true,

    // Pagination
    val currentPage: Int = 1,
    val isLoadingMore: Boolean = false,

    val isBottomSheetVisible: Boolean = false
) {
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

