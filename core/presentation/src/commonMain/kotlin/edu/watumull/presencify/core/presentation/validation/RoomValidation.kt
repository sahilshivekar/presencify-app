package edu.watumull.presencify.core.presentation.validation

import edu.watumull.presencify.core.domain.enums.RoomType
import edu.watumull.presencify.core.domain.model.schedule.Room

fun String?.validateAsRoomNumber(): ValidationResult {
    if (this.isNullOrBlank()) {
        return ValidationResult(successful = false, errorMessage = "Room number cannot be empty")
    }

    val trimmed = this.trim()
    if (trimmed.isEmpty()) {
        return ValidationResult(successful = false, errorMessage = "Room number cannot be empty")
    }

    if (trimmed.length > 50) {
        return ValidationResult(successful = false, errorMessage = "Room number must be less than 50 characters")
    }

    return ValidationResult(successful = true)
}

fun String?.validateAsSittingCapacity(): ValidationResult {
    if (this.isNullOrBlank()) {
        return ValidationResult(successful = false, errorMessage = "Sitting capacity cannot be empty")
    }

    val capacity = this.toIntOrNull() ?: return ValidationResult(
        successful = false,
        errorMessage = "Sitting capacity must be a valid number"
    )

    if (capacity !in 1..10000) {
        return ValidationResult(successful = false, errorMessage = "Sitting capacity must be between 1 and 10000")
    }

    return ValidationResult(successful = true)
}

fun RoomType?.validateAsRoomType(): ValidationResult {
    // Optional field - can be null
    return ValidationResult(successful = true)
}

fun Room?.validateAsRoom(): ValidationResult {
    if (this == null) {
        return ValidationResult(successful = false, errorMessage = "Room must be selected")
    }
    return ValidationResult(successful = true)
}