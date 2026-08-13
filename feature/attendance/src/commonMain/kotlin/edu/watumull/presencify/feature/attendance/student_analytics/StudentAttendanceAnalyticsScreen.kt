package edu.watumull.presencify.feature.attendance.student_analytics

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.multiplatform.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.multiplatform.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.multiplatform.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.multiplatform.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.multiplatform.cartesian.data.lineSeries
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.multiplatform.cartesian.rememberCartesianChart
import edu.watumull.presencify.core.designsystem.components.DonutGraph
import edu.watumull.presencify.core.designsystem.components.PresencifyActionBar
import edu.watumull.presencify.core.designsystem.components.PresencifyDefaultLoadingScreen
import edu.watumull.presencify.core.designsystem.components.PresencifyNoResultsIndicator
import edu.watumull.presencify.core.designsystem.components.PresencifyScaffold
import edu.watumull.presencify.core.designsystem.components.dialog.PresencifyAlertDialog
import edu.watumull.presencify.core.designsystem.theme.DesignToken
import edu.watumull.presencify.core.domain.model.attendance.AggregatedAttendance
import edu.watumull.presencify.core.domain.model.auth.UserRole
import edu.watumull.presencify.core.domain.model.student.StudentSemester
import edu.watumull.presencify.core.presentation.UiConstants
import edu.watumull.presencify.core.presentation.composition_locals.LocalUserRole
import edu.watumull.presencify.core.presentation.isDesktopPlatform
import edu.watumull.presencify.core.presentation.utils.toReadableString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentAttendanceAnalyticsScreen(
    state: StudentAttendanceAnalyticsState,
    onAction: (StudentAttendanceAnalyticsAction) -> Unit,
) {
    val userRole = LocalUserRole.current
    PresencifyScaffold(
        backPress = { onAction(StudentAttendanceAnalyticsAction.NavigateBack) },
        topBarTitle = if (userRole == UserRole.STUDENT) null else "Student Attendance Analytics",
    ) { paddingValues ->
        when (state.viewState) {
            is StudentAttendanceAnalyticsState.ViewState.Loading -> {
                PresencifyDefaultLoadingScreen()
            }

            is StudentAttendanceAnalyticsState.ViewState.Error -> {
                PresencifyNoResultsIndicator(
                    text = state.viewState.message.asString()
                )
            }

            is StudentAttendanceAnalyticsState.ViewState.Content -> {
                StudentAttendanceAnalyticsScreenContent(
                    state = state,
                    onAction = onAction,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }

    state.dialogState?.let { dialogState ->
        PresencifyAlertDialog(
            title = dialogState.title?.asString(),
            message = dialogState.message.asString(),
            dialogType = dialogState.dialogType,
            onDismiss = {
                onAction(StudentAttendanceAnalyticsAction.DismissDialog)
            }
        )
    }
}

@Composable
private fun StudentAttendanceAnalyticsScreenContent(
    state: StudentAttendanceAnalyticsState,
    onAction: (StudentAttendanceAnalyticsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val student = state.student ?: return

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(DesignToken.spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = UiConstants.MAX_CONTENT_WIDTH)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            StudentInfoHeader(
                studentName = "${student.firstName} ${student.middleName?.let { "$it " } ?: ""}${student.lastName}",
                prn = student.prn,
                branch = student.branch?.abbreviation ?: "N/A",
                year = student.studentSemesters?.firstOrNull()?.semester?.semesterNumber?.toAcademicYear()
            )

            Spacer(modifier = Modifier.height(DesignToken.spacing.xl))

            if (student.studentSemesters.isNullOrEmpty()) {
                PresencifyNoResultsIndicator(
                    text = "No attendance data available yet"
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.md),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(
                        items = student.studentSemesters ?: emptyList(),
                        key = { it.semester?.id ?: it.id }
                    ) { studentSemester ->
                        SemesterAttendanceItem(
                            student = student,
                            studentSemester = studentSemester,
                            isExpanded = state.expandedSemesterIds.contains(studentSemester.semester?.id),
                            isLoading = state.loadingSemesterIds.contains(studentSemester.semester?.id),
                            attendanceData = studentSemester.semester?.id?.let {
                                state.semesterAttendanceData[it]
                            } ?: emptyList(),
                            detailedAttendance = studentSemester.semester?.id?.let {
                                state.semesterDetailedAttendance[it]
                            } ?: emptyMap(),
                            onToggleExpansion = {
                                studentSemester.semester?.id?.let { semesterId ->
                                    onAction(StudentAttendanceAnalyticsAction.ToggleSemesterExpansion(semesterId))
                                }
                            },
                            onAction = onAction
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StudentInfoHeader(
    studentName: String,
    prn: String,
    branch: String,
    year: String?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.medium
            )
            .padding(DesignToken.spacing.lg),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = studentName,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Spacer(modifier = Modifier.height(DesignToken.spacing.xs))
        Text(
            text = "PRN: $prn",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = "Branch: $branch ${year?.let { "• $it" } ?: ""}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
private fun SemesterAttendanceItem(
    student: edu.watumull.presencify.core.domain.model.student.Student,
    studentSemester: StudentSemester,
    isExpanded: Boolean,
    isLoading: Boolean,
    attendanceData: List<AggregatedAttendance>,
    detailedAttendance: Map<String, List<edu.watumull.presencify.core.domain.model.attendance.DetailedAttendanceRecord>>,
    onToggleExpansion: () -> Unit,
    onAction: (StudentAttendanceAnalyticsAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val semester = studentSemester.semester ?: return

    val studentDivisions = student.studentDivisions
        ?.filter { it.division?.semesterId == semester.id }
        ?.sortedBy { it.startDate }
        ?: emptyList()

    val studentBatches = student.studentBatches

    edu.watumull.presencify.core.designsystem.components.PresencifyCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = DesignToken.spacing.lg, vertical = DesignToken.spacing.md),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = semester.semesterNumber.toDisplayLabel(),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${semester.academicStartYear}-${semester.academicEndYear.toString().takeLast(2)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = .8f)
                        )
                    }
                }
                IconButton(onClick = onToggleExpansion) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            SemesterDetailsSection(
                semester = semester,
                studentDivisions = studentDivisions,
                studentBatches = studentBatches
            )

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        .padding(DesignToken.spacing.lg)
                ) {
                    when {
                        isLoading -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(DesignToken.spacing.xl),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(DesignToken.components.progressMd),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        attendanceData.isEmpty() -> {
                            Text(
                                text = "No attendance data available for this semester",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(DesignToken.spacing.lg)
                            )
                        }

                        else -> {
                            AttendanceCoursesGrid(
                                attendanceData = attendanceData,
                                detailedAttendance = detailedAttendance,
                                semester = semester,
                                onAction = onAction
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SemesterDetailsSection(
    semester: edu.watumull.presencify.core.domain.model.academics.Semester,
    studentDivisions: List<edu.watumull.presencify.core.domain.model.student.StudentDivision>,
    studentBatches: List<edu.watumull.presencify.core.domain.model.student.StudentBatch>?,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .padding(start = DesignToken.spacing.lg)
            .height(androidx.compose.foundation.layout.IntrinsicSize.Min)
    ) {
        Box(
            modifier = Modifier
                .width(DesignToken.strokes.thick)
                .fillMaxHeight()
                .padding(top = DesignToken.spacing.xs, bottom = DesignToken.spacing.lg)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = MaterialTheme.shapes.medium
                )
        )

        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = DesignToken.spacing.sm)
            ) {
                Text(
                    text = "Start Date",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = ":",
                    modifier = Modifier.padding(horizontal = DesignToken.spacing.xs),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = semester.startDate.toReadableString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            studentDivisions.forEach { studentDivision ->
                DivisionDetailsItem(
                    studentDivision = studentDivision,
                    studentBatches = studentBatches
                )
            }

            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = DesignToken.spacing.sm)
                    .padding(top = DesignToken.spacing.xs, bottom = DesignToken.spacing.lg)
            ) {
                Text(
                    text = "End Date",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = ":",
                    modifier = Modifier.padding(horizontal = DesignToken.spacing.xs),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = semester.endDate.toReadableString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun DivisionDetailsItem(
    studentDivision: edu.watumull.presencify.core.domain.model.student.StudentDivision,
    studentBatches: List<edu.watumull.presencify.core.domain.model.student.StudentBatch>?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = DesignToken.spacing.xs, start = DesignToken.spacing.xxl, end = DesignToken.spacing.lg),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Division ${studentDivision.division?.divisionCode ?: ""} [Roll no - ${studentDivision.rollNo}]",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
        )
    }

    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(start = DesignToken.spacing.xxl)
            .height(androidx.compose.foundation.layout.IntrinsicSize.Min)
    ) {
        Box(
            modifier = Modifier
                .width(DesignToken.strokes.thick)
                .fillMaxHeight()
                .padding(vertical = DesignToken.spacing.xs)
                .background(
                    color = MaterialTheme.colorScheme.secondary,
                    shape = MaterialTheme.shapes.medium
                )
        )

        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = DesignToken.spacing.sm)
            ) {
                Text(
                    text = "From",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = ":",
                    modifier = Modifier.padding(horizontal = DesignToken.spacing.xs),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = studentDivision.startDate.toReadableString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            val pastBatches = studentBatches
                ?.filter {
                    it.batch?.divisionId == studentDivision.division?.id &&
                            it.startDate >= studentDivision.startDate &&
                            (
                                    (it.endDate == null && studentDivision.endDate == null) ||
                                            (studentDivision.endDate?.let { divEnd ->
                                                it.endDate?.let { batchEnd ->
                                                    batchEnd <= divEnd
                                                }
                                            } == true)
                                    )
                }
                ?.sortedBy { it.startDate }

            pastBatches?.forEach { studentBatch ->
                BatchDetailsItem(studentBatch = studentBatch)
            }

            studentDivision.endDate?.let { endDate ->
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = DesignToken.spacing.sm)
                        .padding(top = DesignToken.spacing.xs)
                ) {
                    Text(
                        text = "Till",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = ":",
                        modifier = Modifier.padding(horizontal = DesignToken.spacing.xs),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = endDate.toReadableString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun BatchDetailsItem(studentBatch: edu.watumull.presencify.core.domain.model.student.StudentBatch) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = DesignToken.spacing.xs, start = DesignToken.spacing.xxl, end = DesignToken.spacing.lg),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Batch ${studentBatch.batch?.batchCode ?: ""}",
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurface
        )
    }

    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(start = DesignToken.spacing.xxl)
            .height(androidx.compose.foundation.layout.IntrinsicSize.Min)
    ) {
        Box(
            modifier = Modifier
                .width(DesignToken.strokes.thick)
                .fillMaxHeight()
                .padding(vertical = DesignToken.spacing.xs)
                .background(
                    color = MaterialTheme.colorScheme.tertiary,
                    shape = MaterialTheme.shapes.medium
                )
        )

        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = DesignToken.spacing.sm)
            ) {
                Text(
                    text = "From",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = ":",
                    modifier = Modifier.padding(horizontal = DesignToken.spacing.xs),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = studentBatch.startDate.toReadableString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            studentBatch.endDate?.let { endDate ->
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = DesignToken.spacing.sm)
                        .padding(top = DesignToken.spacing.xs)
                ) {
                    Text(
                        text = "Till",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = ":",
                        modifier = Modifier.padding(horizontal = DesignToken.spacing.xs),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = endDate.toReadableString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

private const val OVERALL_CHIP_ID = "__overall__"

@Composable
private fun AttendanceCoursesGrid(
    attendanceData: List<AggregatedAttendance>,
    detailedAttendance: Map<String, List<edu.watumull.presencify.core.domain.model.attendance.DetailedAttendanceRecord>>,
    semester: edu.watumull.presencify.core.domain.model.academics.Semester,
    onAction: (StudentAttendanceAnalyticsAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedCourseIds = remember {
        mutableStateOf(setOf(OVERALL_CHIP_ID))
    }

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val itemsPerRow = if (maxWidth >= 600.dp) 3 else 2

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.xl)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.md)
            ) {
                Text(
                    text = "Weekly Attendance",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(DesignToken.spacing.sm),
                    verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.sm)
                ) {
                    val isOverallSelected = selectedCourseIds.value.contains(OVERALL_CHIP_ID)
                    val overallColor = getOverallChartColor()

                    FilterChip(
                        selected = isOverallSelected,
                        onClick = {
                            selectedCourseIds.value = if (isOverallSelected) {
                                selectedCourseIds.value - OVERALL_CHIP_ID
                            } else {
                                selectedCourseIds.value + OVERALL_CHIP_ID
                            }
                        },
                        label = { Text("Overall") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        leadingIcon = {
                            Box(
                                modifier = Modifier
                                    .size(DesignToken.icons.xs)
                                    .background(
                                        color = overallColor,
                                        shape = androidx.compose.foundation.shape.CircleShape
                                    )
                            )
                        }
                    )

                    attendanceData.forEachIndexed { index, course ->
                        val isSelected = selectedCourseIds.value.contains(course.courseId)
                        val courseColor = getChartColorForIndex(index)

                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedCourseIds.value = if (isSelected) {
                                    selectedCourseIds.value - course.courseId
                                } else {
                                    selectedCourseIds.value + course.courseId
                                }
                            },
                            label = { Text(course.courseName) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            leadingIcon = {
                                Box(
                                    modifier = Modifier
                                        .size(DesignToken.icons.xs)
                                        .background(
                                            color = courseColor,
                                            shape = androidx.compose.foundation.shape.CircleShape
                                        )
                                )
                            }
                        )
                    }
                }

                val filteredAttendanceData = attendanceData.filter {
                    selectedCourseIds.value.contains(it.courseId)
                }
                val showOverall = selectedCourseIds.value.contains(OVERALL_CHIP_ID)

                if (filteredAttendanceData.isNotEmpty() || showOverall) {
                    WeeklyAttendanceTrendChart(
                        attendanceData = filteredAttendanceData,
                        detailedAttendance = detailedAttendance,
                        semester = semester,
                        originalAttendanceData = attendanceData,
                        showOverall = showOverall,
                        allAttendanceData = attendanceData,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Text(
                        text = "Select at least one course to view attendance graph",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = DesignToken.spacing.xxl, horizontal = DesignToken.spacing.lg)
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.md)
            ) {
                Text(
                    text = "Average Attendance per Course",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                val rows = attendanceData.chunked(itemsPerRow)

                rows.forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(DesignToken.spacing.lg),
                        verticalAlignment = Alignment.Top
                    ) {
                        rowItems.forEach { attendance ->
                            DonutGraph(
                                modifier = Modifier.weight(1f),
                                value = attendance.attendedLectures,
                                total = attendance.totalLectures,
                                label = attendance.courseName,
                                strokeWidth = DesignToken.strokes.extraThick,
                                animate = true,
                                onClick = {
                                    onAction(
                                        StudentAttendanceAnalyticsAction.DonutCourseClick(
                                            courseId = attendance.courseId
                                        )
                                    )
                                }
                            )
                        }

                        if (rowItems.size < itemsPerRow) {
                            repeat(itemsPerRow - rowItems.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }

                    if (rowItems != rows.last()) {
                        Spacer(modifier = Modifier.height(DesignToken.spacing.lg))
                    }
                }
            }
        }
    }
}

@Composable
private fun WeeklyAttendanceTrendChart(
    attendanceData: List<AggregatedAttendance>,
    detailedAttendance: Map<String, List<edu.watumull.presencify.core.domain.model.attendance.DetailedAttendanceRecord>>,
    semester: edu.watumull.presencify.core.domain.model.academics.Semester,
    originalAttendanceData: List<AggregatedAttendance>,
    showOverall: Boolean = false,
    allAttendanceData: List<AggregatedAttendance> = emptyList(),
    modifier: Modifier = Modifier
) {
    if (attendanceData.isEmpty() && !showOverall) return
    if (detailedAttendance.isEmpty()) return

    val scrollState = rememberScrollState()

    val weekSundays = getWeekSundays(semester.startDate, semester.endDate)
    val numberOfWeeks = weekSundays.size
    if (numberOfWeeks == 0) return

    val firstMonday = getMondayOfWeek(semester.startDate)

    val courseWeeklyPercentages = attendanceData.map { course ->
        val records = detailedAttendance[course.courseId] ?: emptyList()
        calculateWeeklyPercentages(records, firstMonday, numberOfWeeks)
    }

    val overallWeeklyPercentages = if (showOverall) {
        val allCoursePercentages = allAttendanceData.map { course ->
            val records = detailedAttendance[course.courseId] ?: emptyList()
            calculateWeeklyPercentages(records, firstMonday, numberOfWeeks)
        }
        if (allCoursePercentages.isNotEmpty()) {
            (0 until numberOfWeeks).map { weekIdx ->
                val values = allCoursePercentages.map { it[weekIdx] }
                val nonZeroValues = values.filter { it > 0f }
                if (nonZeroValues.isNotEmpty()) nonZeroValues.average().toFloat() else 0f
            }
        } else {
            null
        }
    } else {
        null
    }

    val allWeeklyPercentages = buildList {
        overallWeeklyPercentages?.let { add(it) }
        addAll(courseWeeklyPercentages)
    }

    if (allWeeklyPercentages.isEmpty()) return

    @Suppress("DEPRECATION")
    val weekLabels = weekSundays.map { sunday ->
        val monthName = sunday.month.name.take(3).lowercase()
            .replaceFirstChar { it.uppercase() }
        "${sunday.dayOfMonth} $monthName"
    }

    val courseKey = (if (showOverall) "overall," else "") +
            attendanceData.joinToString(",") { it.courseId }

    androidx.compose.runtime.key(courseKey) {
        val modelProducer = androidx.compose.runtime.remember { CartesianChartModelProducer() }

        androidx.compose.runtime.LaunchedEffect(Unit) {
            val xValues = (0 until numberOfWeeks).map { it.toDouble() }
            modelProducer.runTransaction {
                lineSeries {
                    series(listOf(0.0, 0.0), listOf(0.0, 100.0))

                    allWeeklyPercentages.forEach { yData ->
                        series(xValues, yData.map { it.toDouble() })
                    }
                }
            }
        }

        val overallColor = getOverallChartColor()
        val lineSpecs = buildList {
            add(
                LineCartesianLayer.Line(
                    fill = LineCartesianLayer.LineFill.single(
                        com.patrykandpatrick.vico.multiplatform.common.fill(
                            androidx.compose.ui.graphics.Color.Transparent
                        )
                    ),
                    pointConnector = LineCartesianLayer.PointConnector.cubic()
                )
            )

            if (showOverall && overallWeeklyPercentages != null) {
                add(
                    LineCartesianLayer.Line(
                        fill = LineCartesianLayer.LineFill.single(
                            com.patrykandpatrick.vico.multiplatform.common.fill(overallColor)
                        ),
                        pointConnector = LineCartesianLayer.PointConnector.cubic()
                    )
                )
            }
            attendanceData.forEach { course ->
                val originalIndex = originalAttendanceData.indexOfFirst { it.courseId == course.courseId }
                add(
                    LineCartesianLayer.Line(
                        fill = LineCartesianLayer.LineFill.single(
                            com.patrykandpatrick.vico.multiplatform.common.fill(
                                getChartColorForIndex(originalIndex)
                            )
                        ),
                        pointConnector = LineCartesianLayer.PointConnector.cubic()
                    )
                )
            }
        }

        Column(modifier = modifier.fillMaxWidth()) {
            val chartWidth = maxOf(500.dp, (numberOfWeeks * 72).dp)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState)
            ) {
                Box(
                    modifier = Modifier
                        .width(chartWidth)
                        .height(240.dp)
                        .pointerInput(Unit) {
                            detectTransformGestures { _, _, _, _ -> }
                        }
                ) {
                    CartesianChartHost(
                        chart = rememberCartesianChart(
                            LineCartesianLayer(
                                lineProvider = LineCartesianLayer.LineProvider.series(lineSpecs)
                            ),
                            startAxis = VerticalAxis.rememberStart(
                                valueFormatter = { _, value, _ ->
                                    "${value.toInt()}%"
                                }
                            ),
                            bottomAxis = HorizontalAxis.rememberBottom(
                                valueFormatter = { _, value, _ ->
                                    val idx = value.toInt().coerceIn(0, weekLabels.size - 1)
                                    weekLabels[idx]
                                }
                            )
                        ),
                        modelProducer = modelProducer,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}


private fun getMondayOfWeek(date: kotlinx.datetime.LocalDate): kotlinx.datetime.LocalDate {
    val dayOfWeek = date.dayOfWeek
    val daysFromMonday = dayOfWeek.ordinal
    return kotlinx.datetime.LocalDate.fromEpochDays(date.toEpochDays() - daysFromMonday)
}


private fun getWeekSundays(
    semesterStart: kotlinx.datetime.LocalDate,
    semesterEnd: kotlinx.datetime.LocalDate
): List<kotlinx.datetime.LocalDate> {
    val firstMonday = getMondayOfWeek(semesterStart)
    var sunday = kotlinx.datetime.LocalDate.fromEpochDays(firstMonday.toEpochDays() + 6)
    val sundays = mutableListOf<kotlinx.datetime.LocalDate>()
    while (sunday.toEpochDays() <= semesterEnd.toEpochDays() + 6) {
        sundays.add(sunday)
        sunday = kotlinx.datetime.LocalDate.fromEpochDays(sunday.toEpochDays() + 7)
        if (sundays.last().toEpochDays() > semesterEnd.toEpochDays() + 6) break
    }
    return sundays
}


private fun calculateWeeklyPercentages(
    records: List<edu.watumull.presencify.core.domain.model.attendance.DetailedAttendanceRecord>,
    firstMonday: kotlinx.datetime.LocalDate,
    numberOfWeeks: Int
): List<Float> {
    if (records.isEmpty()) return List(numberOfWeeks) { 0f }

    val firstMondayEpoch = firstMonday.toEpochDays()

    val recordsByWeek = records.groupBy { record ->
        val daysSinceFirstMonday = record.date.toEpochDays() - firstMondayEpoch
        (daysSinceFirstMonday / 7).toInt().coerceIn(0, numberOfWeeks - 1)
    }

    return (0 until numberOfWeeks).map { weekIndex ->
        val weekRecords = recordsByWeek[weekIndex]
        if (weekRecords.isNullOrEmpty()) {
            0f
        } else {
            val total = weekRecords.size
            val attended = weekRecords.count { it.attendanceStatus }
            (attended.toFloat() / total.toFloat()) * 100f
        }
    }
}

@Composable
private fun getChartColorForIndex(index: Int): androidx.compose.ui.graphics.Color {
    val colors = listOf(
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.tertiary,
        MaterialTheme.colorScheme.error,
        androidx.compose.ui.graphics.Color(0xFFFF9800),
        androidx.compose.ui.graphics.Color(0xFF9C27B0),
        androidx.compose.ui.graphics.Color(0xFF4CAF50),
        androidx.compose.ui.graphics.Color(0xFFE91E63),
        androidx.compose.ui.graphics.Color(0xFFFF5722),
        androidx.compose.ui.graphics.Color(0xFF3F51B5),
        androidx.compose.ui.graphics.Color(0xFF8BC34A),
        androidx.compose.ui.graphics.Color(0xFFFFEB3B),
        androidx.compose.ui.graphics.Color(0xFF795548),
    )
    return colors[index % colors.size]
}

@Composable
private fun getOverallChartColor(): androidx.compose.ui.graphics.Color {
    return MaterialTheme.colorScheme.onSurface
}

