package edu.watumull.presencify.feature.users.review_student_biometrics

import edu.watumull.presencify.core.domain.enums.BiometricVerificationStatus
import edu.watumull.presencify.core.presentation.UiText
import edu.watumull.presencify.core.presentation.components.dialog.DialogState

data class ReviewStudentBiometricsState(
    val studentId: String = "",
    val viewState: ViewState = ViewState.Loading,
    val biometricStatus: BiometricVerificationStatus = BiometricVerificationStatus.NOT_SUBMITTED,
    val presignedUrls: List<String> = emptyList(),
    val isApproving: Boolean = false,
    val isRejecting: Boolean = false,
    val dialogState: DialogState? = null
) {
    sealed interface ViewState {
        data object Loading : ViewState
        data class Error(val error: UiText) : ViewState
        data object Content : ViewState
    }
}
