package edu.watumull.presencify.core.presentation.composition_locals

import androidx.compose.runtime.compositionLocalOf
import edu.watumull.presencify.core.domain.model.auth.UserRole

val LocalUserRole = compositionLocalOf<UserRole?> { null }
val LocalUserId = compositionLocalOf<String?> { null }
