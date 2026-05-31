package edu.watumull.presencify.feature.attendance.add_student_biometrics

import edu.watumull.presencify.core.presentation.UiText
import edu.watumull.presencify.core.presentation.components.dialog.DialogState

data class AddStudentBiometricsState(
    val studentId: String = "",
    val images: List<ByteArray> = emptyList(),
    val isLoading: Boolean = false,
    val dialogState: DialogState? = null
)

