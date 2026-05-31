package edu.watumull.presencify.core.presentation.components.dialog

import edu.watumull.presencify.core.designsystem.components.dialog.DialogType
import edu.watumull.presencify.core.presentation.UiText

/**
 * Shared dialog UI state used across screens.
 *
 * This state only describes the dialog UI.
 * Actual business meaning of confirm actions should remain
 * feature-specific.
 */
data class DialogState(
    val title: UiText? = null,
    val message: UiText,
    val dialogType: DialogType,
    val purpose: DialogPurpose? = null,
)