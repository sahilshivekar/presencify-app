package edu.watumull.presencify

import androidx.compose.ui.window.ComposeUIViewController
import com.softartdev.kronos.Network
import com.softartdev.kronos.sync
import edu.watumull.presencify.di.initKoin
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
        Clock.Network.sync()
    }
) { App() }