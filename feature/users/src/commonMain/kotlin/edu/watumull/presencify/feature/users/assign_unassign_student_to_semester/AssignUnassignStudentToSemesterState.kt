package edu.watumull.presencify.feature.users.assign_unassign_student_to_semester

import androidx.compose.runtime.Stable
import edu.watumull.presencify.core.designsystem.components.dialog.DialogType
import edu.watumull.presencify.core.domain.enums.SemesterNumber
import edu.watumull.presencify.core.domain.model.academics.Branch
import edu.watumull.presencify.core.presentation.UiText
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

@Stable
data class AssignUnassignStudentToSemesterState(
    val viewState: ViewState = ViewState.Content,
    val dialogState: DialogState? = null,

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

    // Validation
    val branchError: String? = null,
    val semesterNumberError: String? = null,
    val startYearError: String? = null,
    val endYearError: String? = null,

    // Dropdown states
    val isBranchDropdownOpen: Boolean = false,
    val isSemesterNumberDropdownOpen: Boolean = false,

    // Loading state for semester lookup
    val isLookingSemester: Boolean = false,
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
