package edu.watumull.presencify.core.presentation.validation

import edu.watumull.presencify.core.domain.model.student.Dropout

fun Dropout?.validateAsDropout(): ValidationResult {
    if (this == null) {
        return ValidationResult(successful = false, errorMessage = "Dropout must be selected")
    }
    return ValidationResult(successful = true)
}