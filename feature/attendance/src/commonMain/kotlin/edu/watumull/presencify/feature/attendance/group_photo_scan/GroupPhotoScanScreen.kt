package edu.watumull.presencify.feature.attendance.group_photo_scan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Collections
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import edu.watumull.presencify.core.design.systems.components.PresencifyActionBar
import edu.watumull.presencify.core.design.systems.components.PresencifyButton
import edu.watumull.presencify.core.design.systems.components.PresencifyScaffold
import edu.watumull.presencify.core.design.systems.components.dialog.PresencifyAlertDialog
import edu.watumull.presencify.core.presentation.UiConstants
import edu.watumull.presencify.core.presentation.isDesktopPlatform
import edu.watumull.presencify.core.presentation.utils.ImagePicker
import kotlinx.coroutines.launch

@Composable
fun GroupPhotoScanScreen(
    state: GroupPhotoScanState,
    onAction: (GroupPhotoScanAction) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val isDesktop = isDesktopPlatform()

    PresencifyScaffold(
        backPress = { onAction(GroupPhotoScanAction.NavigateBack) },
        topBarTitle = "Group Photo Scan",
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.TopCenter,
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = UiConstants.MAX_CONTENT_WIDTH)
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    PresencifyActionBar(
                        text = "Upload from device",
                        leadingImageVector = Icons.Default.Collections,
                        onClick = {
                            scope.launch {
                                val images = ImagePicker.pickMultipleImages()
                                if (images.isNotEmpty()) {
                                    onAction(GroupPhotoScanAction.AddImages(images))
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )

                    if (!isDesktop) {
                        PresencifyActionBar(
                            text = "Capture with camera",
                            leadingImageVector = Icons.Default.AddAPhoto,
                            onClick = { onAction(GroupPhotoScanAction.OpenCamera) },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }

                Text(
                    text = "Uploaded images",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                if (state.images.isEmpty()) {
                    Text(
                        text = "No images added yet. Upload or capture classroom photos to continue.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 100.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                    ) {
                        itemsIndexed(state.images) { index, imageBytes ->
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                            ) {
                                AsyncImage(
                                    model = imageBytes,
                                    contentDescription = "Group photo ${index + 1}",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop,
                                )

                                IconButton(
                                    onClick = { onAction(GroupPhotoScanAction.RemoveImage(index)) },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(24.dp)
                                        .background(
                                            MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                                            RoundedCornerShape(bottomStart = 8.dp),
                                        ),
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove",
                                        modifier = Modifier.size(16.dp),
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    PresencifyButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onAction(GroupPhotoScanAction.SubmitGroupPhotoScan) },
                        enabled = !state.isLoading && state.images.isNotEmpty(),
                        isLoading = state.isLoading,
                        text = "Submit",
                    )
                }
            }
        }
    }

    state.dialogState?.let { dialogState ->
        PresencifyAlertDialog(
            isVisible = dialogState.isVisible,
            dialogType = dialogState.dialogType,
            title = dialogState.title,
            message = dialogState.message?.asString() ?: "",
            onConfirm = { onAction(GroupPhotoScanAction.DismissDialog) },
            onDismiss = { onAction(GroupPhotoScanAction.DismissDialog) },
        )
    }
}
