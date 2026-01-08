package edu.watumull.presencify.core.presentation.validation

import edu.watumull.presencify.core.domain.model.academics.Scheme

fun String.validateAsSchemeName(): ValidationResult {
    val required = ValidationRule.Required().validate(this)
    if (!required.successful) return required

    val maxLength = ValidationRule.MaxLength(255).validate(this)
    if (!maxLength.successful) return maxLength

    return ValidationResult(successful = true)
}

fun Scheme?.validateAsScheme(): ValidationResult {
    if (this == null) {
        return ValidationResult(successful = false, errorMessage = "Scheme must be selected")
    }
    return ValidationResult(successful = true)
}