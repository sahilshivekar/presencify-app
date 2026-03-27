package edu.watumull.presencify.feature.attendance.mark_attendance

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import edu.watumull.presencify.core.design.systems.components.PresencifyActionBar
import edu.watumull.presencify.core.design.systems.components.PresencifyScaffold
import edu.watumull.presencify.core.domain.model.schedule.ClassSession
import edu.watumull.presencify.core.presentation.UiConstants
import edu.watumull.presencify.core.presentation.components.ClassListItem
import edu.watumull.presencify.core.presentation.components.StudentListItem
import edu.watumull.presencify.core.presentation.utils.toReadableString
import kotlinx.datetime.LocalDate

@Composable
fun MarkAttendanceScreen(
    state: MarkAttendanceState,
    onAction: (MarkAttendanceAction) -> Unit
) {
    PresencifyScaffold(
        backPress = { onAction(MarkAttendanceAction.BackButtonClick) },
        topBarTitle = "Mark Attendance",
        actions = {
            if (state.viewState == MarkAttendanceState.ViewState.Content && state.attendance != null) {
                IconButton(onClick = { onAction(MarkAttendanceAction.ShareAttendanceSummary) }) {
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
                MarkAttendanceState.ViewState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is MarkAttendanceState.ViewState.Error -> {
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
                            Button(onClick = { onAction(MarkAttendanceAction.BackButtonClick) }) {
                                Text("Go Back")
                            }
                        }
                    }
                }

                MarkAttendanceState.ViewState.Content -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        item {
                            Column(
                                modifier = Modifier
                                    .widthIn(max = UiConstants.MAX_CONTENT_WIDTH)
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(24.dp)
                            ) {
                                // Class Details Section
                                state.classSession?.let { classSession ->
                                    ClassDetailsSection(classSession = classSession, date = state.attendance?.date!!)
                                }
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {

                                    PresencifyActionBar(
                                        text = "Mark attendance with Dynamic QR",
                                        onClick = { onAction(MarkAttendanceAction.DynamicQRClick) },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    PresencifyActionBar(
                                        text = "Group photo scan",
                                        onClick = { onAction(MarkAttendanceAction.GroupPhotoScanClick) },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                // Stats Cards Section
                                AttendanceStatsSection(
                                    totalStudents = state.totalStudents,
                                    presentStudents = state.presentStudents,
                                    absentStudents = state.absentStudents
                                )
                            }
                        }

                        // Students List Header
                        item {
                            Column(
                                modifier = Modifier
                                    .widthIn(max = UiConstants.MAX_CONTENT_WIDTH)
                                    .fillMaxWidth()
                            ) {
                                Text(
                                    text = "Students List",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }

                        // Students List
                        val attendanceStudents = state.attendance?.attendanceStudents ?: emptyList()
                        items(
                            items = attendanceStudents,
                            key = { it.id }
                        ) { attendanceStudent ->
                            val student = attendanceStudent.student
                            if (student != null) {
                                val isLoading = state.studentLoadingStates[student.id] == true
                                val feedback = state.studentFeedbacks[student.id]

                                Column(
                                    modifier = Modifier
                                        .widthIn(max = UiConstants.MAX_CONTENT_WIDTH)
                                        .fillMaxWidth()
                                ) {
                                    StudentListItem(
                                        studentName = "${student.firstName} ${student.lastName}",
                                        studentImageUrl = student.studentImageUrl,
                                        prn = student.prn,
                                        feedback = feedback,
                                        trailingContent = {
                                            Column(
                                                verticalArrangement = Arrangement.Top,
                                                horizontalAlignment = Alignment.End,
                                            ) {
                                                Text(
                                                    text = if (attendanceStudent.attendanceStatus) "Present" else "Absent",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = if (attendanceStudent.attendanceStatus)
                                                        MaterialTheme.colorScheme.primary
                                                    else
                                                        MaterialTheme.colorScheme.error
                                                )
                                                Switch(
                                                    checked = attendanceStudent.attendanceStatus,
                                                    onCheckedChange = {
                                                        onAction(
                                                            MarkAttendanceAction.ToggleStudentAttendance(
                                                                studentId = student.id,
                                                                currentStatus = attendanceStudent.attendanceStatus
                                                            )
                                                        )
                                                    },
                                                    enabled = !isLoading,
                                                    thumbContent = if (isLoading) {
                                                        {
                                                            CircularProgressIndicator(
                                                                modifier = Modifier.size(SwitchDefaults.IconSize),
                                                                strokeWidth = 2.dp
                                                            )
                                                        }
                                                    } else null
                                                )
                                            }
                                        },
                                        onClick = null
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
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
            onClick = { /* No action for this view */ },
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
