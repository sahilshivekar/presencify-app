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
import edu.watumull.presencify.core.designsystem.components.PresencifyListItem
import edu.watumull.presencify.core.designsystem.theme.DesignToken
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
    dayOfWeek: String? = null,
    activeFrom: String? = null,
    activeTill: String? = null,
    classType: String? = null,
    isExtraClass: Boolean,
    roomNumber: String? = null,
    divisionOrBatchText: String? = null,
    branchAbbreviation: String? = null,
    semesterText: String? = null,
    feedback: ListItemFeedback? = null,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    // Check if class is inactive (current date is outside the active period)
    val activeTillDate = activeTill?.toLocalDate()
    val activeFromDate = activeFrom?.toLocalDate()
    val today = DateTimeUtils.getCurrentDate()
    val isInactive =
        (activeTillDate != null && today > activeTillDate) ||
                (activeFromDate != null && today < activeFromDate)

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
                        modifier = Modifier.size(DesignToken.spacing.lg),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.width(DesignToken.spacing.xs))

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
                    if (roomNumber != null && dayOfWeek == null) {
                        Spacer(modifier = Modifier.width(DesignToken.spacing.sm))
                        Text(
                            text = roomNumber,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .clip(MaterialTheme.shapes.extraSmall)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(horizontal = DesignToken.spacing.sm, vertical = DesignToken.spacing.xs)
                        )
                    }
                }
                if (classType != null || roomNumber != null || isExtraClass) {
                    Spacer(modifier = Modifier.height(DesignToken.spacing.sm))
                }

                // Tags row: Class Type, Room Number, Extra Class (at end)
                FlowRow(
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(DesignToken.spacing.sm),
                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(DesignToken.spacing.xs)
                ) {
                    // Class Type tag
                    classType?.let {
                        Text(
                            text = classType,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier
                                .clip(MaterialTheme.shapes.extraSmall)
                                .background(MaterialTheme.colorScheme.secondaryContainer)
                                .padding(horizontal =DesignToken.spacing.sm, vertical = DesignToken.spacing.xs)
                        )
                    }

                    // Room Number tag
                    if (roomNumber != null && dayOfWeek != null) {
                        Text(
                            text = roomNumber,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .clip(MaterialTheme.shapes.extraSmall)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(horizontal =DesignToken.spacing.sm, vertical = DesignToken.spacing.xs)
                        )
                    }

                    // Extra Class tag (red background) - moved to end
                    if (isExtraClass) {
                        Text(
                            text = "Extra",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            modifier = Modifier
                                .clip(MaterialTheme.shapes.extraSmall)
                                .background(Color.Red)
                                .padding(horizontal =DesignToken.spacing.sm, vertical = DesignToken.spacing.xs)
                        )
                    }
                }

                if (semesterText != null && divisionOrBatchText != null && branchAbbreviation != null) {
                    Spacer(modifier = Modifier.height(DesignToken.spacing.sm))
                }
                // Semester, Division/Batch, Academic Year, Branch in one row with tags
                FlowRow(
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(DesignToken.spacing.sm),
                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(DesignToken.spacing.xs)
                ) {
                    // Semester tag
                    semesterText?.let { text ->
                        Text(
                            text = text,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier
                                .clip(MaterialTheme.shapes.extraSmall)
                                .background(MaterialTheme.colorScheme.tertiaryContainer)
                                .padding(horizontal =DesignToken.spacing.sm, vertical = DesignToken.spacing.xs)
                        )
                    }

                    // Division/Batch tag
                    divisionOrBatchText?.let { text ->
                        Text(
                            text = text,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier
                                .clip(MaterialTheme.shapes.extraSmall)
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .padding(horizontal =DesignToken.spacing.sm, vertical = DesignToken.spacing.xs)
                        )
                    }

                    // Branch abbreviation tag
                    branchAbbreviation?.let { branch ->
                        Text(
                            text = branch,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .clip(MaterialTheme.shapes.extraSmall)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(horizontal =DesignToken.spacing.sm, vertical = DesignToken.spacing.xs)
                        )
                    }
                }


                if (activeFrom != null && activeTill != null) {
                    Spacer(modifier = Modifier.height(DesignToken.spacing.xs))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isInactive) {
                            Text(
                                text = "Inactive",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(horizontal = DesignToken.spacing.xs, vertical = DesignToken.spacing.xs)
                            )
                        } else {
                            // Active as normal text (no tag)
                            Text(
                                text = "Active:",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.width(DesignToken.spacing.sm))

                        // Dates text
                        Text(
                            text = "$activeFrom - $activeTill",
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
                            Spacer(modifier = Modifier.height(DesignToken.spacing.sm))
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(DesignToken.spacing.xs))
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
