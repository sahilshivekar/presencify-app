package edu.watumull.presencify.core.designsystem.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import edu.watumull.presencify.core.designsystem.theme.DesignToken
import kotlin.math.roundToInt


private fun getAttendanceColor(percentage: Float): Color {
    return when {
        percentage >= 75f -> Color(0xFF4CAF50)
        percentage >= 65f -> Color(0xFFFFEB3B)
        percentage >= 50f -> Color(0xFFFFC107)
        percentage >= 30f -> Color(0xFFFF9800)
        else -> Color(0xFFF44336)
    }
}


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
    strokeWidth: Dp = DesignToken.strokes.extraThick,
    progressColor: Color? = null,
    backgroundColor: Color? = null,
    animate: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    val calculatedPercentage = percentage ?: if (value != null && total != null && total > 0) {
        (value.toFloat() / total.toFloat() * 100f)
    } else {
        0f
    }

    val animatedProgress by animateFloatAsState(
        targetValue = if (animate) calculatedPercentage / 100f else calculatedPercentage / 100f,
        animationSpec = tween(durationMillis = 1000),
        label = "donut_progress"
    )

    val strokeColor = progressColor ?: getAttendanceColor(calculatedPercentage)
    val bgColor = backgroundColor ?: MaterialTheme.colorScheme.surface

    Column(
        modifier = modifier.clickable { onClick?.invoke() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.sm)
    ) {
        val centerTextStyle = when {
            size <= 40.dp -> MaterialTheme.typography.bodySmall
            size <= 64.dp -> MaterialTheme.typography.bodyMedium
            else -> MaterialTheme.typography.titleLarge
        }

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

                drawCircle(
                    color = bgColor,
                    radius = radius,
                    center = center,
                    style = Stroke(width = strokeWidthPx)
                )

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

            Text(
                text = centerText ?: "${calculatedPercentage.roundToInt()}%",
                style = centerTextStyle,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        if (value != null && total != null) {
            Text(
                text = "$value/$total",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }

        if (centerSubtext != null) {
            Text(
                text = centerSubtext,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }

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
                    .padding(horizontal = DesignToken.spacing.sm)
            )
        }
    }
}