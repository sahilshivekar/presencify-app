package edu.watumull.presencify.feature.attendance.add_student_biometrics

import edu.watumull.presencify.core.design.systems.components.dialog.DialogType
import edu.watumull.presencify.core.presentation.UiText

data class AddStudentBiometricsState(
    val studentId: String = "",
    val images: List<ByteArray> = emptyList(),
    val isLoading: Boolean = false,
    val dialogState: DialogState? = null
) {
    data class DialogState(
        val isVisible: Boolean = true,
        val dialogType: DialogType = DialogType.INFO,
        val dialogIntention: DialogIntention = DialogIntention.GENERIC,
        val title: String = "",
        val message: UiText? = null,
    )
}

enum class DialogIntention {
    GENERIC,
}

