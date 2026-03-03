package edu.watumull.presencify.core.design.systems.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

/**
 * Determines the stroke color based on attendance percentage ranges.
 * - 90-100%: Dark Green
 * - 75-89%: Green
 * - 65-74%: Yellow
 * - 50-64%: Amber/Orange
 * - 30-49%: Orange
 * - 0-29%: Red
 */
private fun getAttendanceColor(percentage: Float): Color {
    return when {
        percentage >= 75f -> Color(0xFF4CAF50) // Green
        percentage >= 65f -> Color(0xFFFFEB3B) // Yellow
        percentage >= 50f -> Color(0xFFFFC107) // Amber
        percentage >= 30f -> Color(0xFFFF9800) // Orange
        else -> Color(0xFFF44336) // Red
    }
}

/**
 * A generic donut graph component that displays progress in a circular format
 * with customizable color schemes and center content.
 *
 * Example usage:
 * ```
 * // Simple percentage display
 * DonutGraph(
 *     percentage = 75f,
 *     centerText = "75%",
 *     label = "Completed"
 * )
 *
 * // Value/Total display with custom colors
 * DonutGraph(
 *     value = 7,
 *     total = 10,
 *     label = "Tasks Completed",
 *     progressBrush = Brush.linearGradient(listOf(Color.Blue, Color.Cyan))
 * )
 * ```
 *
 * @param percentage Progress percentage (0-100). If provided, overrides value/total calculation.
 * @param value Current value (numerator) for automatic percentage calculation.
 * @param total Total value (denominator) for automatic percentage calculation.
 * @param centerText Custom text to display in the center. If null, shows value/total or percentage.
 * @param centerSubtext Custom subtext to display below center text. If null, shows percentage.
 * @param label Text to display below the indicator.
 * @param modifier Modifier for the component.
 * @param size Size of the circular indicator.
 * @param strokeWidth Width of the progress stroke.
 * @param progressColor Color for the progress arc. If null, uses attendance-based color scheme.
 * @param backgroundColor Background color of the track. Defaults to surface color.
 * @param animate Whether to animate the progress when first displayed.
 * @param showPercentage Whether to show percentage in center. Only applies if centerText is null.
 */
@Composable
fun DonutGraph(
    modifier: Modifier = Modifier,
    percentage: Float? = null,
    value: Int? = null,
    total: Int? = null,
    centerText: String? = null,
    centerSubtext: String? = null,
    label: String? = null,
    size: Dp = 100.dp,
    strokeWidth: Dp = 8.dp,
    progressColor: Color? = null,
    backgroundColor: Color? = null,
    animate: Boolean = true,
    showPercentage: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    // Calculate percentage from value/total if not provided directly
    val calculatedPercentage = percentage ?: if (value != null && total != null && total > 0) {
        (value.toFloat() / total.toFloat() * 100f)
    } else {
        0f
    }

    // Animate progress
    val animatedProgress by animateFloatAsState(
        targetValue = if (animate) calculatedPercentage / 100f else calculatedPercentage / 100f,
        animationSpec = tween(durationMillis = 1000),
        label = "donut_progress"
    )

    // Determine colors - use attendance color logic by default
    val strokeColor = progressColor ?: getAttendanceColor(calculatedPercentage)
    val bgColor = backgroundColor ?: MaterialTheme.colorScheme.surface

    Column(
        modifier = modifier.clickable { onClick?.invoke() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Circular Progress Indicator
        Box(
            modifier = Modifier.size(size),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .size(size)
                    .aspectRatio(1f)
            ) {
                val canvasSize = size.toPx()
                val strokeWidthPx = strokeWidth.toPx()
                val radius = (canvasSize - strokeWidthPx) / 2f
                val center = Offset(canvasSize / 2f, canvasSize / 2f)

                // Background circle
                drawCircle(
                    color = bgColor,
                    radius = radius,
                    center = center,
                    style = Stroke(width = strokeWidthPx)
                )

                // Progress arc
                val sweepAngle = 360f * animatedProgress
                drawArc(
                    color = strokeColor,
                    startAngle = -90f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = Offset(strokeWidthPx / 2f, strokeWidthPx / 2f),
                    size = Size(canvasSize - strokeWidthPx, canvasSize - strokeWidthPx),
                    style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
                )
            }

            // Center content
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Main center text
                Text(
                    text = centerText ?: if (value != null && total != null) {
                        "$value/$total"
                    } else {
                        "${calculatedPercentage.roundToInt()}%"
                    },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Subtext (percentage by default if not custom)
                if (showPercentage || centerSubtext != null) {
                    Text(
                        text = centerSubtext ?: if (centerText == null && value != null && total != null) {
                            "${calculatedPercentage.roundToInt()}%"
                        } else {
                            ""
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Label below the indicator
        if (label != null) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            )
        }
    }
}