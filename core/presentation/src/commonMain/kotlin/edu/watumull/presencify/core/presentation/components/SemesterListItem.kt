package edu.watumull.presencify.core.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Badge
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import edu.watumull.presencify.core.designsystem.components.PresencifyListItem
import edu.watumull.presencify.core.designsystem.theme.DesignToken
import edu.watumull.presencify.core.domain.enums.SemesterNumber
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

/**
 * List item component for displaying Semester information.
 *
 * @param semesterNumber The semester number.
 * @param semesterAcademicStartYear The academic start year of the semester.
 * @param semesterAcademicEndYear The academic end year of the semester.
 * @param divisionCodes List of division codes (e.g., ["A", "B", "C"]).
 * @param batchCodes List of batch codes.
 * @param feedback Optional feedback message to display.
 * @param trailingContent Optional trailing content composable.
 * @param onClick Optional click handler for the list item.
 * @param modifier Modifier for the list item.
 */
@Composable
fun SemesterListItem(
    semesterNumber: SemesterNumber,
    semesterAcademicStartYear: Int,
    semesterAcademicEndYear: Int,
    divisionCodes: PersistentList<String>,
    batchCodes: PersistentList<String>,
    branchAbbreviation: String,
    feedback: ListItemFeedback? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    PresencifyListItem(
        headlineContent = {
            Text(
                text = "Sem ${semesterNumber.value} • $semesterAcademicStartYear-$semesterAcademicEndYear",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        supportingContent = {
            Column {
                Text(
                    text = "Divisions: ${if (divisionCodes.isEmpty()) "None" else divisionCodes.joinToString(" • ")}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Batches: ${if (batchCodes.isEmpty()) "None" else batchCodes.joinToString(" • ")}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(DesignToken.spacing.xs))
                Badge(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ) {
                    Text(
                        text = branchAbbreviation,
                        style = MaterialTheme.typography.labelSmall
                    )
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
fun SemesterListItemPreview() {
    MaterialTheme {
        SemesterListItem(
            semesterNumber = SemesterNumber.SEMESTER_5,
            semesterAcademicStartYear = 2023,
            semesterAcademicEndYear = 2024,
            divisionCodes = persistentListOf("A", "B"),
            batchCodes = persistentListOf("B1", "B2", "B3"),
            branchAbbreviation = "CS",
            onClick = {}
        )
    }
}

@Composable
fun SemesterListItemMultipleDivisionsPreview() {
    MaterialTheme {
        SemesterListItem(
            semesterNumber = SemesterNumber.SEMESTER_7,
            semesterAcademicStartYear = 2024,
            semesterAcademicEndYear = 2025,
            divisionCodes = persistentListOf("A", "B", "C", "D"),
            batchCodes = persistentListOf("B1", "B2", "B3", "B4"),
            branchAbbreviation = "EXTC"
        )
    }
}

@Composable
fun SemesterListItemFirstSemPreview() {
    MaterialTheme {
        SemesterListItem(
            semesterNumber = SemesterNumber.SEMESTER_1,
            semesterAcademicStartYear = 2025,
            semesterAcademicEndYear = 2026,
            divisionCodes = persistentListOf("A", "B", "C"),
            batchCodes = persistentListOf("B1", "B2"),
            branchAbbreviation = "MECH",
            onClick = {}
        )
    }
}
