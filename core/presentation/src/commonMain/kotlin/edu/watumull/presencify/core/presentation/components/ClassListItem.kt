package edu.watumull.presencify.core.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import edu.watumull.presencify.core.design.systems.components.PresencifyListItem
import edu.watumull.presencify.core.presentation.utils.DateTimeUtils
import edu.watumull.presencify.core.presentation.utils.toLocalDate

/**
 * List item component for displaying ClassSession information.
 *
 * @param courseName The name of the course (required).
 * @param teacherName The name of the teacher (required).
 * @param startTime Start time in readable format (e.g., "10:00 AM") (required).
 * @param endTime End time in readable format (e.g., "11:00 AM") (required).
 * @param dayOfWeek Day of week (e.g., "Monday") (required).
 * @param activeFrom Active from date in readable format (e.g., "01/08/2024") (required).
 * @param activeTill Active till date in readable format (e.g., "31/12/2024") (required).
 * @param classType Class type display text (e.g., "Lecture", "Tutorial", "Practical") (required).
 * @param isExtraClass Whether this is an extra class (required).
 * @param roomNumber Room number (optional).
 * @param divisionOrBatchText Division or Batch display text (e.g., "Division A" or "Batch B1") (optional).
 * @param branchAbbreviation Branch abbreviation (e.g., "COMPS", "IT") (optional).
 * @param semesterText Semester and academic year text (e.g., "FE (2023-2024)") (optional).
 * @param feedback Optional feedback message to display.
 * @param onClick Optional click handler for the list item.
 * @param modifier Modifier for the list item.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ClassListItem(
    courseName: String,
    teacherName: String,
    startTime: String,
    endTime: String,
    dayOfWeek: String,
    activeFrom: String,
    activeTill: String,
    classType: String,
    isExtraClass: Boolean,
    roomNumber: String? = null,
    divisionOrBatchText: String? = null,
    branchAbbreviation: String? = null,
    semesterText: String? = null,
    feedback: ListItemFeedback? = null,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    // Check if class is inactive (activeTill is less than today)
    val activeTillDate = activeTill.toLocalDate()
    val activeFromDate = activeFrom.toLocalDate()
    val today = DateTimeUtils.getCurrentDate()
    val isInactive =
        activeTillDate != null && activeFromDate != null && activeTillDate >= today && activeFromDate <= today

    PresencifyListItem(
        headlineContent = {
            Column {
                // Course name
                Text(
                    text = courseName,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Teacher name with prefix (same line logically, but wraps if needed)
                Text(
                    text = "Prof. $teacherName",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
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
                    Text(
                        text = " • $dayOfWeek",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Tags row: Class Type, Room Number, Extra Class (at end)
                FlowRow(
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp),
                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(4.dp)
                ) {
                    // Class Type tag
                    Text(
                        text = classType,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )

                    // Room Number tag
                    roomNumber?.let { room ->
                        Text(
                            text = room,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    // Extra Class tag (red background) - moved to end
                    if (isExtraClass) {
                        Text(
                            text = "Extra",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.Red)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Semester, Division/Batch, Academic Year, Branch in one row with tags
                FlowRow(
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp),
                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(4.dp)
                ) {
                    // Semester tag
                    semesterText?.let { text ->
                        Text(
                            text = text,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.tertiaryContainer)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    // Division/Batch tag
                    divisionOrBatchText?.let { text ->
                        Text(
                            text = text,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    // Branch abbreviation tag
                    branchAbbreviation?.let { branch ->
                        Text(
                            text = branch,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Active/Inactive status with dates
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Status text or tag
                    if (isInactive) {
                        // Inactive tag with red background
                        Text(
                            text = "Inactive",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(horizontal = 4.dp, vertical = 4.dp)
                        )
                    } else {
                        // Active as normal text (no tag)
                        Text(
                            text = "Active:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Dates text
                    Text(
                        text = "$activeFrom - $activeTill",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
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
