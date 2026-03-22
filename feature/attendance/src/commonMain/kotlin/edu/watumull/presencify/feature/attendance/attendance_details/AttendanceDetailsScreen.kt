package edu.watumull.presencify.feature.attendance.attendance_details

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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import edu.watumull.presencify.core.design.systems.components.PresencifyScaffold
import edu.watumull.presencify.core.design.systems.components.PresencifyTextButton
import edu.watumull.presencify.core.design.systems.components.dialog.DialogType
import edu.watumull.presencify.core.design.systems.components.dialog.PresencifyAlertDialog
import edu.watumull.presencify.core.domain.model.schedule.ClassSession
import edu.watumull.presencify.core.presentation.UiConstants
import edu.watumull.presencify.core.presentation.components.ClassListItem
import edu.watumull.presencify.core.presentation.components.StudentListItem
import edu.watumull.presencify.core.presentation.utils.toReadableString
import kotlinx.datetime.LocalDate

@Composable
fun AttendanceDetailsScreen(
    state: AttendanceDetailsState,
    onAction: (AttendanceDetailsAction) -> Unit
) {
    PresencifyScaffold(
        backPress = { onAction(AttendanceDetailsAction.BackButtonClick) },
        topBarTitle = "Attendance Details",
        actions = {
            if (state.viewState == AttendanceDetailsState.ViewState.Content && state.attendance != null) {
                IconButton(onClick = {
                    val shareText = buildAttendanceShareText(state)
                    onAction(AttendanceDetailsAction.ShareAttendance(shareText))
                }) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share attendance details",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (state.viewState) {
                AttendanceDetailsState.ViewState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is AttendanceDetailsState.ViewState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = state.viewState.message.asString(),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.error
                            )
                            Button(onClick = { onAction(AttendanceDetailsAction.BackButtonClick) }) {
                                Text("Go Back")
                            }
                        }
                    }
                }

                AttendanceDetailsState.ViewState.Content -> {
                    val allTabs = listOf(
                        AttendanceDetailsState.AttendanceTab.PRESENT,
                        AttendanceDetailsState.AttendanceTab.ABSENT
                    )

                    val attendanceStudents = state.attendance?.attendanceStudents ?: emptyList()
                    val filteredStudents = when (state.selectedTab) {
                        AttendanceDetailsState.AttendanceTab.PRESENT ->
                            attendanceStudents.filter { it.attendanceStatus }
                        AttendanceDetailsState.AttendanceTab.ABSENT ->
                            attendanceStudents.filter { !it.attendanceStatus }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        LazyColumn(
                            modifier = Modifier
                                .widthIn(max = UiConstants.MAX_CONTENT_WIDTH)
                                .fillMaxSize()
                        ) {
                            // Class Details Section
                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp)
                                ) {
                                    Spacer(modifier = Modifier.height(16.dp))

                                    state.classSession?.let { classSession ->
                                        ClassDetailsSection(classSession = classSession, date = state.attendance?.date!!)
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Action Buttons (between class details and stats)
                                    ActionButtonsSection(onAction = onAction)

                                    Spacer(modifier = Modifier.height(24.dp))

                                    // Stats Cards Section
                                    AttendanceStatsSection(
                                        totalStudents = state.totalStudents,
                                        presentStudents = state.presentStudents,
                                        absentStudents = state.absentStudents
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                            }

                            // Sticky Tab Row
                            stickyHeader {
                                SecondaryScrollableTabRow(
                                    selectedTabIndex = allTabs.indexOf(state.selectedTab),
                                    divider = {},
                                    edgePadding = 16.dp,
                                    modifier = Modifier.background(MaterialTheme.colorScheme.background)
                                ) {
                                    allTabs.forEach { tab ->
                                        Tab(
                                            selected = state.selectedTab == tab,
                                            onClick = { onAction(AttendanceDetailsAction.TabClick(tab)) },
                                            text = {
                                                Text(
                                                    text = when (tab) {
                                                        AttendanceDetailsState.AttendanceTab.PRESENT -> "Present"
                                                        AttendanceDetailsState.AttendanceTab.ABSENT -> "Absent"
                                                    },
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = if (state.selectedTab == tab) FontWeight.Bold else FontWeight.Normal
                                                )
                                            }
                                        )
                                    }
                                }
                            }

                            // Students List for Selected Tab
                            if (filteredStudents.isEmpty()) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(200.dp)
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = when (state.selectedTab) {
                                                AttendanceDetailsState.AttendanceTab.PRESENT -> "No students marked as present"
                                                AttendanceDetailsState.AttendanceTab.ABSENT -> "No students marked as absent"
                                            },
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            } else {
                                items(
                                    items = filteredStudents,
                                    key = { it.id }
                                ) { attendanceStudent ->
                                    val student = attendanceStudent.student
                                    if (student != null) {
                                        Column {
                                            if (filteredStudents.first() == attendanceStudent) {
                                                Spacer(modifier = Modifier.height(4.dp))
                                            }
                                            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                                                StudentListItem(
                                                    studentName = "${student.firstName} ${student.lastName}",
                                                    prn = student.prn,
                                                    studentImageUrl = student.studentImageUrl,
                                                    onClick = null
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(12.dp))
                                        }
                                    }
                                }
                            }

                            // Bottom padding
                            item {
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    // Confirmation Dialog
    state.dialogState?.let { dialogState ->
        PresencifyAlertDialog(
            isVisible = dialogState.isVisible,
            dialogType = DialogType.CONFIRM_RISKY_ACTION,
            title = "Remove Attendance",
            message = dialogState.message.asString(),
            onConfirm = dialogState.onConfirm,
            onDismiss = dialogState.onDismiss
        )
    }
}

@Composable
private fun ClassDetailsSection(
    classSession: ClassSession,
    date: LocalDate,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = date.toReadableString(),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        val division = classSession.timetable?.division
        val batch = classSession.batch

        val semester = division?.semester
        val branch = semester?.branch

        val divisionBatchText = when {
            batch != null -> batch.batchCode
            division != null -> division.divisionCode
            else -> null
        }

        val semesterText = semester?.let { sem ->
            val semNum = sem.semesterNumber.value
            val academicYear = "${sem.academicStartYear}-${sem.academicEndYear}"
            "Sem: $semNum $academicYear"
        }

        ClassListItem(
            courseName = classSession.course?.name ?: "Unknown Course",
            teacherName = classSession.teacher?.let { "${it.firstName} ${it.lastName}" }
                ?: "Unknown Teacher",
            startTime = classSession.startTime.toReadableString(),
            endTime = classSession.endTime.toReadableString(),
            dayOfWeek = classSession.dayOfWeek.toDisplayLabel(),
            activeFrom = classSession.activeFrom.toReadableString(),
            activeTill = classSession.activeTill.toReadableString(),
            classType = classSession.classType.toDisplayLabel(),
            isExtraClass = classSession.isExtraClass,
            roomNumber = classSession.room?.roomNumber,
            divisionOrBatchText = divisionBatchText,
            branchAbbreviation = branch?.abbreviation,
            semesterText = semesterText,
            onClick = null,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun AttendanceStatsSection(
    totalStudents: Int,
    presentStudents: Int,
    absentStudents: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Total Students Card
        AttendanceStatCard(
            label = "Total",
            count = totalStudents,
            color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.weight(1f)
        )
        // Present Students Card
        AttendanceStatCard(
            label = "Present",
            count = presentStudents,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f)
        )
        // Absent Students Card
        AttendanceStatCard(
            label = "Absent",
            count = absentStudents,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun AttendanceStatCard(
    label: String,
    count: Int,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(
                color = color.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.headlineMedium,
            color = color,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ActionButtonsSection(
    onAction: (AttendanceDetailsAction) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Edit Attendance Button
        PresencifyTextButton(
            onClick = { onAction(AttendanceDetailsAction.EditAttendanceClick) }
        ) {
            Text(
                text = "Edit attendance",
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Remove Button
        PresencifyTextButton(
            onClick = { onAction(AttendanceDetailsAction.RemoveAttendanceClick) }
        ) {
            Text(
                text = "Remove",
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

private fun buildAttendanceShareText(
    state: AttendanceDetailsState,
): String {
    val attendance = state.attendance ?: return ""
    val classSession = state.classSession

    val builder = StringBuilder()

    // Header
    builder.appendLine("Attendance Details")
    builder.appendLine()

    // Date
    builder.appendLine("Date: ${attendance.date.toReadableString()}")

    // Class / course details
    classSession?.let { session ->
        val division = session.timetable?.division
        val batch = session.batch
        val semester = division?.semester
        val branch = semester?.branch

        val divisionBatchText = when {
            batch != null -> batch.batchCode
            division != null -> division.divisionCode
            else -> null
        }

        val semesterText = semester?.let { sem ->
            val semNum = sem.semesterNumber.value
            val academicYear = "${sem.academicStartYear}-${sem.academicEndYear}"
            "Sem $semNum $academicYear"
        }

        builder.appendLine("Course: ${session.course?.name ?: "Unknown Course"}")
        builder.appendLine(
            "Teacher: " + (session.teacher?.let { "${it.firstName} ${it.lastName}" }
                ?: "Unknown Teacher")
        )
        divisionBatchText?.let { builder.appendLine("Division/Batch: $it") }
        branch?.abbreviation?.let { builder.appendLine("Branch: $it") }
        semesterText?.let { builder.appendLine("Semester: $it") }
        builder.appendLine(
            "Time: ${session.startTime.toReadableString()} - ${session.endTime.toReadableString()}"
        )
    }

    builder.appendLine()

    // Stats
    builder.appendLine("Total Students: ${state.totalStudents}")
    builder.appendLine("Present Students: ${state.presentStudents}")
    builder.appendLine("Absent Students: ${state.absentStudents}")

    builder.appendLine()

    val attendanceStudents = attendance.attendanceStudents

    // Present students list
    val presentStudents = attendanceStudents!!.filter { it.attendanceStatus }
    builder.appendLine("Present Students (${presentStudents.size}):")
    if (presentStudents.isEmpty()) {
        builder.appendLine("- None")
    } else {
        presentStudents.forEachIndexed { index, attendanceStudent ->
            val student = attendanceStudent.student
            val name = student?.let { "${it.firstName} ${it.lastName}" } ?: "Unknown Student"
            val prn = student?.prn?.let { " (PRN: $it)" } ?: ""
            builder.appendLine("${index + 1}. $name$prn")
        }
    }

    builder.appendLine()

    // Absent students list
    val absentStudents = attendanceStudents.filter { !it.attendanceStatus }
    builder.appendLine("Absent Students (${absentStudents.size}):")
    if (absentStudents.isEmpty()) {
        builder.appendLine("- None")
    } else {
        absentStudents.forEachIndexed { index, attendanceStudent ->
            val student = attendanceStudent.student
            val name = student?.let { "${it.firstName} ${it.lastName}" } ?: "Unknown Student"
            val prn = student?.prn?.let { " (PRN: $it)" } ?: ""
            builder.appendLine("${index + 1}. $name$prn")
        }
    }

    return builder.toString().trimEnd()
}
