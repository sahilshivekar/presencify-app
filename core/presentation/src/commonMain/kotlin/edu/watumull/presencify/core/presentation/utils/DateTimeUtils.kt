package edu.watumull.presencify.core.presentation.utils

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.format
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char

// 08:01 PM
fun LocalTime.toReadableString(): String {
    val amPm = if (this.hour < 12) "AM" else "PM"

    val hour12 = when {
        this.hour == 0 -> 12
        this.hour > 12 -> this.hour - 12
        else -> this.hour
    }

    val timePart = LocalTime(hour12, this.minute).format(LocalTime.Format {
        hour(Padding.ZERO)
        char(':')
        minute(Padding.ZERO)
    })

    return "$timePart $amPm"
}

// day/month/year
fun LocalDate.toReadableString(): String {
    return this.format(LocalDate.Format {
        dayOfMonth(Padding.ZERO)
        char('/')
        monthNumber(Padding.ZERO)
        char('/')
        year()
    })
}