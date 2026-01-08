package edu.watumull.presencify.core.presentation.validation

import edu.watumull.presencify.core.domain.model.academics.University

fun String.validateAsUniversityName(): ValidationResult {
    val required = ValidationRule.Required().validate(this)
    if (!required.successful) return required

    val minLength = ValidationRule.MinLength(1).validate(this)
    if (!minLength.successful) return minLength

    val maxLength = ValidationRule.MaxLength(100).validate(this)
    if (!maxLength.successful) return maxLength

    return ValidationResult(successful = true)
}

fun String.validateAsUniversityAbbreviation(): ValidationResult {
    // Optional but has limit if provided
    if (this.isBlank()) return ValidationResult(successful = true)

    val maxLength = ValidationRule.MaxLength(20).validate(this)
    if (!maxLength.successful) return maxLength

    return ValidationResult(successful = true)
}

fun University?.validateAsUniversity(): ValidationResult {
    if (this == null) {
        return ValidationResult(successful = false, errorMessage = "University must be selected")
    }
    return ValidationResult(successful = true)
}