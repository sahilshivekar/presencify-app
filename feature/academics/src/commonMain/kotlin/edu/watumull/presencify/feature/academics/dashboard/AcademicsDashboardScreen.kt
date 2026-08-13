package edu.watumull.presencify.feature.academics.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import edu.watumull.presencify.core.designsystem.Res
import edu.watumull.presencify.core.designsystem.apartment_24
import edu.watumull.presencify.core.designsystem.branch_24
import edu.watumull.presencify.core.designsystem.clock_icon
import edu.watumull.presencify.core.designsystem.components.PresencifyActionBar
import edu.watumull.presencify.core.designsystem.components.PresencifyDefaultLoadingScreen
import edu.watumull.presencify.core.designsystem.group_batch
import edu.watumull.presencify.core.designsystem.group_division
import edu.watumull.presencify.core.designsystem.round_menu_book_24
import edu.watumull.presencify.core.designsystem.scheme_24
import edu.watumull.presencify.core.designsystem.theme.DesignToken
import edu.watumull.presencify.core.domain.model.auth.UserRole
import edu.watumull.presencify.core.presentation.UiConstants
import edu.watumull.presencify.core.presentation.composition_locals.LocalUserRole

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
                    .padding(DesignToken.spacing.lg),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier.widthIn(max = UiConstants.MAX_CONTENT_WIDTH),
                    verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.xl)
                ) {
                    DashboardSection(title = "Curriculum and Governance") {
                        Column(verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.md)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(DesignToken.spacing.md)
                            ) {
                                PresencifyActionBar(
                                    text = "Branch",
                                    leadingIcon = Res.drawable.branch_24,
                                    onClick = { onAction(AcademicsDashboardAction.ClickBranch) },
                                    modifier = Modifier.weight(1f)
                                )
                                PresencifyActionBar(
                                    text = "Scheme",
                                    leadingIcon = Res.drawable.scheme_24,
                                    onClick = { onAction(AcademicsDashboardAction.ClickScheme) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(DesignToken.spacing.md)
                            ) {
                                PresencifyActionBar(
                                    text = "Course",
                                    leadingIcon = Res.drawable.round_menu_book_24,
                                    onClick = { onAction(AcademicsDashboardAction.ClickCourse) },
                                    modifier = Modifier.weight(1f)
                                )
                                PresencifyActionBar(
                                    text = "University",
                                    leadingIcon = Res.drawable.apartment_24,
                                    onClick = { onAction(AcademicsDashboardAction.ClickUniversity) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (LocalUserRole.current == UserRole.ADMIN) {
                                PresencifyActionBar(
                                    text = "Link/Unlink Courses",
                                    leadingIcon = Res.drawable.round_menu_book_24,
                                    onClick = { onAction(AcademicsDashboardAction.ClickLinkUnlinkCourse) },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }

                    DashboardSection(title = "Academic Time & cohort") {
                        Column(verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.md)) {
                            PresencifyActionBar(
                                text = "Semesters",
                                leadingIcon = Res.drawable.clock_icon,
                                onClick = { onAction(AcademicsDashboardAction.ClickSemester) },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(DesignToken.spacing.md)
                            ) {
                                PresencifyActionBar(
                                    text = "Division",
                                    leadingIcon = Res.drawable.group_division,
                                    onClick = { onAction(AcademicsDashboardAction.ClickDivision) },
                                    modifier = Modifier.weight(1f)
                                )
                                PresencifyActionBar(
                                    text = "Batch",
                                    leadingIcon = Res.drawable.group_batch,
                                    onClick = { onAction(AcademicsDashboardAction.ClickBatch) },
                                    modifier = Modifier.weight(1f)
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
