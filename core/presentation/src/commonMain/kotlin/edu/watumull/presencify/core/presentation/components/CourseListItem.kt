package edu.watumull.presencify.core.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Badge
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import edu.watumull.presencify.core.designsystem.components.PresencifyListItem
import edu.watumull.presencify.core.designsystem.theme.DesignToken

/**
 * List item component for displaying Course information.
 *
 * @param name The name of the course.
 * @param code The course code.
 * @param schemeName The scheme name associated with the course.
 * @param optionalCourse Optional course name if the course is optional.
 * @param feedback Optional feedback message to display.
 * @param trailingContent Optional trailing content composable.
 * @param onClick Optional click handler for the list item.
 * @param modifier Modifier for the list item.
 */
@Composable
fun CourseListItem(
    name: String,
    code: String,
    schemeName: String,
    optionalCourse: String? = null,
    optionalCourseDivisionCodes: List<String> = emptyList(),
    feedback: ListItemFeedback? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    containerColor: Color? = null,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    PresencifyListItem(
        containerColor = containerColor,
        headlineContent = {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        supportingContent = {
            Column {
                Text(
                    text = "Course code: $code",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Scheme: $schemeName",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                optionalCourse?.let {
                    Spacer(modifier = Modifier.height(DesignToken.spacing.xs))
                    Column(verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.xs)) {
                        Badge(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ) {
                            Text(
                                text = "Optional: $it",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                        if (optionalCourseDivisionCodes.isNotEmpty()) {
                            Row(horizontalArrangement = Arrangement.spacedBy(DesignToken.spacing.xs)) {
                                optionalCourseDivisionCodes.forEach { divisionCode ->
                                    Badge(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                    ) {
                                        Text(
                                            text = divisionCode,
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }
                            }
                        }
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
        trailingContent = trailingContent,
        onClick = onClick,
        modifier = modifier
    )
}

@Composable
fun CourseListItemPreview() {
    MaterialTheme {
        CourseListItem(
            name = "Data Structures and Algorithms",
            code = "CS301",
            schemeName = "2023 Scheme",
            onClick = {}
        )
    }
}

@Composable
fun CourseListItemOptionalPreview() {
    MaterialTheme {
        CourseListItem(
            name = "Machine Learning",
            code = "CS405",
            schemeName = "2023 Scheme",
            optionalCourse = "Artificial Intelligence Track"
        )
    }
}

@Composable
fun CourseListItemLongNamePreview() {
    MaterialTheme {
        CourseListItem(
            name = "Advanced Topics in Computer Networks and Security",
            code = "CS501",
            schemeName = "Revised Engineering Curriculum 2023-24",
            optionalCourse = "Cybersecurity Specialization",
            onClick = {}
        )
    }
}
