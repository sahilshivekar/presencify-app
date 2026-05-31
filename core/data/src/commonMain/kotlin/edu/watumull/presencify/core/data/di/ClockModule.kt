package edu.watumull.presencify.core.data.di

import com.softartdev.kronos.Network
import edu.watumull.presencify.core.domain.NtpClock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.dsl.module
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

private class KronosNtpClockImpl : NtpClock {
    private var offsetMs: Long = 0L
    private var synced: Boolean = false
    private var periodicSyncJob: Job? = null

    @OptIn(ExperimentalTime::class)
    override suspend fun sync() {
        val ntpTime = Clock.Network.now().toEpochMilliseconds()
        offsetMs = ntpTime - Clock.System.now().toEpochMilliseconds()
        synced = true
    }

    @OptIn(ExperimentalTime::class)
    override fun now(): Long {
        return Clock.System.now().toEpochMilliseconds() + offsetMs
    }

    override fun isSynced(): Boolean {
        return synced
    }

    override fun startPeriodicSync(scope: CoroutineScope) {
        if (periodicSyncJob?.isActive == true) return

        periodicSyncJob = scope.launch {
            while (isActive) {
                delay(5 * 60 * 1000L) // 5 minutes
                sync()
            }
        }
    }
}

val clockModule = module {
    single<NtpClock> { KronosNtpClockImpl() }
}
