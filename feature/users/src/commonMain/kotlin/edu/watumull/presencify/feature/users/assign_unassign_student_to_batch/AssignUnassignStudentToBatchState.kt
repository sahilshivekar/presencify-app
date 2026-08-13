package edu.watumull.presencify.feature.users.assign_unassign_student_to_batch

import androidx.compose.runtime.Stable
import edu.watumull.presencify.core.domain.enums.SemesterNumber
import edu.watumull.presencify.core.domain.model.academics.Batch
import edu.watumull.presencify.core.domain.model.academics.Branch
import edu.watumull.presencify.core.presentation.UiText
import edu.watumull.presencify.core.presentation.components.dialog.DialogState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

@Stable
data class AssignUnassignStudentToBatchState(
    val viewState: ViewState = ViewState.Content,
    val dialogState: DialogState? = null,

    val branchOptions: PersistentList<Branch> = persistentListOf(),
    val selectedBranch: Branch? = null,
    val areBranchesLoading: Boolean = true,

    val semesterNumberOptions: ImmutableList<SemesterNumber> = SemesterNumber.entries.toImmutableList(),
    val selectedSemesterNumber: SemesterNumber? = null,

    val startYear: String = "",
    val endYear: String = "",

    val batchOptions: PersistentList<Batch> = persistentListOf(),
    val selectedBatch: Batch? = null,
    val areBatchesVisible: Boolean = false,

    val branchError: String? = null,
    val semesterNumberError: String? = null,
    val startYearError: String? = null,
    val endYearError: String? = null,
    val batchError: String? = null,

    val isBranchDropdownOpen: Boolean = false,
    val isSemesterNumberDropdownOpen: Boolean = false,
    val isBatchDropdownOpen: Boolean = false,

    val isLookingDivisions: Boolean = false,
) {
    sealed interface ViewState {
        data object Loading : ViewState
        data class Error(val message: UiText) : ViewState
        data object Content : ViewState
    }
}
