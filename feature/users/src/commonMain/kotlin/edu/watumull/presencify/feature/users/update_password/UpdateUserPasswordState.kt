package edu.watumull.presencify.feature.users.update_password

import edu.watumull.presencify.core.design.systems.components.dialog.DialogType
import edu.watumull.presencify.core.presentation.UiText

data class UpdateUserPasswordState(
    val password: String = "",
    val confirmPassword: String = "",
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val isPasswordVisible: Boolean = false,
    val isUpdating: Boolean = false,
    val dialogState: DialogState? = null,
) {
    data class DialogState(
        val isVisible: Boolean = true,
        val title: String = "",
        val message: UiText? = null,
        val dialogType: DialogType = DialogType.INFO,
    )
}
