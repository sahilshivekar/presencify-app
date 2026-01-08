package edu.watumull.presencify.core.presentation.validation

import edu.watumull.presencify.core.domain.model.academics.Division

fun String.validateAsDivisionCode(): ValidationResult {
    val required = ValidationRule.Required().validate(this)
    if (!required.successful) return required

    val minLength = ValidationRule.MinLength(1).validate(this)
    if (!minLength.successful) return minLength

    val maxLength = ValidationRule.MaxLength(10).validate(this)
    if (!maxLength.successful) return maxLength

    return ValidationResult(successful = true)
}

fun Division?.validateAsDivision(): ValidationResult {
    if (this == null) {
        return ValidationResult(successful = false, errorMessage = "Division must be selected")
    }
    return ValidationResult(successful = true)
}