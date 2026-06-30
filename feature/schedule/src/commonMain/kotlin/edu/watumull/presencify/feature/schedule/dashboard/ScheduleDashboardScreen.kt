package edu.watumull.presencify.feature.schedule.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import edu.watumull.presencify.core.designsystem.Res
import edu.watumull.presencify.core.designsystem.apartment_24
import edu.watumull.presencify.core.designsystem.clock_icon
import edu.watumull.presencify.core.designsystem.components.PresencifyDefaultLoadingScreen
import edu.watumull.presencify.core.designsystem.components.PresencifyListItem
import edu.watumull.presencify.core.designsystem.components.shimmerEffect
import edu.watumull.presencify.core.designsystem.round_menu_book_24
import edu.watumull.presencify.core.designsystem.theme.DesignToken
import edu.watumull.presencify.core.domain.model.auth.UserRole
import edu.watumull.presencify.core.domain.model.schedule.UpcomingClass
import edu.watumull.presencify.core.presentation.UiConstants
import edu.watumull.presencify.core.presentation.utils.DateTimeUtils
import edu.watumull.presencify.core.presentation.utils.toReadableString
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun ScheduleDashboardScreen(
    state: ScheduleDashboardState,
    currentUserRole: UserRole?,
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
                    if (currentUserRole == UserRole.STUDENT || currentUserRole == UserRole.TEACHER) {
                        DashboardSection(title = "Upcoming Classes") {
                            UpcomingClassesGrid(
                                upcomingClasses = state.upcomingClasses,
                                userRole = currentUserRole,
                                isLoading = state.isLoadingUpcomingClasses,
                                errorMessage = state.upcomingClassesError?.asString()
                            )
                        }
                    }

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
private fun UpcomingClassesGrid(
    upcomingClasses: List<UpcomingClass>,
    userRole: UserRole,
    isLoading: Boolean,
    errorMessage: String?
) {
    when {
        isLoading -> {
            UpcomingClassesShimmer()
        }
        errorMessage != null -> {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }
        upcomingClasses.isEmpty() -> {
            Text(
                text = "No upcoming classes",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        else -> {
            LazyHorizontalGrid(
                rows = GridCells.Fixed(4),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(DesignToken.spacing.huge * 6),
                horizontalArrangement = Arrangement.spacedBy(DesignToken.spacing.md),
                verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.md)
            ) {
                items(
                    items = upcomingClasses,
                    key = { it.id }
                ) { upcomingClass ->
                    UpcomingClassListItem(
                        upcomingClass = upcomingClass,
                        userRole = userRole,
                        modifier = Modifier.width(DesignToken.spacing.huge * 5)
                    )
                }
            }
        }
    }
}

@Composable
private fun UpcomingClassesShimmer() {
    LazyHorizontalGrid(
        rows = GridCells.Fixed(4),
        modifier = Modifier
            .fillMaxWidth()
            .height(DesignToken.spacing.huge * 6),
        horizontalArrangement = Arrangement.spacedBy(DesignToken.spacing.md),
        verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.md)
    ) {
        items(count = 8) {
            Box(
                modifier = Modifier
                    .width(DesignToken.spacing.huge * 5)
                    .height(DesignToken.spacing.huge)
                    .shimmerEffect()
            )
        }
    }
}

@Composable
private fun UpcomingClassListItem(
    upcomingClass: UpcomingClass,
    userRole: UserRole,
    modifier: Modifier = Modifier
) {
    PresencifyListItem(
        modifier = modifier,
        headlineContent = {
            Text(
                text = upcomingClass.course.name,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        supportingContent = {
            Column(verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.xs)) {
                Text(
                    text = upcomingClass.supportingText(userRole),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${upcomingClass.startTime.toReadableString()} - ${upcomingClass.endTime.toReadableString()} | ${upcomingClass.room.roomNumber}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        leadingContent = {
            Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(DesignToken.icons.md)
            )
        },
        trailingContent = {
            UpcomingClassDateLabel(upcomingClass = upcomingClass)
        }
    )
}

@Composable
private fun UpcomingClassDateLabel(upcomingClass: UpcomingClass) {
    val today = DateTimeUtils.getCurrentDate()
    val daysUntilClass = upcomingClass.nextClassDate.toEpochDays() - today.toEpochDays()
    val currentTime = DateTimeUtils.getCurrentTime()
    val label = when (daysUntilClass) {
        0L -> {
            val remainingMinutes = (upcomingClass.startTime.hour * 60 + upcomingClass.startTime.minute) -
                    (currentTime.hour * 60 + currentTime.minute)
            val remainingHours = ((remainingMinutes.coerceAtLeast(0) + 59) / 60)
            "today ${remainingHours}h"
        }
        1L -> "tomorrow"
        else -> if (daysUntilClass > 1L) "${daysUntilClass}d" else null
    }

    if (label != null) {
        val containerColor = when (daysUntilClass) {
            0L -> MaterialTheme.colorScheme.errorContainer
            1L -> MaterialTheme.colorScheme.tertiaryContainer
            else -> MaterialTheme.colorScheme.secondaryContainer
        }
        val contentColor = when (daysUntilClass) {
            0L -> MaterialTheme.colorScheme.onErrorContainer
            1L -> MaterialTheme.colorScheme.onTertiaryContainer
            else -> MaterialTheme.colorScheme.onSecondaryContainer
        }

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = contentColor,
            modifier = Modifier
                .clip(MaterialTheme.shapes.extraSmall)
                .background(containerColor)
                .padding(horizontal = DesignToken.spacing.sm, vertical = DesignToken.spacing.xs)
        )
    }
}

private fun UpcomingClass.supportingText(userRole: UserRole): String {
    return when (userRole) {
        UserRole.STUDENT -> "Prof. ${teacher.firstName} ${teacher.lastName}"
        UserRole.TEACHER -> teacherClassContext()
        UserRole.ADMIN -> ""
    }
}

private fun UpcomingClass.teacherClassContext(): String {
    val matchingClass = course.classes?.firstOrNull { it.id == id }
    val batch = matchingClass?.batch
    val division = matchingClass?.timetable?.division ?: batch?.division
    val semester = division?.semester ?: course.branchCourseSemesters?.firstOrNull()?.semesterNumber
    val semesterText = when (semester) {
        is edu.watumull.presencify.core.domain.model.academics.Semester -> "Sem ${semester.semesterNumber.value}"
        is edu.watumull.presencify.core.domain.enums.SemesterNumber -> "Sem ${semester.value}"
        else -> null
    }
    val divisionOrBatchText = when {
        batch != null -> "Batch ${batch.batchCode}"
        division != null -> "Div ${division.divisionCode}"
        else -> null
    }

    return listOfNotNull(semesterText, divisionOrBatchText)
        .joinToString(" - ")
        .ifBlank { course.code }
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
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(DesignToken.icons.md)
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

