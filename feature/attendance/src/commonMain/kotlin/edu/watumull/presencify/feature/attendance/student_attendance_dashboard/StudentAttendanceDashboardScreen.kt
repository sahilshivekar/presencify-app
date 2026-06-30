package edu.watumull.presencify.feature.attendance.student_attendance_dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.multiplatform.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.multiplatform.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.multiplatform.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.multiplatform.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.multiplatform.cartesian.data.lineSeries
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.multiplatform.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.multiplatform.common.fill as vicoFill
import edu.watumull.presencify.core.designsystem.components.DonutGraph
import edu.watumull.presencify.core.designsystem.components.PresencifyActionBar
import edu.watumull.presencify.core.designsystem.components.PresencifyDefaultLoadingScreen
import edu.watumull.presencify.core.designsystem.components.PresencifyNoResultsIndicator
import edu.watumull.presencify.core.designsystem.components.PresencifyScaffold
import edu.watumull.presencify.core.designsystem.components.dialog.PresencifyAlertDialog
import edu.watumull.presencify.core.designsystem.components.shimmerEffect
import edu.watumull.presencify.core.designsystem.theme.DesignToken
import edu.watumull.presencify.core.domain.model.academics.Semester
import edu.watumull.presencify.core.domain.model.attendance.AggregatedAttendance
import edu.watumull.presencify.core.domain.model.attendance.Attendance
import edu.watumull.presencify.core.domain.model.attendance.DetailedAttendanceRecord
import edu.watumull.presencify.core.presentation.UiConstants
import edu.watumull.presencify.core.presentation.components.AttendanceListItem
import edu.watumull.presencify.core.presentation.isDesktopPlatform
import edu.watumull.presencify.core.presentation.utils.toReadableString
import kotlinx.datetime.LocalDate

