package edu.watumull.presencify.core.presentation.validation

import edu.watumull.presencify.core.domain.enums.TeacherRole
import edu.watumull.presencify.core.domain.model.teacher.Teacher

fun TeacherRole?.validateAsTeacherRole(): ValidationResult {
    if (this == null) {
        return ValidationResult(successful = false, errorMessage = "Teacher role must be selected")
    }
    return ValidationResult(successful = true)
}

fun String.validateAsHighestQualification(): ValidationResult {
    // Optional field
    if (this.isBlank()) return ValidationResult(successful = true)

    val maxLength = ValidationRule.MaxLength(255).validate(this)
    if (!maxLength.successful) return maxLength

    return ValidationResult(successful = true)
}

fun Teacher?.validateAsTeacher(): ValidationResult {
    if (this == null) {
        return ValidationResult(successful = false, errorMessage = "Teacher must be selected")
    }
    return ValidationResult(successful = true)
}