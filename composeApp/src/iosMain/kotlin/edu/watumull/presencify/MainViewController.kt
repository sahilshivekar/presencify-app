package edu.watumull.presencify

import androidx.compose.ui.window.ComposeUIViewController
import edu.watumull.presencify.di.initKoin
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import com.softartdev.kronos.Network
import com.softartdev.kronos.sync

@OptIn(ExperimentalTime::class)
fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
        Clock.Network.sync()
    }
) { App() }