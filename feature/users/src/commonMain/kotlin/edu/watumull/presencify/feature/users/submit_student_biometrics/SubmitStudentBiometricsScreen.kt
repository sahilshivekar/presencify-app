package edu.watumull.presencify.feature.users.submit_student_biometrics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import edu.watumull.presencify.core.designsystem.components.PresencifyButton
import edu.watumull.presencify.core.designsystem.components.PresencifyScaffold
import edu.watumull.presencify.core.designsystem.components.dialog.PresencifyAlertDialog
import edu.watumull.presencify.core.designsystem.theme.DesignToken
import edu.watumull.presencify.core.presentation.UiConstants
import edu.watumull.presencify.core.presentation.utils.ImagePicker
import kotlinx.coroutines.launch

@Composable
fun SubmitStudentBiometricsScreen(
    state: SubmitStudentBiometricsState,
    onAction: (SubmitStudentBiometricsAction) -> Unit
) {
    val scope = rememberCoroutineScope()

    PresencifyScaffold(
        backPress = { onAction(SubmitStudentBiometricsAction.NavigateBack) },
        topBarTitle = "Biometrics",
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = UiConstants.MAX_CONTENT_WIDTH)
                    .fillMaxSize()
                    .padding(DesignToken.spacing.lg)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Add up to 10 face images for the student. Ensure the face is clearly visible in different angles/lighting if possible.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.padding(bottom = DesignToken.spacing.lg)
                )

                PresencifyButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        scope.launch {
                            val images = ImagePicker.pickMultipleImages()
                            if (images.isNotEmpty()) {
                                onAction(SubmitStudentBiometricsAction.AddImages(images))
                            }
                        }
                    },
                    enabled = !state.isLoading && state.images.size < 10,
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(DesignToken.spacing.sm))
                    Text("Add Images")
                }

                Spacer(modifier = Modifier.height(DesignToken.spacing.lg))

                Text(
                    text = "Selected Images: ${state.images.size} / 10",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(DesignToken.spacing.sm))

                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 100.dp),
                    horizontalArrangement = Arrangement.spacedBy(DesignToken.spacing.sm),
                    verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.sm),
                    modifier = Modifier.weight(1f).fillMaxWidth()
                ) {
                    itemsIndexed(state.images) { index, imageBytes ->
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(MaterialTheme.shapes.small)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            AsyncImage(
                                model = imageBytes,
                                contentDescription = "Face Image ${index + 1}",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )

                            IconButton(
                                onClick = { onAction(SubmitStudentBiometricsAction.RemoveImage(index)) },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(DesignToken.icons.md)
                                    .background(
                                        MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                                    )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove",
                                    modifier = Modifier.size(DesignToken.spacing.lg)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(DesignToken.spacing.lg))

                PresencifyButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onAction(SubmitStudentBiometricsAction.SubmitBiometrics) },
                    enabled = !state.isLoading && state.images.isNotEmpty(),
                    isLoading = state.isLoading,
                    text = "Submit Biometrics"
                )
            }
        }
    }

    state.dialogState?.let { dialogState ->
        PresencifyAlertDialog(
            title = dialogState.title?.asString(),
            message = dialogState.message.asString(),
            dialogType = dialogState.dialogType,
            onDismiss = { onAction(SubmitStudentBiometricsAction.DismissDialog) }
        )
    }
}
