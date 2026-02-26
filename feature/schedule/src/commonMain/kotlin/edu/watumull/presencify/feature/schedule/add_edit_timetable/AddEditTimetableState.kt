package edu.watumull.presencify.feature.schedule.add_edit_timetable

import androidx.compose.runtime.Stable
import edu.watumull.presencify.core.design.systems.components.dialog.DialogType
import edu.watumull.presencify.core.domain.enums.SemesterNumber
import edu.watumull.presencify.core.domain.model.academics.Batch
import edu.watumull.presencify.core.domain.model.academics.Branch
import edu.watumull.presencify.core.domain.model.academics.Division
import edu.watumull.presencify.core.presentation.UiText
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

    // Branch Selection
    val branchOptions: PersistentList<Branch> = persistentListOf(),
    val selectedBranch: Branch? = null,
    val areBranchesLoading: Boolean = true,

    // Semester Number Selection
    val semesterNumberOptions: ImmutableList<SemesterNumber> = SemesterNumber.entries.toImmutableList(),
    val selectedSemesterNumber: SemesterNumber? = null,

    // Academic Year Selection
    val startYear: String = "",
    val endYear: String = "",

    // Division Selection (shown after finding divisions)
    val divisionOptions: PersistentList<Division> = persistentListOf(),
    val selectedDivision: Division? = null,
    val areDivisionsVisible: Boolean = false,

    // Batch Selection (shown after finding batches)
    val batchOptions: PersistentList<Batch> = persistentListOf(),
    val selectedBatches: PersistentList<Batch> = persistentListOf(),
    val areBatchesVisible: Boolean = false,

    // Timetable Version
    val timetableVersion: String = "",

    // Validation
    val branchError: String? = null,
    val semesterNumberError: String? = null,
    val startYearError: String? = null,
    val endYearError: String? = null,
    val divisionError: String? = null,
    val timetableVersionError: String? = null,

    // Dropdown states
    val isBranchDropdownOpen: Boolean = false,
    val isSemesterNumberDropdownOpen: Boolean = false,
    val isDivisionDropdownOpen: Boolean = false,

    // Loading states
    val isLookingDivisions: Boolean = false,
    val isLookingBatches: Boolean = false,
    val isSaving: Boolean = false,
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
    SUCCESS,
}
