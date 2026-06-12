package edu.watumull.presencify.feature.users.submit_student_biometrics

import edu.watumull.presencify.core.presentation.components.dialog.DialogState

data class SubmitStudentBiometricsState(
    val images: List<ByteArray> = emptyList(),
    val isLoading: Boolean = false,
    val dialogState: DialogState? = null
)
