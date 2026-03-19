package edu.watumull.presencify

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.watumull.presencify.core.design.systems.Res
import edu.watumull.presencify.core.design.systems.presencify_logo_circle_svg
import edu.watumull.presencify.core.design.systems.theme.PresencifyTheme
import edu.watumull.presencify.core.domain.model.auth.UserRole
import edu.watumull.presencify.core.presentation.composition_locals.LocalUserId
import edu.watumull.presencify.core.presentation.composition_locals.LocalUserRole
import edu.watumull.presencify.core.presentation.global_snackbar.ObserveAsEvents
import edu.watumull.presencify.core.presentation.global_snackbar.SnackbarController
import edu.watumull.presencify.navigation.AppNavHost
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun App() {
    val viewModel = koinViewModel<AppViewModel>()
    val state by viewModel.state.collectAsState()

    CompositionLocalProvider(
        LocalUserRole provides state.userRole,
        LocalUserId provides state.userId
    ) {

        PresencifyTheme {

            val snackbarHostState = remember { SnackbarHostState() }
            val scope = rememberCoroutineScope()

            ObserveAsEvents(
                SnackbarController.events,
            ) { event ->
                scope.launch {
                    snackbarHostState.currentSnackbarData?.dismiss()

                    val result = snackbarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = event.action?.name,
                        duration = if (event.action == null) SnackbarDuration.Short else SnackbarDuration.Long
                    )

                    if (result == SnackbarResult.ActionPerformed) {
                        event.action?.action()
                    }
                }
            }

            state.startDestination?.let { destination ->
                Scaffold(
                    snackbarHost = {
                        SnackbarHost(
                            hostState = snackbarHostState,
                            modifier = Modifier.padding(16.dp),
                        )
                    },
                    contentWindowInsets = WindowInsets(0.dp)
                ) {
                    AppNavHost(destination)
                }
            } ?: SplashScreen()
        }
    }
}


@Composable
fun SplashScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(Res.drawable.presencify_logo_circle_svg),
            contentDescription = null,
            modifier = Modifier.size(100.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Presencify",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}