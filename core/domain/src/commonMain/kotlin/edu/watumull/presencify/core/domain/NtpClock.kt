package edu.watumull.presencify.core.domain

interface NtpClock {
    fun getCurrentNtpTimeMs(): Long
}
