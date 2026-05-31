package edu.watumull.presencify.feature.users.update_password

import edu.watumull.presencify.core.presentation.UiText
import edu.watumull.presencify.core.presentation.components.dialog.DialogState

data class UpdateUserPasswordState(
    val password: String = "",
    val confirmPassword: String = "",
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val isPasswordVisible: Boolean = false,
    val isUpdating: Boolean = false,
    val dialogState: DialogState? = null,
)
