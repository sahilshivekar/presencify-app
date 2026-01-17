package edu.watumull.presencify.feature.users.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import edu.watumull.presencify.core.design.systems.*
import edu.watumull.presencify.core.design.systems.components.PresencifyDefaultLoadingScreen
import edu.watumull.presencify.core.design.systems.components.PresencifyListItem
import edu.watumull.presencify.core.presentation.UiConstants
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

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
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier.widthIn(max = UiConstants.MAX_CONTENT_WIDTH),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Section 1: Users
                    DashboardSection(title = "Users") {
                        DashboardRow {
                            DashboardItem(
                                text = "Students",
                                icon = Res.drawable.baseline_person_24,
                                onClick = { onAction(UsersDashboardAction.ClickStudents) }
                            )
                            DashboardItem(
                                text = "Teachers",
                                icon = Res.drawable.baseline_admin_panel_settings_24,
                                onClick = { onAction(UsersDashboardAction.ClickTeachers) }
                            )
                        }
                    }

                    // Section 2: Assign Students
                    DashboardSection(title = "Assign students") {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            DashboardRow {
                                DashboardItem(
                                    text = "Assign students to semester",
                                    icon = Res.drawable.add_to_semester,
                                    onClick = { onAction(UsersDashboardAction.ClickAssignToSemester) }
                                )
                                DashboardItem(
                                    text = "Remove students from semester",
                                    icon = Res.drawable.student_semester_remove,
                                    onClick = { onAction(UsersDashboardAction.ClickRemoveFromSemester) }
                                )
                            }
                            DashboardRow {
                                DashboardItem(
                                    text = "Assign student to division",
                                    icon = Res.drawable.group_division,
                                    onClick = { onAction(UsersDashboardAction.ClickAssignToDivision) }
                                )
                                DashboardItem(
                                    text = "Assign student to batch",
                                    icon = Res.drawable.group_batch,
                                    onClick = { onAction(UsersDashboardAction.ClickAssignToBatch) }
                                )
                            }
                            DashboardRow {
                                DashboardItem(
                                    text = "Modify students division",
                                    icon = Res.drawable.edit_outlined,
                                    onClick = { onAction(UsersDashboardAction.ClickModifyDivision) }
                                )
                                DashboardItem(
                                    text = "Modify students batch",
                                    icon = Res.drawable.edit_outlined,
                                    onClick = { onAction(UsersDashboardAction.ClickModifyBatch) }
                                )
                            }
                            DashboardRow {
                                DashboardItem(
                                    text = "Remove student from division",
                                    icon = Res.drawable.outline_delete_outline_24,
                                    onClick = { onAction(UsersDashboardAction.ClickRemoveFromDivision) }
                                )
                                DashboardItem(
                                    text = "Remove student from batch",
                                    icon = Res.drawable.outline_delete_outline_24,
                                    onClick = { onAction(UsersDashboardAction.ClickRemoveFromBatch) }
                                )
                            }
                        }
                    }

                    // Section 3: Dropout
                    DashboardSection(title = "Dropout students management") {
                        DashboardRow {
                            DashboardItem(
                                text = "Add to dropout",
                                icon = Res.drawable.baseline_person_remove_24,
                                onClick = { onAction(UsersDashboardAction.ClickAddToDropout) }
                            )
                            DashboardItem(
                                text = "Remove from dropout",
                                icon = Res.drawable.outline_person_add_alt_1_24,
                                onClick = { onAction(UsersDashboardAction.ClickRemoveFromDropout) }
                            )
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
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        content()
    }
}

@Composable
private fun DashboardRow(content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        content = content
    )
}

@Composable
private fun RowScope.DashboardItem(
    text: String,
    icon: DrawableResource,
    onClick: () -> Unit
) {
    PresencifyListItem(
        modifier = Modifier.weight(1f),
        headlineContent = {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        leadingContent = {
            androidx.compose.material3.Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        },
        trailingContent = {
            androidx.compose.material3.Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline
            )
        },
        onClick = onClick
    )
}