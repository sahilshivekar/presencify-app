package edu.watumull.presencify.core.data.di

import com.softartdev.kronos.Network
import edu.watumull.presencify.core.domain.NtpClock
import org.koin.dsl.module
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

private class KronosNtpClockImpl : NtpClock {
    @OptIn(ExperimentalTime::class)
    override fun getCurrentNtpTimeMs(): Long {
        return Clock.Network.now().toEpochMilliseconds()
    }
}

val clockModule = module {
    single<NtpClock> { KronosNtpClockImpl() }
}
