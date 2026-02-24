package edu.watumull.presencify.core.presentation.components

import edu.watumull.presencify.core.presentation.UiText

/**
 * Feedback messages for list items.
 */
sealed class ListItemFeedback {
    data class Success(val message: UiText) : ListItemFeedback()
    data class Error(val message: UiText) : ListItemFeedback()
}
