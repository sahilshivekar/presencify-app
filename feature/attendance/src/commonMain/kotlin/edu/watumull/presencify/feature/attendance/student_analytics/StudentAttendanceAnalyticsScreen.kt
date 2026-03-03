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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import edu.watumull.presencify.core.design.systems.components.PresencifyDefaultLoadingScreen
import edu.watumull.presencify.core.design.systems.components.PresencifyNoResultsIndicator
import edu.watumull.presencify.core.design.systems.components.PresencifyScaffold
import edu.watumull.presencify.core.design.systems.components.dialog.PresencifyAlertDialog
import edu.watumull.presencify.core.domain.model.attendance.AggregatedAttendance
import edu.watumull.presencify.core.domain.model.student.StudentSemester
import edu.watumull.presencify.core.presentation.UiConstants
import edu.watumull.presencify.core.presentation.utils.toReadableString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentAttendanceAnalyticsScreen(
    state: StudentAttendanceAnalyticsState,
    onAction: (StudentAttendanceAnalyticsAction) -> Unit,
) {
    PresencifyScaffold(
        backPress = { onAction(StudentAttendanceAnalyticsAction.BackButtonClick) },
        topBarTitle = "Student Attendance Analytics",
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
            isVisible = dialogState.isVisible,
            dialogType = dialogState.dialogType,
            title = dialogState.title,
            message = dialogState.message?.asString() ?: "",
            onConfirm = {
                when (dialogState.dialogIntention) {
                    DialogIntention.GENERIC -> {
                        // Handle generic confirmation
                    }
                }
            },
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
            .padding(16.dp),
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
            // Student Info Header
            StudentInfoHeader(
                studentName = "${student.firstName} ${student.middleName?.let { "$it " } ?: ""}${student.lastName}",
                prn = student.prn,
                branch = student.branch?.abbreviation ?: "N/A",
                year = student.studentSemesters?.firstOrNull()?.semester?.semesterNumber?.toAcademicYear()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Semesters List
            if (student.studentSemesters.isNullOrEmpty()) {
                PresencifyNoResultsIndicator(
                    text = "No semesters found for this student"
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
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
                            }
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
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = studentName,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Spacer(modifier = Modifier.height(4.dp))
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
    modifier: Modifier = Modifier
) {
    val semester = studentSemester.semester ?: return

    // Get divisions from StudentDivision where division.semesterId matches current semester
    val studentDivisions = student.studentDivisions
        ?.filter { it.division?.semesterId == semester.id }
        ?.sortedBy { it.startDate }
        ?: emptyList()

    // Get batches from StudentBatch
    val studentBatches = student.studentBatches

    edu.watumull.presencify.core.design.systems.components.PresencifyCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Semester Header with Expand Icon
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
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

            // Semester Details (similar to StudentDetailsScreen)
            SemesterDetailsSection(
                semester = semester,
                studentDivisions = studentDivisions,
                studentBatches = studentBatches
            )

            // Expandable Attendance Section
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        .padding(16.dp)
                ) {
                    when {
                        isLoading -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(40.dp),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        attendanceData.isEmpty() -> {
                            Text(
                                text = "No attendance data available for this semester",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                        else -> {
                            AttendanceCoursesGrid(
                                attendanceData = attendanceData,
                                detailedAttendance = detailedAttendance,
                                semester = semester
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
            .padding(start = 16.dp)
            .height(androidx.compose.foundation.layout.IntrinsicSize.Min)
    ) {
        // Semester line
        Box(
            modifier = Modifier
                .width(5.dp)
                .fillMaxHeight()
                .padding(top = 4.dp, bottom = 20.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = MaterialTheme.shapes.medium
                )
        )

        // Semester details column
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = "Start Date",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = ":",
                    modifier = Modifier.padding(horizontal = 4.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = semester.startDate.toReadableString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Divisions
            studentDivisions.forEach { studentDivision ->
                DivisionDetailsItem(
                    studentDivision = studentDivision,
                    studentBatches = studentBatches
                )
            }

            // Semester end date
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .padding(top = 4.dp, bottom = 16.dp)
            ) {
                Text(
                    text = "End Date",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = ":",
                    modifier = Modifier.padding(horizontal = 4.dp),
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
            .padding(top = 4.dp, start = 32.dp, end = 16.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Division ${studentDivision.division?.divisionCode ?: ""}",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
        )
    }

    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(start = 32.dp)
            .height(androidx.compose.foundation.layout.IntrinsicSize.Min)
    ) {
        // Division line
        Box(
            modifier = Modifier
                .width(5.dp)
                .fillMaxHeight()
                .padding(vertical = 4.dp)
                .background(
                    color = MaterialTheme.colorScheme.secondary,
                    shape = MaterialTheme.shapes.medium
                )
        )

        // Division details column
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = "From",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = ":",
                    modifier = Modifier.padding(horizontal = 4.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = studentDivision.startDate.toReadableString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Batches
            val pastBatches = studentBatches
                ?.filter { it.batch?.divisionId == studentDivision.division?.id }
                ?.sortedBy { it.startDate }

            pastBatches?.forEach { studentBatch ->
                BatchDetailsItem(studentBatch = studentBatch)
            }

            studentDivision.endDate?.let { endDate ->
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .padding(top = 4.dp)
                ) {
                    Text(
                        text = "Till",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = ":",
                        modifier = Modifier.padding(horizontal = 4.dp),
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
            .padding(top = 4.dp, start = 32.dp, end = 16.dp),
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
            .padding(start = 32.dp)
            .height(androidx.compose.foundation.layout.IntrinsicSize.Min)
    ) {
        // Batch line
        Box(
            modifier = Modifier
                .width(5.dp)
                .fillMaxHeight()
                .padding(vertical = 4.dp)
                .background(
                    color = MaterialTheme.colorScheme.tertiary,
                    shape = MaterialTheme.shapes.medium
                )
        )

        // Batch details column
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = "From",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = ":",
                    modifier = Modifier.padding(horizontal = 4.dp),
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
                        .padding(horizontal = 8.dp)
                        .padding(top = 4.dp)
                ) {
                    Text(
                        text = "Till",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = ":",
                        modifier = Modifier.padding(horizontal = 4.dp),
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
private fun AttendanceCoursesGrid(
    attendanceData: List<AggregatedAttendance>,
    detailedAttendance: Map<String, List<edu.watumull.presencify.core.domain.model.attendance.DetailedAttendanceRecord>>,
    semester: edu.watumull.presencify.core.domain.model.academics.Semester,
    modifier: Modifier = Modifier
) {
    // State to track which courses are selected for display in the chart
    val selectedCourseIds = androidx.compose.runtime.remember {
        androidx.compose.runtime.mutableStateOf(attendanceData.map { it.courseId }.toSet())
    }

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        // Determine items per row based on available width
        val itemsPerRow = if (maxWidth >= 600.dp) 3 else 2

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Section 1: Weekly Attendance with Course Filter Chips
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Weekly Attendance",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Course filter chips
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
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
                            leadingIcon = {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .background(
                                            color = courseColor,
                                            shape = androidx.compose.foundation.shape.CircleShape
                                        )
                                )
                            }
                        )
                    }
                }

                // Weekly Attendance Chart - filtered by selected courses
                val filteredAttendanceData = attendanceData.filter {
                    selectedCourseIds.value.contains(it.courseId)
                }

                if (filteredAttendanceData.isNotEmpty()) {
                    WeeklyAttendanceTrendChart(
                        attendanceData = filteredAttendanceData,
                        detailedAttendance = detailedAttendance,
                        semester = semester,
                        originalAttendanceData = attendanceData, // Pass full list for color mapping
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Text(
                        text = "Select at least one course to view trends",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 32.dp, horizontal = 16.dp)
                    )
                }
            }

            // Section 2: Average Attendance per Course
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Average Attendance per Course",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Group items into rows
                val rows = attendanceData.chunked(itemsPerRow)

                rows.forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        rowItems.forEach { attendance ->
                            edu.watumull.presencify.core.design.systems.components.DonutGraph(
                                modifier = Modifier.weight(1f),
                                value = attendance.attendedLectures,
                                total = attendance.totalLectures,
                                label = attendance.courseName,
                                size = 100.dp,
                                strokeWidth = 8.dp,
                                animate = true,
                                showPercentage = true
                            )
                        }

                        // Add empty spacers for incomplete last row to maintain left alignment
                        if (rowItems.size < itemsPerRow) {
                            repeat(itemsPerRow - rowItems.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }

                    // Add spacing between rows except after the last row
                    if (rowItems != rows.last()) {
                        Spacer(modifier = Modifier.height(16.dp))
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
    modifier: Modifier = Modifier
) {
    if (attendanceData.isEmpty() || detailedAttendance.isEmpty()) return

    val scrollState = rememberScrollState()

    // Calculate number of weeks from semester start and end date
    val semesterStartDate = semester.startDate
    val semesterEndDate = semester.endDate
    val totalDays = semesterEndDate.toEpochDays() - semesterStartDate.toEpochDays()
    val numberOfWeeks = maxOf(1, ((totalDays + 6) / 7).toInt())

    // Build weekly percentage data for each course from real detailed records
    val weeklyPercentages = attendanceData.map { course ->
        val records = detailedAttendance[course.courseId] ?: emptyList()
        calculateWeeklyPercentages(records, semesterStartDate, numberOfWeeks)
    }

    // Create a stable key from courseIds to force recomposition when selection changes
    val courseKey = attendanceData.joinToString(",") { it.courseId }

    // Use key() to force a full tear-down and re-creation of the chart when data changes.
    // This ensures the model producer, chart, and axis formatters are all created fresh together.
    androidx.compose.runtime.key(courseKey) {
        val modelProducer = androidx.compose.runtime.remember { CartesianChartModelProducer() }

        // Populate data inside LaunchedEffect (runTransaction is suspend)
        androidx.compose.runtime.LaunchedEffect(Unit) {
            val xValues = (0 until numberOfWeeks).map { it.toDouble() }
            modelProducer.runTransaction {
                lineSeries {
                    weeklyPercentages.forEach { yData ->
                        series(xValues, yData.map { it.toDouble() })
                    }
                }
            }
        }

        // Pre-build line specs using original indices for color mapping
        val lineSpecs = attendanceData.map { course ->
            val originalIndex = originalAttendanceData.indexOfFirst { it.courseId == course.courseId }
            LineCartesianLayer.Line(
                fill = LineCartesianLayer.LineFill.single(
                    com.patrykandpatrick.vico.multiplatform.common.fill(
                        getChartColorForIndex(originalIndex)
                    )
                )
            )
        }

        Column(modifier = modifier.fillMaxWidth()) {
            val chartWidth = maxOf(500.dp, (numberOfWeeks * 48).dp)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState)
            ) {
                Box(
                    modifier = Modifier
                        .width(chartWidth)
                        .height(220.dp)
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
                                "W${value.toInt() + 1}"
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

/**
 * Calculate weekly attendance percentages from detailed records.
 * Groups records by week number (based on semester start date),
 * then for each week: (attended / total classes that week) * 100
 */
private fun calculateWeeklyPercentages(
    records: List<edu.watumull.presencify.core.domain.model.attendance.DetailedAttendanceRecord>,
    semesterStartDate: kotlinx.datetime.LocalDate,
    numberOfWeeks: Int
): List<Float> {
    if (records.isEmpty()) return List(numberOfWeeks) { 0f }

    val startEpochDay = semesterStartDate.toEpochDays()

    // Group records by week index
    val recordsByWeek = records.groupBy { record ->
        val daysSinceStart = record.date.toEpochDays() - startEpochDay
        (daysSinceStart / 7).toInt().coerceIn(0, numberOfWeeks - 1)
    }

    return (0 until numberOfWeeks).map { weekIndex ->
        val weekRecords = recordsByWeek[weekIndex]
        if (weekRecords.isNullOrEmpty()) {
            0f // No classes this week
        } else {
            val total = weekRecords.size
            val attended = weekRecords.count { it.attendanceStatus }
            (attended.toFloat() / total.toFloat()) * 100f
        }
    }
}

// Helper function to get distinct colors for each course line
@Composable
private fun getChartColorForIndex(index: Int): androidx.compose.ui.graphics.Color {
    val colors = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.tertiary,
        MaterialTheme.colorScheme.error,
        androidx.compose.ui.graphics.Color(0xFF00BCD4), // Cyan
        androidx.compose.ui.graphics.Color(0xFFFF9800), // Orange
        androidx.compose.ui.graphics.Color(0xFF9C27B0), // Purple
        androidx.compose.ui.graphics.Color(0xFF4CAF50), // Green
        androidx.compose.ui.graphics.Color(0xFFE91E63), // Pink
        androidx.compose.ui.graphics.Color(0xFF009688), // Teal
        androidx.compose.ui.graphics.Color(0xFFFF5722), // Deep Orange
        androidx.compose.ui.graphics.Color(0xFF3F51B5), // Indigo
        androidx.compose.ui.graphics.Color(0xFF8BC34A), // Light Green
        androidx.compose.ui.graphics.Color(0xFFFFEB3B), // Yellow
        androidx.compose.ui.graphics.Color(0xFF795548), // Brown
    )
    return colors[index % colors.size]
}
