package edu.watumull.presencify.core.data.util

import kotlinx.datetime.LocalTime
import kotlinx.datetime.format
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char


// HH:MM:SS format for API calls
fun LocalTime.toApiTimeString(): String {
    return this.format(LocalTime.Format {
        hour(Padding.ZERO)
        char(':')
        minute(Padding.ZERO)
        char(':')
        second(Padding.ZERO)
    })
}