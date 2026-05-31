package edu.watumull.presencify.feature.schedule.timetable_details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import edu.watumull.presencify.core.designsystem.components.PresencifyActionBar
import edu.watumull.presencify.core.designsystem.components.PresencifyDefaultLoadingScreen
import edu.watumull.presencify.core.designsystem.components.PresencifyNoResultsIndicator
import edu.watumull.presencify.core.designsystem.components.PresencifyScaffold
import edu.watumull.presencify.core.designsystem.components.PresencifyTextButton
import edu.watumull.presencify.core.designsystem.components.dialog.PresencifyAlertDialog
import edu.watumull.presencify.core.designsystem.theme.DesignToken
import edu.watumull.presencify.core.domain.enums.DayOfWeek
import edu.watumull.presencify.core.domain.model.auth.UserRole
import edu.watumull.presencify.core.presentation.UiConstants
import edu.watumull.presencify.core.presentation.components.ClassListItem
import edu.watumull.presencify.core.presentation.components.TimetableListItem
import edu.watumull.presencify.core.presentation.composition_locals.LocalUserRole
import edu.watumull.presencify.core.presentation.utils.DateTimeUtils
import edu.watumull.presencify.core.presentation.utils.toReadableString
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun TimetableDetailsScreen(
    state: TimetableDetailsState,
    onAction: (TimetableDetailsAction) -> Unit,
) {
    PresencifyScaffold(
        backPress = { onAction(TimetableDetailsAction.NavigateBack) },
        topBarTitle = "Timetable Details",
    ) { paddingValues ->
        when (state.viewState) {
            is TimetableDetailsState.ViewState.Loading -> {
                PresencifyDefaultLoadingScreen()
            }

            is TimetableDetailsState.ViewState.Error -> {
                PresencifyNoResultsIndicator(text = state.viewState.message.asString())
            }

            is TimetableDetailsState.ViewState.Content -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(
                        modifier = Modifier
                            .widthIn(max = UiConstants.MAX_CONTENT_WIDTH)
                            .fillMaxSize()
                    ) {
                        // Timetable Info Card
                        Column(
                            modifier = Modifier.padding(DesignToken.spacing.lg)
                        ) {
                            state.timetable?.let { timetable ->
                                val division = timetable.division
                                val semester = division?.semester
                                val branch = semester?.branch

                                // Determine year from semester number
                                val year = semester?.semesterNumber?.let { semNum ->
                                    when (semNum.value) {
                                        1, 2 -> "FE"
                                        3, 4 -> "SE"
                                        5, 6 -> "TE"
                                        7, 8 -> "BE"
                                        else -> "Unknown"
                                    }
                                } ?: "Unknown"

                                TimetableListItem(
                                    branchAbbreviation = branch?.abbreviation ?: "Unknown Branch",
                                    year = year,
                                    semesterNumber = semester?.semesterNumber
                                        ?: edu.watumull.presencify.core.domain.enums.SemesterNumber.SEMESTER_1,
                                    semesterAcademicStartYear = semester?.academicStartYear ?: 0,
                                    semesterAcademicEndYear = semester?.academicEndYear ?: 0,
                                    divisionCode = division?.divisionCode ?: "Unknown Division",
                                    onClick = null,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            Spacer(modifier = Modifier.height(DesignToken.spacing.lg))
                            
                            if (LocalUserRole.current == UserRole.ADMIN) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    PresencifyTextButton(
                                        onClick = { onAction(TimetableDetailsAction.AddClassClick) },
                                        enabled = !state.isRemovingTimetable
                                    ) {
                                        Text(
                                            text = "Add class",
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }

//                                    PresencifyTextButton(
//                                        onClick = { onAction(TimetableDetailsAction.EditTimetableClick) },
//                                        enabled = !state.isRemovingTimetable
//                                    ) {
//                                        Text(
//                                            text = "Edit timetable",
//                                            color = MaterialTheme.colorScheme.primary
//                                        )
//                                    }

                                    PresencifyTextButton(
                                        onClick = { onAction(TimetableDetailsAction.RemoveTimetableClick) },
                                        enabled = !state.isRemovingTimetable
                                    ) {
                                        if (state.isRemovingTimetable) {
                                            CircularProgressIndicator(
                                                color = MaterialTheme.colorScheme.error,
                                                modifier = Modifier.size(DesignToken.components.progressMd),
                                                strokeWidth = DesignToken.strokes.md,
                                            )
                                        } else {
                                            Text(
                                                text = "Remove timetable",
                                                color = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                }
                            }
                        }


                        // Day Tabs
                        val allDays = listOf(
                            DayOfWeek.MONDAY,
                            DayOfWeek.TUESDAY,
                            DayOfWeek.WEDNESDAY,
                            DayOfWeek.THURSDAY,
                            DayOfWeek.FRIDAY,
                            DayOfWeek.SATURDAY,
                            DayOfWeek.SUNDAY
                        )

                        SecondaryScrollableTabRow(
                            selectedTabIndex = allDays.indexOf(state.selectedDay),
                            divider = {},
                            edgePadding = DesignToken.spacing.lg
                        ) {
                            allDays.forEach { day ->
                                Tab(
                                    selected = state.selectedDay == day,
                                    onClick = { onAction(TimetableDetailsAction.DayTabClick(day)) },
                                    text = {
                                        Text(
                                            text = day.value,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = if (state.selectedDay == day) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                )
                            }
                        }


                        // Classes List for Selected Day
                        if (state.isLoadingClasses) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(DesignToken.spacing.lg),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        } else {
                            val classesForSelectedDay = state.classesByDay[state.selectedDay] ?: emptyList()
                            val today = DateTimeUtils.getCurrentDate()

                            // Filter active and inactive classes
                            val activeClasses = classesForSelectedDay.filter { classSession ->
                                classSession.activeFrom <= today && classSession.activeTill >= today
                            }
                            val inactiveClasses = classesForSelectedDay.filter { classSession ->
                                classSession.activeFrom > today || classSession.activeTill < today
                            }

                            if (classesForSelectedDay.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(DesignToken.spacing.lg),
                                    contentAlignment = Alignment.Center
                                ) {
                                    PresencifyNoResultsIndicator(
                                        text = "No classes scheduled for ${state.selectedDay.value}"
                                    )
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(DesignToken.spacing.lg),
                                    verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.md)
                                ) {
                                    // Active Classes
                                    if (activeClasses.isNotEmpty()) {
                                        items(
                                            items = activeClasses,
                                            key = { it.id }
                                        ) { classSession ->
                                            val division = state.timetable?.division
                                            val semester = division?.semester
                                            val branch = semester?.branch

                                            val semesterText = semester?.let {
                                                "${semester.semesterNumber.value} (${it.academicStartYear}-${it.academicEndYear})"
                                            }

                                            // Build division or batch text
                                            val batch = classSession.batch
                                            val batchText = when {
                                                batch?.batchCode != null -> batch.batchCode
                                                else -> null
                                            }

                                            ClassListItem(
                                                courseName = classSession.course?.name ?: "Unknown Course",
                                                teacherName = classSession.teacher?.let { "${it.firstName} ${it.lastName}" }
                                                    ?: "N/A",
                                                startTime = classSession.startTime.toReadableString(),
                                                endTime = classSession.endTime.toReadableString(),
                                                isExtraClass = classSession.isExtraClass,
                                                roomNumber = classSession.room?.roomNumber,
                                                onClick = { onAction(TimetableDetailsAction.ClassClick(classSession.id)) },
                                                modifier = Modifier.fillMaxWidth(),
                                                divisionOrBatchText = batchText
                                            )
                                        }
                                    }

                                    // Inactive Classes Toggle
                                    if (inactiveClasses.isNotEmpty()) {
                                        item {
                                            PresencifyActionBar(
                                                text = if (state.showInactiveClasses) "Hide Inactive Classes" else "Show Inactive Classes",
                                                onClick = { onAction(TimetableDetailsAction.ToggleShowInactiveClasses) },
                                                trailingIcon = if (state.showInactiveClasses) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }

                                        // Show Inactive Classes if expanded
                                        if (state.showInactiveClasses) {
                                            items(
                                                items = inactiveClasses,
                                                key = { it.id }
                                            ) { classSession ->
                                                val division = state.timetable?.division
                                                val semester = division?.semester
                                                val branch = semester?.branch
                                                val year = semester?.semesterNumber?.let { semNum ->
                                                    when (semNum.value) {
                                                        1, 2 -> "FE"
                                                        3, 4 -> "SE"
                                                        5, 6 -> "TE"
                                                        7, 8 -> "BE"
                                                        else -> "Unknown"
                                                    }
                                                } ?: "Unknown"

                                                // Build semester text (e.g., "FE (2023-2024)")
                                                val semesterText = semester?.let {
                                                    "$year (${it.academicStartYear}-${it.academicEndYear})"
                                                }

                                                // Build division or batch text
                                                val batch = classSession.batch
                                                val divisionOrBatchText = when {
                                                    batch?.batchCode != null -> "Batch ${batch.batchCode}"
                                                    division?.divisionCode != null -> "Division ${division.divisionCode}"
                                                    else -> null
                                                }

                                                ClassListItem(
                                                    courseName = classSession.course?.name ?: "Unknown Course",
                                                    teacherName = classSession.teacher?.let { "${it.firstName} ${it.lastName}" }
                                                        ?: "N/A",
                                                    startTime = classSession.startTime.toReadableString(),
                                                    endTime = classSession.endTime.toReadableString(),
                                                    isExtraClass = classSession.isExtraClass,
                                                    roomNumber = classSession.room?.roomNumber,
                                                    onClick = { onAction(TimetableDetailsAction.ClassClick(classSession.id)) },
                                                    modifier = Modifier.fillMaxWidth()
                                                )
                                            }
                                        }
                                    }

                                    // Show message if only inactive classes exist
                                    if (activeClasses.isEmpty() && inactiveClasses.isNotEmpty() && !state.showInactiveClasses) {
                                        item {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = DesignToken.spacing.xxl),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                PresencifyNoResultsIndicator(
                                                    text = "No active classes for ${state.selectedDay.value}"
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    state.dialogState?.let { dialogState ->
        PresencifyAlertDialog(
            dialogType = dialogState.dialogType,
            title = dialogState.title?.asString(),
            message = dialogState.message.asString(),
            onConfirm = { onAction(TimetableDetailsAction.ConfirmRemoveTimetable) },
            onDismiss = { onAction(TimetableDetailsAction.DismissDialog) }
        )
    }
}
