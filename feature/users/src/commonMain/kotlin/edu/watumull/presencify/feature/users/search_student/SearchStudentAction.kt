package edu.watumull.presencify.feature.users.search_student

import edu.watumull.presencify.core.domain.enums.AdmissionType
import edu.watumull.presencify.core.domain.enums.BiometricVerificationStatus
import edu.watumull.presencify.core.domain.enums.SemesterNumber
import edu.watumull.presencify.core.domain.model.academics.Batch
import edu.watumull.presencify.core.domain.model.academics.Branch
import edu.watumull.presencify.core.domain.model.academics.Division
import edu.watumull.presencify.core.domain.model.academics.Scheme

sealed interface SearchStudentAction {
    data object NavigateBack : SearchStudentAction

    data class UpdateSearchQuery(val query: String) : SearchStudentAction
    data object Search : SearchStudentAction
    data object Refresh : SearchStudentAction

    data class ToggleBranch(val branch: Branch) : SearchStudentAction
    data class ToggleSemester(val semester: SemesterNumber) : SearchStudentAction
    data class UpdateAcademicStartYear(val year: String) : SearchStudentAction
    data class UpdateAcademicEndYear(val year: String) : SearchStudentAction
    data class ToggleAdmissionType(val admissionType: AdmissionType) : SearchStudentAction
    data class SelectAdmissionYear(val year: String?) : SearchStudentAction
    data class UpdateDropoutStartYear(val year: String) : SearchStudentAction
    data class UpdateDropoutEndYear(val year: String) : SearchStudentAction
    data class SelectBiometricVerificationStatus(val status: BiometricVerificationStatus?) : SearchStudentAction
    data class SelectScheme(val scheme: Scheme?) : SearchStudentAction
    data class SelectDivision(val division: Division?) : SearchStudentAction
    data class SelectBatch(val batch: Batch?) : SearchStudentAction

    data object ResetFilters : SearchStudentAction
    data object ApplyFilters : SearchStudentAction

    data class StudentCardClick(val studentId: String) : SearchStudentAction
    data class StudentActionButtonClick(val studentId: String) : SearchStudentAction
    data class ToggleStudentDropout(val studentId: String, val isCurrentlyDropout: Boolean) : SearchStudentAction

    data object LoadMoreStudents : SearchStudentAction

    data object ClickFloatingActionButton : SearchStudentAction
}

