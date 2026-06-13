package edu.watumull.presencify.feature.users.review_student_biometrics

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter.Companion.tint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import edu.watumull.presencify.core.designsystem.components.PresencifyButton
import edu.watumull.presencify.core.designsystem.components.PresencifyDefaultLoadingScreen
import edu.watumull.presencify.core.designsystem.components.PresencifyNoResultsIndicator
import edu.watumull.presencify.core.designsystem.components.PresencifyOutlinedButton
import edu.watumull.presencify.core.designsystem.components.PresencifyScaffold
import edu.watumull.presencify.core.designsystem.components.dialog.PresencifyAlertDialog
import edu.watumull.presencify.core.designsystem.components.shimmerEffect
import edu.watumull.presencify.core.designsystem.theme.DesignToken
import edu.watumull.presencify.core.domain.enums.BiometricVerificationStatus
import edu.watumull.presencify.core.presentation.UiConstants

@Composable
fun ReviewStudentBiometricsScreen(
    state: ReviewStudentBiometricsState,
    onAction: (ReviewStudentBiometricsAction) -> Unit
) {
    PresencifyScaffold(
        backPress = { onAction(ReviewStudentBiometricsAction.NavigateBack) },
        topBarTitle = "Review Biometrics",
    ) { paddingValues ->
        when (state.viewState) {
            is ReviewStudentBiometricsState.ViewState.Loading -> {
                PresencifyDefaultLoadingScreen()
            }

            is ReviewStudentBiometricsState.ViewState.Error -> {
                PresencifyNoResultsIndicator(
                    text = "Error: ${state.viewState.error.asString()}"
                )
            }

            is ReviewStudentBiometricsState.ViewState.Content -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .widthIn(max = UiConstants.MAX_CONTENT_WIDTH)
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(horizontal = DesignToken.spacing.lg),
                    ) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Spacer(modifier = Modifier.height(DesignToken.spacing.md))
                        }

                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.xxs),
                                    horizontalAlignment = Alignment.Start
                                ) {
                                    Text(
                                        text = "Biometric Verification Status:",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium
                                    )
                                    state.biometricStatus?.let {
                                        BiometricStatusChip(status = it)
                                    }
                                }
                            }
                        }

                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Spacer(modifier = Modifier.height(DesignToken.spacing.md))
                        }

                        if (state.presignedUrls.isEmpty()) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                PresencifyNoResultsIndicator(
                                    text = "No biometric images available for this student."
                                )
                            }
                        } else {
                            items(state.presignedUrls.size) { index ->
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1f),
                                    shape = MaterialTheme.shapes.medium
                                ) {
                                    SubcomposeAsyncImage(
                                        model = state.presignedUrls[index],
                                        contentDescription = "Biometric image $index",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize().padding(DesignToken.spacing.sm),
                                        loading = {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .shimmerEffect()
                                            )
                                        },
                                        error = {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .background(MaterialTheme.colorScheme.onErrorContainer)
                                                    .padding(DesignToken.spacing.sm),
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Rounded.Warning,
                                                    contentDescription = "Image load failed",
                                                    tint = MaterialTheme.colorScheme.errorContainer,
                                                    modifier = Modifier.size(DesignToken.icons.xxxl)
                                                )
                                                Spacer(modifier = Modifier.height(DesignToken.spacing.xxs))
                                                Text(
                                                    text = "Failed to load image",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.errorContainer,
                                                    modifier = Modifier.padding(top = DesignToken.spacing.sm)
                                                )
                                            }
                                        },
                                        success = {
                                            SubcomposeAsyncImageContent()
                                        }
                                    )
                                }
                            }
                        }

                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Spacer(modifier = Modifier.height(DesignToken.spacing.lg))
                        }

                        if (state.biometricStatus == BiometricVerificationStatus.PENDING_REVIEW) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                PresencifyButton(
                                    text = "Approve Biometrics",
                                    onClick = {
                                        onAction(
                                            ReviewStudentBiometricsAction.ApproveStudentBiometrics
                                        )
                                    },
                                    isLoading = state.isApproving,
                                    enabled = !state.isRejecting,
                                    modifier = Modifier.padding(
                                        horizontal = DesignToken.spacing.lg
                                    )
                                )
                            }
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                Spacer(modifier = Modifier.height(DesignToken.spacing.sm))
                            }
                        }

                        if (state.biometricStatus == BiometricVerificationStatus.PENDING_REVIEW ||
                            state.biometricStatus == BiometricVerificationStatus.APPROVED) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                PresencifyOutlinedButton(
                                    text = "Reject Biometrics",
                                    onClick = {
                                        onAction(
                                            ReviewStudentBiometricsAction.RejectStudentBiometrics
                                        )
                                    },
                                    isLoading = state.isRejecting,
                                    enabled = !state.isApproving,
                                    modifier = Modifier
                                        .widthIn(max = 800.dp)
                                        .fillMaxWidth()
                                        .padding(horizontal = DesignToken.spacing.lg)
                                )
                            }
                        }

                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Spacer(modifier = Modifier.height(DesignToken.spacing.lg))
                        }
                    }
                }

            }
        }
    }

    state.dialogState?.let { dialogState ->
        PresencifyAlertDialog(
            title = dialogState.title?.asString(),
            message = dialogState.message?.asString() ?: "",
            dialogType = dialogState.dialogType,
            onDismiss = { onAction(ReviewStudentBiometricsAction.DismissDialog) }
        )
    }
}


@Composable
private fun BiometricStatusChip(status: BiometricVerificationStatus) {
    val (backgroundColor, labelColor, label) = when (status) {
        BiometricVerificationStatus.APPROVED -> {
            Triple(
                MaterialTheme.colorScheme.primaryContainer,
                MaterialTheme.colorScheme.onPrimaryContainer,
                "✓ Approved"
            )
        }
        BiometricVerificationStatus.REJECTED -> {
            Triple(
                MaterialTheme.colorScheme.errorContainer,
                MaterialTheme.colorScheme.onErrorContainer,
                "✗ Rejected"
            )
        }
        BiometricVerificationStatus.PENDING_REVIEW -> {
            Triple(
                MaterialTheme.colorScheme.secondaryContainer,
                MaterialTheme.colorScheme.onSecondaryContainer,
                "⏳ Pending Review"
            )
        }
        BiometricVerificationStatus.NOT_SUBMITTED -> {
            Triple(
                MaterialTheme.colorScheme.tertiaryContainer,
                MaterialTheme.colorScheme.onTertiaryContainer,
                "○ Not Submitted"
            )
        }
    }

    AssistChip(
        onClick = {},
        label = {
            Text(
                text = label,
                color = labelColor,
                style = MaterialTheme.typography.labelMedium
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = backgroundColor
        ),
        shape = MaterialTheme.shapes.medium
    )
}


