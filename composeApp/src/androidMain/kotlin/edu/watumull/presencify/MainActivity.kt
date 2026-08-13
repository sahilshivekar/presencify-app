package edu.watumull.presencify

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.softartdev.kronos.Network
import com.softartdev.kronos.sync
import edu.watumull.presencify.core.presentation.utils.ShareUtils
import edu.watumull.presencify.di.initKoin
import edu.watumull.presencify.navigation.notification.ScheduleNotificationDeepLink
import edu.watumull.presencify.navigation.toScheduleNotificationDeepLink
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.init
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class MainActivity : ComponentActivity() {
    private var scheduleNotificationDeepLink by mutableStateOf<ScheduleNotificationDeepLink?>(null)

    @OptIn(ExperimentalTime::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        scheduleNotificationDeepLink = intent?.toScheduleNotificationDeepLink()

        Clock.Network.sync(applicationContext)

        initKoin(applicationContext)

        FileKit.init(this)

        ShareUtils.setActivityProvider { this }

        setContent {
            App(
                scheduleNotificationDeepLink = scheduleNotificationDeepLink,
                onDeepLinkConsumed = { scheduleNotificationDeepLink = null }
            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        scheduleNotificationDeepLink = intent.toScheduleNotificationDeepLink()
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
