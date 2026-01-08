package edu.watumull.presencify.core.presentation.validation

data class ValidationResult(
    val successful: Boolean,
    val errorMessage: String? = null
)
