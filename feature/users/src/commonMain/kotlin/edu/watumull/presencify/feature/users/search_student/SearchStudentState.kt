package edu.watumull.presencify.feature.users.search_student

import androidx.compose.runtime.Stable
import edu.watumull.presencify.core.domain.enums.AdmissionType
import edu.watumull.presencify.core.domain.enums.BiometricVerificationStatus
import edu.watumull.presencify.core.domain.enums.SemesterNumber
import edu.watumull.presencify.core.domain.model.academics.Batch
import edu.watumull.presencify.core.domain.model.academics.Branch
import edu.watumull.presencify.core.domain.model.academics.Division
import edu.watumull.presencify.core.domain.model.academics.Scheme
import edu.watumull.presencify.core.domain.model.student.Student
import edu.watumull.presencify.core.presentation.UiText
import edu.watumull.presencify.core.presentation.components.ListItemFeedback
import edu.watumull.presencify.core.presentation.utils.DateTimeUtils
import edu.watumull.presencify.feature.users.navigation.SearchStudentIntention
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

@Stable
data class SearchStudentState(
    val viewState: ViewState = ViewState.Content,

    // Search & Filter
    val searchQuery: String = "",
    val isRefreshing: Boolean = false,

    // Students List
    val students: PersistentList<Student> = persistentListOf(),
    val isLoadingStudents: Boolean = true,

    // Selection Mode
    val intention: SearchStudentIntention = SearchStudentIntention.DEFAULT,

    // Required parameters for specific intentions
    val semesterId: String? = null,
    val divisionId: String? = null,
    val batchId: String? = null,
    val newStartDate: String? = null,

    // Dropout parameters (for MARK_UNMARK_STUDENT_AS_DROPOUT intention)
    val dropoutAcademicStartYear: Int? = null,
    val dropoutAcademicEndYear: Int? = null,

    // Track dropout status per student (studentId -> isDropout)
    val studentDropoutStatus: Map<String, Boolean> = emptyMap(),

    // Track individual student operations
    val loadingStudentIds: Set<String> = emptySet(),

    // Feedback for individual student operations
    val studentFeedback: Map<String, ListItemFeedback?> = emptyMap(),

    // Filter Options - Branches
    val branchOptions: PersistentList<Branch> = persistentListOf(),
    val selectedBranches: PersistentList<Branch> = persistentListOf(),
    val areBranchesLoading: Boolean = true,

    // Filter Options - Semesters
    val semesterOptions: ImmutableList<SemesterNumber> = SemesterNumber.entries.toImmutableList(),
    val selectedSemesters: PersistentList<SemesterNumber> = persistentListOf(),

    // Filter Options - Academic Year of Semester
    val academicStartYear: String = "",
    val academicEndYear: String = "",

    // Filter Options - Admission
    val admissionTypeOptions: ImmutableList<AdmissionType> = AdmissionType.entries.toImmutableList(),
    val selectedAdmissionTypes: PersistentList<AdmissionType> = persistentListOf(),

    val admissionYearOptions: ImmutableList<String> = generateYears(),
    val admissionYear: String? = null,

    // Filter Options - Dropout Year
    val dropoutStartYear: String = "",
    val dropoutEndYear: String = "",

    // Filter Options - Biometric Verification Status
    val biometricVerificationStatusOptions: ImmutableList<BiometricVerificationStatus> = BiometricVerificationStatus.entries.toImmutableList(),
    val selectedBiometricVerificationStatus: BiometricVerificationStatus? = null,

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
    val isLoadingMore: Boolean = false
) {
    sealed interface ViewState {
        data object Loading : ViewState
        data class Error(val message: UiText) : ViewState
        data object Content : ViewState
    }
}


private fun generateYears(): ImmutableList<String> {
    val currentYear = DateTimeUtils.getCurrentDate().year
    return (0..9).map { offset ->
        (currentYear - offset).toString()
    }.toImmutableList()
}
