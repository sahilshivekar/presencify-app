package edu.watumull.presencify.core.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Badge
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import edu.watumull.presencify.core.design.systems.components.DonutGraph
import edu.watumull.presencify.core.design.systems.components.PresencifyListItem

/**
 * List item component for displaying Attendance information.
 *
 * Shows the attendance date at the top, followed by all ClassListItem content.
 *
 * @param attendanceDate The attendance date in readable format (e.g., "27/02/2026") (required).
 * @param courseName The name of the course (required).
 * @param teacherName The name of the teacher (required).
 * @param startTime Start time in readable format (e.g., "10:00 AM") (required).
 * @param endTime End time in readable format (e.g., "11:00 AM") (required).
 * @param dayOfWeek Day of week (e.g., "Monday") (required).
 * @param feedback Optional feedback message to display.
 * @param onClick Optional click handler for the list item.
 * @param modifier Modifier for the list item.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AttendanceListItem(
    attendanceDate: String,
    courseName: String?,
    teacherName: String,
    startTime: String,
    endTime: String,
    dayOfWeek: String? = null,
    feedback: ListItemFeedback? = null,
    onClick: (() -> Unit)? = null,
    isPresent: Boolean? = null,
    presentCount: Int? = null,
    totalCount: Int? = null,
    modifier: Modifier = Modifier
) {
    PresencifyListItem(
        headlineContent = {
            Column {
                courseName?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Badge(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.tertiary,
                ) {
                    Text(
                        text = attendanceDate,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Teacher name with prefix (same line logically, but wraps if needed)
                Text(
                    text = "Prof. $teacherName",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        trailingContent = {
            if (isPresent != null) {
                Text(
                    text = if (isPresent) "Present" else "Absent",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (isPresent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            } else if (presentCount != null && totalCount != null) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
//                    Text(
//                        text = "$presentCount/$totalCount",
//                        style = MaterialTheme.typography.labelMedium, // Or bodyMedium? labelMedium is slightly smaller/bolder usually
//                        color = MaterialTheme.colorScheme.onSurface
//                    )
//                    Spacer(modifier = Modifier.height(8.dp))

                    val percentage = if (totalCount > 0) (presentCount.toFloat() / totalCount.toFloat()) * 100f else 0f
                    DonutGraph(
                        percentage = percentage,
                        size = 40.dp,
                        strokeWidth = 4.dp,
                        total = totalCount,
                        value = presentCount,
                        animate = false // Disable animation for list items to improve performance
                    )
                }
            }
        },
        supportingContent = {
            Column {
                // Time info with clock icon, Day
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Clock icon
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "Time",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    // Time: "10:00 AM - 11:00 AM"
                    Text(
                        text = "$startTime - $endTime",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Day of week
                    dayOfWeek?.let {
                        Text(
                            text = " • $dayOfWeek",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Feedback section (if any)
                AnimatedVisibility(
                    visible = feedback != null,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    feedback?.let {
                        val (color, message) = when (it) {
                            is ListItemFeedback.Success -> Color.Green to it.message
                            is ListItemFeedback.Error -> MaterialTheme.colorScheme.error to it.message
                        }
                        Column {
                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = message.asString(),
                                style = MaterialTheme.typography.bodySmall,
                                color = color
                            )
                        }
                    }
                }
            }
        },
        onClick = onClick,
        modifier = modifier
    )
}
