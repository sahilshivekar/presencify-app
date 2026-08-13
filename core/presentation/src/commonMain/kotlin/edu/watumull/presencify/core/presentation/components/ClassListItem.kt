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
    val activeTillDate = activeTill?.toLocalDate()
    val activeFromDate = activeFrom?.toLocalDate()
    val today = DateTimeUtils.getCurrentDate()
    val isInactive =
        (activeTillDate != null && today > activeTillDate) ||
                (activeFromDate != null && today < activeFromDate)

    PresencifyListItem(
        headlineContent = {
            Column {
                Text(
                    text = courseName,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "Prof. $teacherName",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        supportingContent = {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "Time",
                        modifier = Modifier.size(DesignToken.spacing.lg),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.width(DesignToken.spacing.xs))

                    Text(
                        text = "$startTime - $endTime",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

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

                FlowRow(
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(DesignToken.spacing.sm),
                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(DesignToken.spacing.xs)
                ) {
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
                FlowRow(
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(DesignToken.spacing.sm),
                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(DesignToken.spacing.xs)
                ) {
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
                            Text(
                                text = "Active:",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.width(DesignToken.spacing.sm))

                        Text(
                            text = "$activeFrom - $activeTill",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

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
