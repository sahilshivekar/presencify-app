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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import edu.watumull.presencify.core.design.systems.components.PresencifyActionBar
import edu.watumull.presencify.core.design.systems.components.PresencifyScaffold
import edu.watumull.presencify.core.presentation.UiText
import edu.watumull.presencify.feature.attendance.recognize_student.RecognizeStudentAction.OnCheatingDetected

@Composable
fun RecognizeStudentScreen(
    state: RecognizeStudentState,
    onAction: (RecognizeStudentAction) -> Unit
) {
    PresencifyScaffold(
        topBarTitle = "Face Verification",
        backPress = { onAction(RecognizeStudentAction.NavigateBack) }
    ) { padding ->
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
                    // Pass both embeddings to the ViewModel via a new action
                    onAction(RecognizeStudentAction.OnRecognitionSuccessWithMirror(original, mirrored))
                    onAction(RecognizeStudentAction.OnEmbeddingCaptureConsumed)
                },
                isLivenessComplete = state.isLivenessComplete,
                shouldCaptureEmbedding = state.shouldCaptureEmbedding,
                cameraPermissionGranted = state.cameraPermissionGranted,
                onPermissionResult = { isGranted ->
                    onAction(RecognizeStudentAction.OnPermissionResult(isGranted))
                },
                onCheatingDetected = {
                    onAction(OnCheatingDetected)
                }
            )

            // Liveness Instructions Overlay
            if (!state.isLivenessComplete && state.error == null && state.cameraPermissionGranted) {
                LivenessOverlay(
                    sequence = state.livenessSequence,
                    currentStep = state.currentStep,
                    modifier = Modifier.align(Alignment.TopCenter).padding(top = 32.dp)
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
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (state.isRecognizing) "Verifying Identity..." else "Loading...",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }

            // Error Overlay
            if (state.error != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.error.asString(),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
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
            .padding(16.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Step ${currentStep + 1}/${sequence.size}",
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = instruction,
                color = MaterialTheme.colorScheme.primary, // Or a visible color
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}
