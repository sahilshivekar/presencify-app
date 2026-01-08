package edu.watumull.presencify.core.presentation.validation

fun String.validateAsAdminUsername(): ValidationResult {
    val required = ValidationRule.Required().validate(this)
    if (!required.successful) return required

    val minLength = ValidationRule.MinLength(3).validate(this)
    if (!minLength.successful) return minLength

    val maxLength = ValidationRule.MaxLength(30).validate(this)
    if (!maxLength.successful) return maxLength

    val noSpaces = ValidationRule.NoSpaces().validate(this)
    if (!noSpaces.successful) return noSpaces

    val isLowercase = ValidationRule.IsLowercase().validate(this)
    if (!isLowercase.successful) return isLowercase

    return ValidationResult(successful = true)
}