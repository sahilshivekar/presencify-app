package edu.watumull.presencify.core.presentation.validation

import edu.watumull.presencify.core.domain.model.academics.Branch

fun String.validateAsBranchName(): ValidationResult {
    val required = ValidationRule.Required().validate(this)
    if (!required.successful) return required

    val maxLength = ValidationRule.MaxLength(255).validate(this)
    if (!maxLength.successful) return maxLength

    return ValidationResult(successful = true)
}

fun String.validateAsBranchAbbreviation(): ValidationResult {
    val required = ValidationRule.Required().validate(this)
    if (!required.successful) return required

    val maxLength = ValidationRule.MaxLength(255).validate(this)
    if (!maxLength.successful) return maxLength

    return ValidationResult(successful = true)
}

fun Branch?.validateAsBranch(): ValidationResult {
    if (this == null) {
        return ValidationResult(successful = false, errorMessage = "Branch must be selected")
    }
    return ValidationResult(successful = true)
}