package edu.watumull.presencify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.softartdev.kronos.Network
import com.softartdev.kronos.sync
import edu.watumull.presencify.core.presentation.utils.ShareUtils
import edu.watumull.presencify.di.initKoin
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.init
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalTime::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        Clock.Network.sync(applicationContext)

        initKoin(applicationContext)

        // Initialize FileKit for Android so ImagePicker can use it
        FileKit.init(this)

        // Set the activity provider for ShareUtils
        ShareUtils.setActivityProvider { this }

        setContent {
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}