package edu.watumull.presencify.feature.users.search_student

import androidx.compose.runtime.Stable
import edu.watumull.presencify.core.design.systems.components.dialog.DialogType
import edu.watumull.presencify.core.domain.enums.AdmissionType
import edu.watumull.presencify.core.domain.enums.SemesterNumber
import edu.watumull.presencify.core.domain.model.academics.Batch
import edu.watumull.presencify.core.domain.model.academics.Branch
import edu.watumull.presencify.core.domain.model.academics.Division
import edu.watumull.presencify.core.domain.model.academics.Scheme
import edu.watumull.presencify.core.domain.model.student.Student
import edu.watumull.presencify.core.presentation.UiText
import edu.watumull.presencify.core.presentation.utils.DateTimeUtils
import edu.watumull.presencify.feature.users.navigation.SearchStudentIntention
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

@Stable
data class SearchStudentState(
    val viewState: ViewState = ViewState.Content,
    val dialogState: DialogState? = null,

    // Search & Filter
    val searchQuery: String = "",
    val isRefreshing: Boolean = false,

    // Students List
    val students: PersistentList<Student> = persistentListOf(),
    val isLoadingStudents: Boolean = true,

    // Selection Mode
    val intention: SearchStudentIntention = SearchStudentIntention.DEFAULT,
    val selectedStudentIds: Set<String> = emptySet(),
    val isSelectable: Boolean = false,

    // Filter Options - Branches
    val branchOptions: PersistentList<Branch> = persistentListOf(),
    val selectedBranches: PersistentList<Branch> = persistentListOf(),
    val areBranchesLoading: Boolean = true,

    // Filter Options - Semesters
    val semesterOptions: ImmutableList<SemesterNumber> = SemesterNumber.entries.toImmutableList(),
    val selectedSemesters: PersistentList<SemesterNumber> = persistentListOf(),

    // Filter Options - Academic Year of Semester
    val academicYearOfSemesterOptions: ImmutableList<String> = generateAcademicYears(),
    val selectedAcademicYearOfSemester: String? = null,

    // Filter Options - Admission
    val admissionTypeOptions: ImmutableList<AdmissionType> = AdmissionType.entries.toImmutableList(),
    val selectedAdmissionTypes: PersistentList<AdmissionType> = persistentListOf(),

    val admissionYearOptions: ImmutableList<String> = generateYears(),
    val selectedAdmissionYear: String? = null,

    // Filter Options - Dropout Year
    val dropoutYearOptions: ImmutableList<String> = generateAcademicYears(),
    val selectedDropoutYear: String? = null,

    // Filter Options - Scheme
    val schemeOptions: PersistentList<Scheme> = persistentListOf(),
    val selectedScheme: Scheme? = null,
    val areSchemesLoading: Boolean = true,

    // Filter Options - Division
    val divisionOptions: PersistentList<Division> = persistentListOf(),
    val selectedDivision: Division? = null,
    val areDivisionsLoading: Boolean = false,

    // Filter Options - Batch
    val batchOptions: PersistentList<Batch> = persistentListOf(),
    val selectedBatch: Batch? = null,
    val areBatchesLoading: Boolean = false,

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

private fun generateYears(): ImmutableList<String> {
    val currentYear = DateTimeUtils.getCurrentDate().year
    return (0..9).map { offset ->
        (currentYear - offset).toString()
    }.toImmutableList()
}

