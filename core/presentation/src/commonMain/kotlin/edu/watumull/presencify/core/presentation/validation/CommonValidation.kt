package edu.watumull.presencify.core.presentation.validation

import edu.watumull.presencify.core.domain.enums.SemesterNumber

fun SemesterNumber?.validateAsSemesterNumber(): ValidationResult {
    if (this == null) {
        return ValidationResult(successful = false, errorMessage = "Semester number must be selected")
    }
    return ValidationResult(successful = true)
}


fun String.validateAsEmail(): ValidationResult {
    val required = ValidationRule.Required().validate(this)
    if (!required.successful) return required

    val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    val regexMatch = ValidationRule.RegexMatch(emailRegex, "This field must be a valid email address").validate(this)
    if (!regexMatch.successful) return regexMatch

    return ValidationResult(successful = true)
}

fun String.validateAsPassword(): ValidationResult {
    val required = ValidationRule.Required().validate(this)
    if (!required.successful) return required

    val minLength = ValidationRule.MinLength(8).validate(this)
    if (!minLength.successful) return minLength

    val maxLength = ValidationRule.MaxLength(128).validate(this)
    if (!maxLength.successful) return maxLength

    val noSpaces = ValidationRule.NoSpaces().validate(this)
    if (!noSpaces.successful) return noSpaces

    val containsUppercase = ValidationRule.ContainsUppercase().validate(this)
    if (!containsUppercase.successful) return containsUppercase

    val containsLowercase = ValidationRule.ContainsLowercase().validate(this)
    if (!containsLowercase.successful) return containsLowercase

    val containsDigit = ValidationRule.ContainsDigit().validate(this)
    if (!containsDigit.successful) return containsDigit

    val containsSpecialChar = ValidationRule.ContainsSpecialChar().validate(this)
    if (!containsSpecialChar.successful) return containsSpecialChar

    return ValidationResult(successful = true)
}

fun String.validateAsFirstName(): ValidationResult {
    val required = ValidationRule.Required().validate(this)
    if (!required.successful) return required

    val minLength = ValidationRule.MinLength(1).validate(this)
    if (!minLength.successful) return minLength

    val maxLength = ValidationRule.MaxLength(100).validate(this)
    if (!maxLength.successful) return maxLength

    return ValidationResult(successful = true)
}

fun String.validateAsLastName(): ValidationResult {
    val required = ValidationRule.Required().validate(this)
    if (!required.successful) return required

    val minLength = ValidationRule.MinLength(1).validate(this)
    if (!minLength.successful) return minLength

    val maxLength = ValidationRule.MaxLength(100).validate(this)
    if (!maxLength.successful) return maxLength

    return ValidationResult(successful = true)
}


fun String.validateAsMiddleName(): ValidationResult {
    if (this.isBlank()) return ValidationResult(successful = true)  // Optional

    val minLength = ValidationRule.MinLength(1).validate(this)
    if (!minLength.successful) return minLength

    val maxLength = ValidationRule.MaxLength(100).validate(this)
    if (!maxLength.successful) return maxLength

    return ValidationResult(successful = true)
}

fun String.validateAsPhoneNumber(): ValidationResult {
    val required = ValidationRule.Required().validate(this)
    if (!required.successful) return required

    val phoneRegex = Regex("^\\+[1-9]\\d{6,14}$")
    val regexMatch = ValidationRule.RegexMatch(phoneRegex, "This is not a valid phone number").validate(this)
    if (!regexMatch.successful) return regexMatch

    return ValidationResult(successful = true)
}

fun String.validateAsVerificationCode(): ValidationResult {
    val required = ValidationRule.Required().validate(this)
    if (!required.successful) return required

    if (this.length != 6) {
        return ValidationResult(successful = false, errorMessage = "Verification code must be exactly 6 digits")
    }

    if (!this.all { it.isDigit() }) {
        return ValidationResult(successful = false, errorMessage = "Verification code must contain only numbers")
    }

    return ValidationResult(successful = true)
}

fun String?.validateAsUUID(): ValidationResult {
    if (this.isNullOrBlank()) {
        return ValidationResult(successful = false, errorMessage = "UUID cannot be empty")
    }

    val uuidRegex = Regex("^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$")
    val regexMatch = ValidationRule.RegexMatch(uuidRegex, "This field must be a valid UUID").validate(this)
    if (!regexMatch.successful) return regexMatch

    return ValidationResult(successful = true)
}

fun String?.validateAsAcademicStartYear(endYear: String?): ValidationResult {
    if (this.isNullOrBlank()) {
        return ValidationResult(successful = false, errorMessage = "Academic start year cannot be empty")
    }

    val start = this.toIntOrNull() ?: return ValidationResult(
        successful = false,
        errorMessage = "Academic start year must be a valid number"
    )

    if (start !in 1900..3000) {
        return ValidationResult(successful = false, errorMessage = "Academic start year must be between 1900 and 3000")
    }

    if (endYear != null) {
        val end = endYear.toIntOrNull()
        if (end != null && start >= end) {
            return ValidationResult(successful = false, errorMessage = "Academic start year must be before academic end year")
        }
    }

    return ValidationResult(successful = true)
}

fun String?.validateAsAcademicEndYear(startYear: String?): ValidationResult {
    if (this.isNullOrBlank()) {
        return ValidationResult(successful = false, errorMessage = "Academic end year cannot be empty")
    }

    val end = this.toIntOrNull() ?: return ValidationResult(
        successful = false,
        errorMessage = "Academic end year must be a valid number"
    )

    if (end !in 1900..3000) {
        return ValidationResult(successful = false, errorMessage = "Academic end year must be between 1900 and 3000")
    }

    if (startYear != null) {
        val start = startYear.toIntOrNull()
        if (start != null && end <= start) {
            return ValidationResult(successful = false, errorMessage = "Academic end year must be after academic start year")
        }
    }

    return ValidationResult(successful = true)
}