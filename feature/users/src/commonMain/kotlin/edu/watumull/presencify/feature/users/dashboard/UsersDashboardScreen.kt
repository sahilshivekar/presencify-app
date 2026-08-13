package edu.watumull.presencify.feature.users.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import edu.watumull.presencify.core.designsystem.Res
import edu.watumull.presencify.core.designsystem.add_to_semester
import edu.watumull.presencify.core.designsystem.baseline_person_remove_24
import edu.watumull.presencify.core.designsystem.components.PresencifyActionBar
import edu.watumull.presencify.core.designsystem.components.PresencifyDefaultLoadingScreen
import edu.watumull.presencify.core.designsystem.edit_outlined
import edu.watumull.presencify.core.designsystem.group_batch
import edu.watumull.presencify.core.designsystem.group_division
import edu.watumull.presencify.core.designsystem.theme.DesignToken
import edu.watumull.presencify.core.domain.model.auth.UserRole
import edu.watumull.presencify.core.presentation.UiConstants
import edu.watumull.presencify.core.presentation.composition_locals.LocalUserRole

@Composable
fun UsersDashboardScreen(
    state: UsersDashboardState,
    onAction: (UsersDashboardAction) -> Unit
) {
    when (state.viewState) {
        UsersDashboardState.ViewState.Loading -> PresencifyDefaultLoadingScreen()
        UsersDashboardState.ViewState.Content -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(DesignToken.spacing.lg),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier.widthIn(max = UiConstants.MAX_CONTENT_WIDTH),
                    verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.xl)
                ) {
                    DashboardSection(title = "Users") {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(DesignToken.spacing.md)
                        ) {
                            PresencifyActionBar(
                                text = "Students",
                                leadingImageVector = Icons.Default.Person,
                                onClick = { onAction(UsersDashboardAction.ClickStudents) },
                                modifier = Modifier.weight(1f)
                            )
                            PresencifyActionBar(
                                text = "Teachers",
                                leadingImageVector = Icons.Default.AccountBox,
                                onClick = { onAction(UsersDashboardAction.ClickTeachers) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    if (LocalUserRole.current == UserRole.ADMIN) {
                        DashboardSection(title = "Assign students") {
                            Column(verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.md)) {
                                PresencifyActionBar(
                                    text = "Assign/Unassign Student to Semester",
                                    leadingIcon = Res.drawable.add_to_semester,
                                    onClick = { onAction(UsersDashboardAction.ClickAssignUnassignSemester) },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                PresencifyActionBar(
                                    text = "Assign/Unassign Student to Division",
                                    leadingIcon = Res.drawable.group_division,
                                    onClick = { onAction(UsersDashboardAction.ClickAssignUnassignDivision) },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                PresencifyActionBar(
                                    text = "Assign/Unassign Student to Batch",
                                    leadingIcon = Res.drawable.group_batch,
                                    onClick = { onAction(UsersDashboardAction.ClickAssignUnassignBatch) },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                PresencifyActionBar(
                                    text = "Modify Student's Division",
                                    leadingIcon = Res.drawable.edit_outlined,
                                    onClick = { onAction(UsersDashboardAction.ClickModifyDivision) },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                PresencifyActionBar(
                                    text = "Modify Student's Batch",
                                    leadingIcon = Res.drawable.edit_outlined,
                                    onClick = { onAction(UsersDashboardAction.ClickModifyBatch) },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        DashboardSection(title = "Dropout students management") {
                            PresencifyActionBar(
                                text = "Mark/Unmark Student as Dropout",
                                leadingIcon = Res.drawable.baseline_person_remove_24,
                                onClick = { onAction(UsersDashboardAction.ClickMarkUnmarkStudentAsDropout) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        DashboardSection(title = "Import from CSV") {
                            Column(verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.md)) {
                                PresencifyActionBar(
                                    text = "Import Students from CSV",
                                    leadingImageVector = Icons.Default.Description,
                                    onClick = { onAction(UsersDashboardAction.ClickImportStudents) },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                PresencifyActionBar(
                                    text = "Import Teachers from CSV",
                                    leadingImageVector = Icons.Default.Description,
                                    onClick = { onAction(UsersDashboardAction.ClickImportTeachers) },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DashboardSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.md)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        content()
    }
}
