package edu.watumull.presencify.feature.academics.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
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
fun AcademicsDashboardScreen(
    state: AcademicsDashboardState,
    onAction: (AcademicsDashboardAction) -> Unit
) {
    when (state.viewState) {
        AcademicsDashboardState.ViewState.Loading -> PresencifyDefaultLoadingScreen()
        AcademicsDashboardState.ViewState.Content -> {
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
                    // Section 1: Curriculum and Governance
                    DashboardSection(title = "Curriculum and Governance") {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            DashboardRow {
                                DashboardItem(
                                    text = "Branch",
                                    icon = Res.drawable.branch_24,
                                    onClick = { onAction(AcademicsDashboardAction.ClickBranch) }
                                )
                                DashboardItem(
                                    text = "Scheme",
                                    icon = Res.drawable.scheme_24,
                                    onClick = { onAction(AcademicsDashboardAction.ClickScheme) }
                                )
                            }
                            DashboardRow {
                                DashboardItem(
                                    text = "Course",
                                    icon = Res.drawable.round_menu_book_24,
                                    onClick = { onAction(AcademicsDashboardAction.ClickCourse) }
                                )
                                DashboardItem(
                                    text = "University",
                                    icon = Res.drawable.apartment_24,
                                    onClick = { onAction(AcademicsDashboardAction.ClickUniversity) }
                                )
                            }
                        }
                    }

                    // Section 2: Academic Time & Cohort
                    DashboardSection(title = "Academic Time & cohort") {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            // Full width row for Semesters
                            DashboardItem(
                                text = "Semesters",
                                icon = Res.drawable.clock_icon,
                                onClick = { onAction(AcademicsDashboardAction.ClickSemester) },
                                modifier = Modifier.fillMaxWidth()
                            )

                            DashboardRow {
                                DashboardItem(
                                    text = "Division",
                                    icon = Res.drawable.group_division,
                                    onClick = { onAction(AcademicsDashboardAction.ClickDivision) }
                                )
                                DashboardItem(
                                    text = "Batch",
                                    icon = Res.drawable.group_batch,
                                    onClick = { onAction(AcademicsDashboardAction.ClickBatch) }
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
private fun DashboardItem(
    text: String,
    icon: DrawableResource,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    PresencifyListItem(
        modifier = modifier,
        headlineContent = {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        leadingContent = {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        },
        trailingContent = {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline
            )
        },
        onClick = onClick
    )
}

@Composable
private fun RowScope.DashboardItem(
    text: String,
    icon: DrawableResource,
    onClick: () -> Unit
) {
    DashboardItem(
        text = text,
        icon = icon,
        onClick = onClick,
        modifier = Modifier.weight(1f)
    )
}