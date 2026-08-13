package edu.watumull.presencify.core.presentation.utils

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.format
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

object DateTimeUtils {
    fun getCurrentDate(): LocalDate = getCurrentLocalDateTime().date
    fun getCurrentTime(): LocalTime = getCurrentLocalDateTime().time

    @OptIn(ExperimentalTime::class)
    fun getCurrentLocalDateTime(): LocalDateTime {
        val utcString = Clock.System.now().toString().removeSuffix("Z")

        val parts = utcString.split("T")
        val dateParts = parts[0].split("-")
        val timeParts = parts[1].split(":")

        var year = dateParts[0].toInt()
        var month = dateParts[1].toInt()
        var day = dateParts[2].toInt()

        var hour = timeParts[0].toInt()
        var minute = timeParts[1].toInt()

        val secParts = timeParts[2].split(".")
        val second = secParts[0].toInt()
        val nanosecond = if (secParts.size > 1) {
            secParts[1].padEnd(9, '0').take(9).toInt()
        } else 0


        minute += 30
        if (minute >= 60) {
            minute -= 60
            hour += 1
        }

        hour += 5
        if (hour >= 24) {
            hour -= 24
            day += 1
        }

        val daysInCurrentMonth = getDaysInMonth(month, year)

        if (day > daysInCurrentMonth) {
            day -= daysInCurrentMonth
            month += 1

            if (month > 12) {
                month = 1
                year += 1
            }
        }

        return LocalDateTime(year, month, day, hour, minute, second, nanosecond)
    }

    
    private fun getDaysInMonth(month: Int, year: Int): Int {
        return when (month) {
            4, 6, 9, 11 -> 30
            2 -> if (isLeapYear(year)) 29 else 28
            else -> 31
        }
    }

    
    private fun isLeapYear(year: Int): Boolean {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
    }
}


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

fun LocalDate.toReadableString(): String {
    return this.format(LocalDate.Format {
        day(padding = Padding.ZERO)
        char('/')
        monthNumber(Padding.ZERO)
        char('/')
        year()
    })
}

fun String.toLocalDate(): LocalDate? {
    return try {
        val parts = this.split("/")
        if (parts.size == 3) {
            LocalDate(year = parts[2].toInt(),
                month = parts[1].toInt(),
                day = parts[0].toInt()
            )
        } else {
            null
        }
    } catch (e: Exception) {
        null
    }
}
