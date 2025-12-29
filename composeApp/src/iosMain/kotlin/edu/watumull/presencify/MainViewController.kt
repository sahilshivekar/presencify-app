package edu.watumull.presencify

import androidx.compose.ui.window.ComposeUIViewController
import edu.watumull.presencify.di.initKoin

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
    }
) { App() }