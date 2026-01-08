package edu.watumull.presencify.core.presentation.validation

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

fun LocalDate?.validateAsAttendanceDate(): ValidationResult {
    if (this == null) {
        return ValidationResult(successful = false, errorMessage = "This field is required")
    }

    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    if (this > today) {
        return ValidationResult(successful = false, errorMessage = "Date cannot be in the future")
    }

    return ValidationResult(successful = true)
}