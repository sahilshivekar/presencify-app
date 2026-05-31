package edu.watumull.presencify.feature.attendance.recognize_student

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import edu.watumull.presencify.core.designsystem.components.PresencifyNoResultsIndicator
import edu.watumull.presencify.core.designsystem.components.PresencifyScaffold
import edu.watumull.presencify.core.designsystem.components.dialog.PresencifyAlertDialog
import edu.watumull.presencify.core.designsystem.theme.DesignToken

@Composable
fun RecognizeStudentScreen(
    state: RecognizeStudentState,
    onAction: (RecognizeStudentAction) -> Unit
) {
    // Notify ViewModel when screen becomes visible so it can start the 90s timeout
    LaunchedEffect(Unit) {
        onAction(RecognizeStudentAction.OnScreenStarted)
    }

    // Ensure we cancel the timeout when screen is disposed/navigated away
    DisposableEffect(Unit) {
        onDispose {
            onAction(RecognizeStudentAction.OnScreenStopped)
        }
    }

    PresencifyScaffold(
        topBarTitle = "Face Verification",
        backPress = { onAction(RecognizeStudentAction.NavigateBack) }
    ) { padding ->
        when (state.viewState) {
            is RecognizeStudentState.ViewState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }

            is RecognizeStudentState.ViewState.Error -> {
                PresencifyNoResultsIndicator(
                    text = state.viewState.message.asString()
                )
            }

            is RecognizeStudentState.ViewState.Content -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    RecognizeStudentCamera(
                        modifier = Modifier.fillMaxSize(),
                        onFaceDetected = { yaw ->
                            onAction(RecognizeStudentAction.OnFaceDetected(yaw))
                        },
                        onEmbeddingExtracted = { original, mirrored ->
                            onAction(RecognizeStudentAction.OnRecognitionSuccess(original, mirrored))
                            onAction(RecognizeStudentAction.OnEmbeddingCaptureConsumed)
                        },
                        isLivenessComplete = state.isLivenessComplete,
                        shouldCaptureEmbedding = state.shouldCaptureEmbedding,
                        cameraPermissionGranted = state.cameraPermissionGranted,
                        onPermissionResult = { isGranted ->
                            onAction(RecognizeStudentAction.OnPermissionResult(isGranted))
                        },
                        onCheatingDetected = {
                            onAction(RecognizeStudentAction.OnCheatingDetected)
                        }
                    )

                    // Liveness Instructions Overlay
                    if (!state.isLivenessComplete && state.error == null && state.cameraPermissionGranted) {
                        LivenessOverlay(
                            sequence = state.livenessSequence,
                            currentStep = state.currentStep,
                            modifier = Modifier.align(Alignment.TopCenter).padding(top = DesignToken.spacing.xxl)
                        )
                    }

                    // Loading / Recognizing Indicator
                    if (state.isLoading || state.isRecognizing) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(DesignToken.spacing.lg))
                                Text(
                                    text = if (state.isRecognizing) "Verifying Identity..." else "Loading...",
                                    color = Color.White,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog for face not recognized / cheating / generic errors
    state.dialogState?.let { dialogState ->
        PresencifyAlertDialog(
            dialogType = dialogState.dialogType,
            title = dialogState.title,
            message = dialogState.message.asString(),
            onConfirm = {
                onAction(RecognizeStudentAction.OnRetryFromDialog)
            },
            onDismiss = {
                onAction(RecognizeStudentAction.OnDismissDialog)
            }
        )
    }
}

@Composable
private fun LivenessOverlay(
    sequence: List<HeadMovement>,
    currentStep: Int,
    modifier: Modifier = Modifier
) {
    if (currentStep >= sequence.size) return

    val currentMovement = sequence[currentStep]
    val instruction = when (currentMovement) {
        HeadMovement.LEFT -> "Turn Head LEFT"
        HeadMovement.RIGHT -> "Turn Head RIGHT"
        HeadMovement.STRAIGHT -> "Look STRAIGHT"
    }

    Box(
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.6f), shape = MaterialTheme.shapes.medium)
            .padding(DesignToken.spacing.lg)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Step ${currentStep + 1}/${sequence.size}",
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(DesignToken.spacing.sm))
            Text(
                text = instruction,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}
