package edu.watumull.presencify.core.presentation.validation

import edu.watumull.presencify.core.domain.model.academics.Semester

fun Semester?.validateAsSemester(): ValidationResult {
    if (this == null) {
        return ValidationResult(successful = false, errorMessage = "Semester must be selected")
    }
    return ValidationResult(successful = true)
}