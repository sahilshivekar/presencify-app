package edu.watumull.presencify.feature.schedule.add_edit_timetable

import androidx.compose.runtime.Stable
import edu.watumull.presencify.core.domain.enums.SemesterNumber
import edu.watumull.presencify.core.domain.model.academics.Batch
import edu.watumull.presencify.core.domain.model.academics.Branch
import edu.watumull.presencify.core.domain.model.academics.Division
import edu.watumull.presencify.core.presentation.UiText
import edu.watumull.presencify.core.presentation.components.dialog.DialogState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

@Stable
data class AddEditTimetableState(
    val viewState: ViewState = ViewState.Content,
    val dialogState: DialogState? = null,
    val isEditMode: Boolean = false,
    val timetableId: String? = null,

    val branchOptions: PersistentList<Branch> = persistentListOf(),
    val selectedBranch: Branch? = null,
    val areBranchesLoading: Boolean = true,

    val semesterNumberOptions: ImmutableList<SemesterNumber> = SemesterNumber.entries.toImmutableList(),
    val selectedSemesterNumber: SemesterNumber? = null,

    val startYear: String = "",
    val endYear: String = "",

    val divisionOptions: PersistentList<Division> = persistentListOf(),
    val selectedDivision: Division? = null,
    val areDivisionsVisible: Boolean = false,

    val batchOptions: PersistentList<Batch> = persistentListOf(),
    val selectedBatches: PersistentList<Batch> = persistentListOf(),
    val areBatchesVisible: Boolean = false,

    val timetableVersion: String = "",

    val branchError: String? = null,
    val semesterNumberError: String? = null,
    val startYearError: String? = null,
    val endYearError: String? = null,
    val divisionError: String? = null,
    val timetableVersionError: String? = null,

    val isBranchDropdownOpen: Boolean = false,
    val isSemesterNumberDropdownOpen: Boolean = false,
    val isDivisionDropdownOpen: Boolean = false,

    val isLookingDivisions: Boolean = false,
    val isLookingBatches: Boolean = false,
    val isSaving: Boolean = false,
) {
    sealed interface ViewState {
        data object Loading : ViewState
        data class Error(val message: UiText) : ViewState
        data object Content : ViewState
    }

    sealed interface DialogPurpose {
        data object NavigationConfirmation : DialogPurpose
        data object Success : DialogPurpose
    }
}
