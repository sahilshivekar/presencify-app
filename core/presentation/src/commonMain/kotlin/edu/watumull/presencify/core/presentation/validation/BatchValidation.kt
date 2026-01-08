package edu.watumull.presencify.core.presentation.validation

import edu.watumull.presencify.core.domain.model.academics.Batch

fun String.validateAsBatchCode(): ValidationResult {
    val required = ValidationRule.Required().validate(this)
    if (!required.successful) return required

    val maxLength = ValidationRule.MaxLength(255).validate(this)
    if (!maxLength.successful) return maxLength

    return ValidationResult(successful = true)
}

fun Batch?.validateAsBatch(): ValidationResult {
    if (this == null) {
        return ValidationResult(successful = false, errorMessage = "Batch must be selected")
    }
    return ValidationResult(successful = true)
}