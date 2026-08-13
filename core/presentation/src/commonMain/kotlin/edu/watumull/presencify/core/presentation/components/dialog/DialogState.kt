package edu.watumull.presencify.core.presentation.components.dialog

import edu.watumull.presencify.core.designsystem.components.dialog.DialogType
import edu.watumull.presencify.core.presentation.UiText


data class DialogState(
    val title: UiText? = null,
    val message: UiText,
    val dialogType: DialogType,
    val purpose: DialogPurpose? = null,
)