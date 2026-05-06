package edu.watumull.presencify.feature.schedule.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
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
import edu.watumull.presencify.core.designsystem.Res
import edu.watumull.presencify.core.designsystem.apartment_24
import edu.watumull.presencify.core.designsystem.clock_icon
import edu.watumull.presencify.core.designsystem.components.PresencifyDefaultLoadingScreen
import edu.watumull.presencify.core.designsystem.components.PresencifyListItem
import edu.watumull.presencify.core.designsystem.round_menu_book_24
import edu.watumull.presencify.core.designsystem.theme.DesignToken
import edu.watumull.presencify.core.presentation.UiConstants
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun ScheduleDashboardScreen(
    state: ScheduleDashboardState,
    onAction: (ScheduleDashboardAction) -> Unit
) {
    when (state.viewState) {
        ScheduleDashboardState.ViewState.Loading -> PresencifyDefaultLoadingScreen()
        ScheduleDashboardState.ViewState.Content -> {
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
                    // Section: Rooms and Classes
                    DashboardSection(title = "Rooms and Classes") {
                        DashboardRow {
                            DashboardItem(
                                text = "Room",
                                icon = Res.drawable.apartment_24,
                                onClick = { onAction(ScheduleDashboardAction.ClickRoom) }
                            )
                            DashboardItem(
                                text = "Classes",
                                icon = Res.drawable.round_menu_book_24,
                                onClick = { onAction(ScheduleDashboardAction.ClickClasses) }
                            )
                        }
                        DashboardRow {
                            DashboardItem(
                                text = "Timetable",
                                icon = Res.drawable.clock_icon,
                                onClick = { onAction(ScheduleDashboardAction.ClickTimetable) }
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

@Composable
private fun DashboardRow(content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(DesignToken.spacing.md),
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
                modifier = Modifier.size(DesignToken.icons.md)
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

