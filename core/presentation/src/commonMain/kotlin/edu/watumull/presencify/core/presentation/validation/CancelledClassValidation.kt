package edu.watumull.presencify.core.presentation.validation

import kotlinx.datetime.LocalDate

fun LocalDate?.validateAsCancelledClassDate(): ValidationResult {
    if (this == null) {
        return ValidationResult(successful = false, errorMessage = "This field is required")
    }

    return ValidationResult(successful = true)
}

fun String.validateAsCancelledClassReason(): ValidationResult {
    // Optional field
    if (this.isBlank()) return ValidationResult(successful = true)

    val maxLength = ValidationRule.MaxLength(255).validate(this)
    if (!maxLength.successful) return maxLength

    return ValidationResult(successful = true)
}