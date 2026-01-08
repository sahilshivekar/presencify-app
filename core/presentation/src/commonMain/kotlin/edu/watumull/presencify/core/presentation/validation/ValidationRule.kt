package edu.watumull.presencify.core.presentation.validation

sealed class ValidationRule {
    abstract fun validate(value: String): ValidationResult

    data class Required(val errorMessage: String = ValidationMessages.FIELD_REQUIRED) : ValidationRule() {
        override fun validate(value: String): ValidationResult {
            return if (value.isBlank()) {
                ValidationResult(successful = false, errorMessage = errorMessage)
            } else {
                ValidationResult(successful = true)
            }
        }
    }

    data class MinLength(val minLength: Int, val errorMessage: String = ValidationMessages.FIELD_MIN_LENGTH) : ValidationRule() {
        override fun validate(value: String): ValidationResult {
            return if (value.length < minLength) {
                ValidationResult(successful = false, errorMessage = errorMessage.replace("{length}", minLength.toString()))
            } else {
                ValidationResult(successful = true)
            }
        }
    }

    data class MaxLength(val maxLength: Int, val errorMessage: String = ValidationMessages.FIELD_MAX_LENGTH) : ValidationRule() {
        override fun validate(value: String): ValidationResult {
            return if (value.length > maxLength) {
                ValidationResult(successful = false, errorMessage = errorMessage.replace("{length}", maxLength.toString()))
            } else {
                ValidationResult(successful = true)
            }
        }
    }

    data class RegexMatch(val regex: Regex, val errorMessage: String = "Invalid format") : ValidationRule() {
        override fun validate(value: String): ValidationResult {
            return if (!value.matches(regex)) {
                ValidationResult(successful = false, errorMessage = errorMessage)
            } else {
                ValidationResult(successful = true)
            }
        }
    }

    data class NoSpaces(val errorMessage: String = ValidationMessages.FIELD_NO_SPACES) : ValidationRule() {
        override fun validate(value: String): ValidationResult {
            return if (value.contains(" ")) {
                ValidationResult(successful = false, errorMessage = errorMessage)
            } else {
                ValidationResult(successful = true)
            }
        }
    }

    data class ContainsUppercase(val errorMessage: String = ValidationMessages.FIELD_CONTAINS_UPPERCASE) : ValidationRule() {
        override fun validate(value: String): ValidationResult {
            return if (!value.any { it.isUpperCase() }) {
                ValidationResult(successful = false, errorMessage = errorMessage)
            } else {
                ValidationResult(successful = true)
            }
        }
    }

    data class ContainsLowercase(val errorMessage: String = ValidationMessages.FIELD_CONTAINS_LOWERCASE) : ValidationRule() {
        override fun validate(value: String): ValidationResult {
            return if (!value.any { it.isLowerCase() }) {
                ValidationResult(successful = false, errorMessage = errorMessage)
            } else {
                ValidationResult(successful = true)
            }
        }
    }

    data class ContainsDigit(val errorMessage: String = ValidationMessages.FIELD_CONTAINS_DIGIT) : ValidationRule() {
        override fun validate(value: String): ValidationResult {
            return if (!value.any { it.isDigit() }) {
                ValidationResult(successful = false, errorMessage = errorMessage)
            } else {
                ValidationResult(successful = true)
            }
        }
    }

    data class ContainsSpecialChar(val errorMessage: String = ValidationMessages.FIELD_CONTAINS_SPECIAL_CHAR) : ValidationRule() {
        override fun validate(value: String): ValidationResult {
            return if (!value.any { !it.isLetterOrDigit() }) {
                ValidationResult(successful = false, errorMessage = errorMessage)
            } else {
                ValidationResult(successful = true)
            }
        }
    }

    data class IsLowercase(val errorMessage: String = ValidationMessages.FIELD_LOWERCASE) : ValidationRule() {
        override fun validate(value: String): ValidationResult {
            return if (value != value.lowercase()) {
                ValidationResult(successful = false, errorMessage = errorMessage)
            } else {
                ValidationResult(successful = true)
            }
        }
    }
}