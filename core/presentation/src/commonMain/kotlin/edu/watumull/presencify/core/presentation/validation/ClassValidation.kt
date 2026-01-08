package edu.watumull.presencify.core.presentation.validation

import edu.watumull.presencify.core.domain.enums.DayOfWeek
import kotlinx.datetime.LocalDate

fun DayOfWeek?.validateAsDayOfWeek(): ValidationResult {
    if (this == null) {
        return ValidationResult(successful = false, errorMessage = "Day of week must be selected")
    }
    return ValidationResult(successful = true)
}

fun LocalDate?.validateAsClassActiveFrom(activeTill: LocalDate?): ValidationResult {
    if (this == null) {
        return ValidationResult(successful = false, errorMessage = "This field is required")
    }
    if (activeTill != null && this > activeTill) {
        return ValidationResult(successful = false, errorMessage = "Active from date must be on or before active till date")
    }
    return ValidationResult(successful = true)
}

fun LocalDate?.validateAsClassActiveTill(activeFrom: LocalDate?): ValidationResult {
    if (this == null) {
        return ValidationResult(successful = false, errorMessage = "This field is required")
    }
    if (activeFrom != null && this < activeFrom) {
        return ValidationResult(successful = false, errorMessage = "Active till date must be on or after active from date")
    }
    return ValidationResult(successful = true)
}