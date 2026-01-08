package edu.watumull.presencify.core.presentation.validation

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

fun LocalDate.validateAsStartDate(endDate: LocalDate?): ValidationResult {
    if (endDate != null && this > endDate) {
        return ValidationResult(successful = false, errorMessage = "Start date must be on or before end date")
    }
    return ValidationResult(successful = true)
}

fun LocalDate.validateAsEndDate(startDate: LocalDate?): ValidationResult {
    if (startDate != null && this < startDate) {
        return ValidationResult(successful = false, errorMessage = "End date must be on or after start date")
    }
    return ValidationResult(successful = true)
}

fun LocalTime.validateAsStartTime(endTime: LocalTime?): ValidationResult {
    if (endTime != null && this > endTime) {
        return ValidationResult(successful = false, errorMessage = "Start time must be on or before end time")
    }
    return ValidationResult(successful = true)
}

fun LocalTime.validateAsEndTime(startTime: LocalTime?): ValidationResult {
    if (startTime != null && this < startTime) {
        return ValidationResult(successful = false, errorMessage = "End time must be on or after start time")
    }
    return ValidationResult(successful = true)
}