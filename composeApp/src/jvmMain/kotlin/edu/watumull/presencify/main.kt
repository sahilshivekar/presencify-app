package edu.watumull.presencify

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.softartdev.kronos.Network
import com.softartdev.kronos.sync
import edu.watumull.presencify.di.initKoin
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun main() = application {
    Clock.Network.sync()

    initKoin()

    Window(
        onCloseRequest = ::exitApplication,
        title = "Presencify",
    ) {
        App()
    }
}

