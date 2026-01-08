package edu.watumull.presencify.core.presentation.validation

import edu.watumull.presencify.core.domain.model.schedule.Timetable

fun Int?.validateAsTimetableVersion(): ValidationResult {
    if (this == null) {
        return ValidationResult(successful = false, errorMessage = "This field is required")
    }

    if (this < 1) {
        return ValidationResult(successful = false, errorMessage = "Timetable version must be at least 1")
    }

    return ValidationResult(successful = true)
}

fun Timetable?.validateAsTimetable(): ValidationResult {
    if (this == null) {
        return ValidationResult(successful = false, errorMessage = "Timetable must be selected")
    }
    return ValidationResult(successful = true)
}