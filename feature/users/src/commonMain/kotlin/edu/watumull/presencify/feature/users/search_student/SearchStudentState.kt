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

    val searchQuery: String = "",
    val isRefreshing: Boolean = false,

    val students: PersistentList<Student> = persistentListOf(),
    val isLoadingStudents: Boolean = true,

    val intention: SearchStudentIntention = SearchStudentIntention.DEFAULT,

    val semesterId: String? = null,
    val divisionId: String? = null,
    val batchId: String? = null,
    val newStartDate: String? = null,

    val dropoutAcademicStartYear: Int? = null,
    val dropoutAcademicEndYear: Int? = null,

    val studentDropoutStatus: Map<String, Boolean> = emptyMap(),

    val loadingStudentIds: Set<String> = emptySet(),

    val studentFeedback: Map<String, ListItemFeedback?> = emptyMap(),

    val branchOptions: PersistentList<Branch> = persistentListOf(),
    val selectedBranches: PersistentList<Branch> = persistentListOf(),
    val areBranchesLoading: Boolean = true,

    val semesterOptions: ImmutableList<SemesterNumber> = SemesterNumber.entries.toImmutableList(),
    val selectedSemesters: PersistentList<SemesterNumber> = persistentListOf(),

    val academicStartYear: String = "",
    val academicEndYear: String = "",

    val admissionTypeOptions: ImmutableList<AdmissionType> = AdmissionType.entries.toImmutableList(),
    val selectedAdmissionTypes: PersistentList<AdmissionType> = persistentListOf(),

    val admissionYearOptions: ImmutableList<String> = generateYears(),
    val admissionYear: String? = null,

    val dropoutStartYear: String = "",
    val dropoutEndYear: String = "",

    val biometricVerificationStatusOptions: ImmutableList<BiometricVerificationStatus> = BiometricVerificationStatus.entries.toImmutableList(),
    val selectedBiometricVerificationStatus: BiometricVerificationStatus? = null,

    val schemeOptions: PersistentList<Scheme> = persistentListOf(),
    val selectedScheme: Scheme? = null,
    val areSchemesLoading: Boolean = true,

    val divisionOptions: PersistentList<Division> = persistentListOf(),
    val selectedDivision: Division? = null,
    val areDivisionsLoading: Boolean = false,

    val batchOptions: PersistentList<Batch> = persistentListOf(),
    val selectedBatch: Batch? = null,
    val areBatchesLoading: Boolean = false,

    val currentPage: Int = 1,
    val isLoadingMore: Boolean = false,

    val academicStartYearError: String? = null,
    val academicEndYearError: String? = null,
    val dropoutStartYearError: String? = null,
    val dropoutEndYearError: String? = null,
    val admissionYearError: String? = null,
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