private val DashboardChartHeight: Dp = 240.dp
private val DashboardMinChartWidth: Dp = 500.dp
private val DashboardWeekWidth: Dp = 72.dp
private val DashboardDonutWidth: Dp = 180.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentAttendanceDashboardScreen(
    state: StudentAttendanceDashboardState,
    onAction: (StudentAttendanceDashboardAction) -> Unit,
) {
    PresencifyScaffold(
        backPress = { onAction(StudentAttendanceDashboardAction.NavigateBack) },
        topBarTitle = null,
    ) { paddingValues ->
        when (state.viewState) {
            StudentAttendanceDashboardState.ViewState.Loading -> {
                PresencifyDefaultLoadingScreen()
            }

            is StudentAttendanceDashboardState.ViewState.Error -> {
                PresencifyNoResultsIndicator(
                    text = state.viewState.message.asString()
                )
            }

            StudentAttendanceDashboardState.ViewState.Content -> {
                StudentAttendanceDashboardContent(
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
            onDismiss = { onAction(StudentAttendanceDashboardAction.DismissDialog) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StudentAttendanceDashboardContent(
    state: StudentAttendanceDashboardState,
    onAction: (StudentAttendanceDashboardAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val selectedSemester = state.semesters
        .firstOrNull { it.semester?.id == state.selectedSemesterId }
        ?.semester
    val selectedTabIndex = state.semesters.indexOfFirst {
        it.semester?.id == state.selectedSemesterId
    }.coerceAtLeast(0)
    val isLoadingSelectedSemester = state.selectedSemesterId
        ?.let { state.loadingSemesterIds.contains(it) } == true

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        LazyColumn(
            modifier = Modifier
                .widthIn(max = UiConstants.MAX_CONTENT_WIDTH)
                .fillMaxWidth()
                .padding(DesignToken.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (state.semesters.isNotEmpty()) {
                item {
                    SecondaryScrollableTabRow(
                        selectedTabIndex = selectedTabIndex,
                        divider = {},
                        edgePadding = DesignToken.spacing.none,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        state.semesters.forEach { studentSemester ->
                            val semester = studentSemester.semester
                            Tab(
                                selected = semester?.id == state.selectedSemesterId,
                                onClick = {
                                    semester?.id?.let { semesterId ->
                                        onAction(StudentAttendanceDashboardAction.SelectSemester(semesterId))
                                    }
                                },
                                text = {
                                    Text(
                                        text = semester?.semesterNumber?.toDisplayLabel() ?: "Semester",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = if (semester?.id == state.selectedSemesterId) {
                                            FontWeight.Bold
                                        } else {
                                            FontWeight.Normal
                                        },
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            )
                        }
                    }
                }
            }
            if (state.semesters.isEmpty()) {
                item {
                    PresencifyNoResultsIndicator(
                        text = "No semesters available yet"
                    )
                }
            } else if (selectedSemester == null) {
                item {
                    PresencifyNoResultsIndicator(
                        text = "Select a semester to view attendance"
                    )
                }
            } else {
                item {
                    DashboardHeader(
                        studentName = state.student?.let {
                            "${it.firstName} ${it.middleName?.let { middleName -> "$middleName " } ?: ""}${it.lastName}"
                        }.orEmpty(),
                        semester = selectedSemester,
                        onAction = onAction
                    )
                }

                if (isLoadingSelectedSemester) {
                    item {
                        StudentAttendanceDashboardShimmer()
                    }
                } else if (state.selectedAttendanceData.isEmpty()) {
                    item {
                        PresencifyNoResultsIndicator(
                            text = "No attendance data available for this semester"
                        )
                    }
                } else {
                    item {
                        WeeklyAttendanceSection(
                            attendanceData = state.selectedAttendanceData,
                            detailedAttendance = state.selectedDetailedAttendance,
                            selectedCourseIds = state.selectedCourseIds,
                            semester = selectedSemester,
                            onAction = onAction
                        )
                    }

                    item {
                        DonutGraphsSection(
                            attendanceData = state.selectedAttendanceData,
                            onAction = onAction
                        )
                    }

                    item {
                        RecentAttendancesSection(
                            attendances = state.recentAttendances,
                            studentId = state.studentId,
                            isLoading = state.isLoadingRecentAttendances,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StudentAttendanceDashboardShimmer(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.lg)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(DashboardChartHeight)
                .shimmerEffect()
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(DesignToken.spacing.sm)
        ) {
            repeat(4) {
                Box(
                    modifier = Modifier
                        .width(DashboardDonutWidth)
                        .height(DesignToken.icons.xl)
                        .shimmerEffect()
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(DesignToken.spacing.lg)
        ) {
            repeat(3) {
                Box(
                    modifier = Modifier
                        .width(DashboardDonutWidth)
                        .height(DashboardDonutWidth)
                        .shimmerEffect()
                )
            }
        }
    }
}

@Composable
private fun DashboardHeader(
    studentName: String,
    semester: Semester,
    onAction: (StudentAttendanceDashboardAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.md)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.xs)
        ) {
            Text(
                text = studentName.ifBlank { "Attendance Dashboard" },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "${semester.semesterNumber.toDisplayLabel()} (${semester.academicStartYear}-${semester.academicEndYear})",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (!isDesktopPlatform()) {
            PresencifyActionBar(
                text = "Scan QR for attendance",
                onClick = { onAction(StudentAttendanceDashboardAction.ScanQrClick) }
            )
        }
    }
}

@Composable
private fun WeeklyAttendanceSection(
    attendanceData: List<AggregatedAttendance>,
    detailedAttendance: Map<String, List<DetailedAttendanceRecord>>,
    selectedCourseIds: Set<String>,
    semester: Semester,
    onAction: (StudentAttendanceDashboardAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val filteredAttendanceData = attendanceData.filter {
        selectedCourseIds.contains(it.courseId)
    }
    val showOverall = selectedCourseIds.contains(OVERALL_ATTENDANCE_COURSE_ID)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.md)
    ) {
        Text(
            text = "Weekly Attendance",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )

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
                modifier = Modifier.padding(vertical = DesignToken.spacing.xxl)
            )
        }

        CourseSelectionRow(
            attendanceData = attendanceData,
            selectedCourseIds = selectedCourseIds,
            onAction = onAction
        )
    }
}

@Composable
private fun CourseSelectionRow(
    attendanceData: List<AggregatedAttendance>,
    selectedCourseIds: Set<String>,
    onAction: (StudentAttendanceDashboardAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(DesignToken.spacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AttendanceFilterChip(
            text = "Overall",
            selected = selectedCourseIds.contains(OVERALL_ATTENDANCE_COURSE_ID),
            indicatorColor = getOverallChartColor(),
            onClick = {
                onAction(StudentAttendanceDashboardAction.ToggleCourseSelection(OVERALL_ATTENDANCE_COURSE_ID))
            }
        )

        attendanceData.forEachIndexed { index, attendance ->
            AttendanceFilterChip(
                text = attendance.courseName,
                selected = selectedCourseIds.contains(attendance.courseId),
                indicatorColor = getChartColorForIndex(index),
                onClick = {
                    onAction(StudentAttendanceDashboardAction.ToggleCourseSelection(attendance.courseId))
                }
            )
        }
    }
}

@Composable
private fun AttendanceFilterChip(
    text: String,
    selected: Boolean,
    indicatorColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = text,
                color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
            )
        },
        leadingIcon = {
            Box(
                modifier = Modifier
                    .size(DesignToken.icons.xs)
                    .background(
                        color = indicatorColor,
                        shape = androidx.compose.foundation.shape.CircleShape
                    )
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            containerColor = MaterialTheme.colorScheme.surface,
            labelColor = MaterialTheme.colorScheme.onSurface,
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
        ),
        modifier = modifier
    )
}

@Composable
private fun DonutGraphsSection(
    attendanceData: List<AggregatedAttendance>,
    onAction: (StudentAttendanceDashboardAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.md)
    ) {
        Text(
            text = "Average Attendance per Course",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(DesignToken.spacing.lg)
        ) {
            items(
                items = attendanceData,
                key = { it.courseId }
            ) { attendance ->
                DonutGraph(
                    modifier = Modifier.width(DashboardDonutWidth),
                    value = attendance.attendedLectures,
                    total = attendance.totalLectures,
                    label = attendance.courseName,
                    strokeWidth = DesignToken.strokes.extraThick,
                    animate = true,
                    onClick = {
                        onAction(
                            StudentAttendanceDashboardAction.DonutCourseClick(
                                courseId = attendance.courseId
                            )
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun DashboardSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.md)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )

        content()
    }
}

@Composable
private fun RecentAttendancesSection(
    attendances: List<Attendance>,
    studentId: String?,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
) {
    DashboardSection(
        title = "Recent Attendances",
        modifier = modifier
    ) {
        when {
            isLoading -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.md)
                ) {
                    repeat(3) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(DesignToken.images.sm)
                                .shimmerEffect()
                        )
                    }
                }
            }

            attendances.isEmpty() -> {
                PresencifyNoResultsIndicator(
                    text = "No recent attendances found"
                )
            }

            else -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.md)
                ) {
                    attendances.forEach { attendance ->
                        val classSession = attendance.klass
                        if (classSession != null) {
                            AttendanceListItem(
                                attendanceDate = attendance.date.toReadableString(),
                                courseName = classSession.course?.name,
                                teacherName = classSession.teacher?.let {
                                    "${it.firstName} ${it.lastName}"
                                } ?: "Unknown Teacher",
                                startTime = classSession.startTime.toReadableString(),
                                endTime = classSession.endTime.toReadableString(),
                                dayOfWeek = classSession.dayOfWeek.toDisplayLabel(),
                                isPresent = attendance.attendanceStudents
                                    ?.find { it.studentId == studentId }
                                    ?.attendanceStatus,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WeeklyAttendanceTrendChart(
    attendanceData: List<AggregatedAttendance>,
    detailedAttendance: Map<String, List<DetailedAttendanceRecord>>,
    semester: Semester,
    originalAttendanceData: List<AggregatedAttendance>,
    showOverall: Boolean,
    allAttendanceData: List<AggregatedAttendance>,
    modifier: Modifier = Modifier,
) {
    if (attendanceData.isEmpty() && !showOverall) return
    if (detailedAttendance.isEmpty()) return

    val scrollState = rememberScrollState()
    val weekSundays = getWeekSundays(semester.startDate, semester.endDate)
    val numberOfWeeks = weekSundays.size
    if (numberOfWeeks == 0) return

    val firstMonday = getMondayOfWeek(semester.startDate)
    val courseWeeklyPercentages = attendanceData.map { course ->
        val records = detailedAttendance[course.courseId].orEmpty()
        calculateWeeklyPercentages(records, firstMonday, numberOfWeeks)
    }

    val overallWeeklyPercentages = if (showOverall) {
        val allCoursePercentages = allAttendanceData.map { course ->
            val records = detailedAttendance[course.courseId].orEmpty()
            calculateWeeklyPercentages(records, firstMonday, numberOfWeeks)
        }
        if (allCoursePercentages.isNotEmpty()) {
            (0 until numberOfWeeks).map { weekIndex ->
                val nonZeroValues = allCoursePercentages
                    .map { it[weekIndex] }
                    .filter { it > 0f }
                if (nonZeroValues.isNotEmpty()) {
                    nonZeroValues.average().toFloat()
                } else {
                    0f
                }
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

    val weekLabels = weekSundays.map { sunday ->
        val monthName = sunday.month.name.take(3).lowercase()
            .replaceFirstChar { it.uppercase() }
        "${sunday.day} $monthName"
    }
    val courseKey = (if (showOverall) "overall," else "") +
            attendanceData.joinToString(",") { it.courseId }

    key(courseKey) {
        val modelProducer = androidx.compose.runtime.remember { CartesianChartModelProducer() }
        LaunchedEffect(courseKey, numberOfWeeks) {
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
                    fill = LineCartesianLayer.LineFill.single(vicoFill(Color.Transparent)),
                    pointConnector = LineCartesianLayer.PointConnector.cubic()
                )
            )

            if (showOverall && overallWeeklyPercentages != null) {
                add(
                    LineCartesianLayer.Line(
                        fill = LineCartesianLayer.LineFill.single(vicoFill(overallColor)),
                        pointConnector = LineCartesianLayer.PointConnector.cubic()
                    )
                )
            }

            attendanceData.forEach { course ->
                val originalIndex = originalAttendanceData.indexOfFirst { it.courseId == course.courseId }
                add(
                    LineCartesianLayer.Line(
                        fill = LineCartesianLayer.LineFill.single(
                            vicoFill(getChartColorForIndex(originalIndex.coerceAtLeast(0)))
                        ),
                        pointConnector = LineCartesianLayer.PointConnector.cubic()
                    )
                )
            }
        }

        val chartWidth = maxOf(DashboardMinChartWidth, DashboardWeekWidth * numberOfWeeks)

        Box(
            modifier = modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
        ) {
            Box(
                modifier = Modifier
                    .width(chartWidth)
                    .height(DashboardChartHeight)
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
                            valueFormatter = { _, value, _ -> "${value.toInt()}%" }
                        ),
                        bottomAxis = HorizontalAxis.rememberBottom(
                            valueFormatter = { _, value, _ ->
                                val index = value.toInt().coerceIn(0, weekLabels.size - 1)
                                weekLabels[index]
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

private fun getMondayOfWeek(date: LocalDate): LocalDate {
    val daysFromMonday = date.dayOfWeek.ordinal
    return LocalDate.fromEpochDays(date.toEpochDays() - daysFromMonday)
}

private fun getWeekSundays(
    semesterStart: LocalDate,
    semesterEnd: LocalDate,
): List<LocalDate> {
    val firstMonday = getMondayOfWeek(semesterStart)
    var sunday = LocalDate.fromEpochDays(firstMonday.toEpochDays() + 6)
    val sundays = mutableListOf<LocalDate>()

    while (sunday.toEpochDays() <= semesterEnd.toEpochDays() + 6) {
        sundays.add(sunday)
        sunday = LocalDate.fromEpochDays(sunday.toEpochDays() + 7)
        if (sundays.last().toEpochDays() > semesterEnd.toEpochDays() + 6) break
    }

    return sundays
}

private fun calculateWeeklyPercentages(
    records: List<DetailedAttendanceRecord>,
    firstMonday: LocalDate,
    numberOfWeeks: Int,
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
            val attended = weekRecords.count { it.attendanceStatus }
            (attended.toFloat() / weekRecords.size.toFloat()) * 100f
        }
    }
}

@Composable
private fun getChartColorForIndex(index: Int): Color {
    val colorScheme = MaterialTheme.colorScheme
    val colors = listOf(
        colorScheme.primary,
        colorScheme.secondary,
        colorScheme.tertiary,
        colorScheme.error,
        colorScheme.inversePrimary,
        colorScheme.outline,
        colorScheme.onSecondaryContainer,
        colorScheme.onTertiaryContainer,
    )
    return colors[index % colors.size]
}

@Composable
private fun getOverallChartColor(): Color {
    return MaterialTheme.colorScheme.onSurface
}
