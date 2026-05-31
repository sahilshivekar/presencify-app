package edu.watumull.presencify.core.domain

import kotlinx.coroutines.CoroutineScope

interface NtpClock {
    suspend fun sync()

    fun now(): Long

    fun isSynced(): Boolean

    fun startPeriodicSync(scope: CoroutineScope)
}
