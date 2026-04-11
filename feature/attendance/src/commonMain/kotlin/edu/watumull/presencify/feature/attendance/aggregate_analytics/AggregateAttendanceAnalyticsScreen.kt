package edu.watumull.presencify.feature.attendance.aggregate_analytics

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import edu.watumull.presencify.core.design.systems.components.DonutGraph
import edu.watumull.presencify.core.design.systems.components.PresencifyDropDownMenuBox
import edu.watumull.presencify.core.design.systems.components.PresencifyScaffold
import edu.watumull.presencify.core.design.systems.components.PresencifyTextField
import edu.watumull.presencify.core.design.systems.components.dialog.PresencifyAlertDialog
import edu.watumull.presencify.core.domain.enums.SemesterNumber
import edu.watumull.presencify.core.domain.model.academics.Batch
import edu.watumull.presencify.core.domain.model.academics.Branch
import edu.watumull.presencify.core.domain.model.academics.Division
import edu.watumull.presencify.core.domain.model.academics.Semester
import edu.watumull.presencify.core.domain.model.attendance.AggregatedAttendance
import edu.watumull.presencify.core.domain.model.attendance.AttendanceRecord
import edu.watumull.presencify.core.presentation.UiConstants

private const val OVERALL_CHIP_ID = "__overall__"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AggregateAttendanceAnalyticsScreen(
    state: AggregateAttendanceAnalyticsState,
    onAction: (AggregateAttendanceAnalyticsAction) -> Unit,
) {
    PresencifyScaffold(
        backPress = { onAction(AggregateAttendanceAnalyticsAction.BackButtonClick) },
        topBarTitle = "Aggregate Attendance Analytics",
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Column(
                    modifier = Modifier
                        .widthIn(max = UiConstants.MAX_CONTENT_WIDTH)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Filter Section
                    FilterSection(state = state, onAction = onAction)

                    // Loading
                    if (state.isLoadingAttendance) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    // Attendance Data
                    if (!state.isLoadingAttendance && state.attendanceData.isNotEmpty() && state.semester != null) {
                        AttendanceChartsSection(
                            attendanceData = state.attendanceData,
                            detailedRecords = state.detailedAttendanceRecords,
                            semester = state.semester,
                            onAction = onAction
                        )
                    }

                    // No data state
                    if (!state.isLoadingAttendance && state.semester != null && state.attendanceData.isEmpty()) {
                        Text(
                            text = "No attendance data available for the selected filters.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 32.dp)
                        )
                    }
                }
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
                    DialogIntention.GENERIC -> onAction(AggregateAttendanceAnalyticsAction.DismissDialog)
                }
            },
            onDismiss = { onAction(AggregateAttendanceAnalyticsAction.DismissDialog) }
        )
    }
}

@Composable
private fun FilterSection(
    state: AggregateAttendanceAnalyticsState,
    onAction: (AggregateAttendanceAnalyticsAction) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Select Semester",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )

        // Semester Number Dropdown
        PresencifyDropDownMenuBox<SemesterNumber>(
            value = state.selectedSemesterNumber?.toDisplayLabel() ?: "",
            options = SemesterNumber.entries,
            onSelectItem = { onAction(AggregateAttendanceAnalyticsAction.SelectSemesterNumber(it)) },
            label = "Semester Number *",
            itemToString = { it.toDisplayLabel() },
            expanded = state.isSemesterNumberDropdownOpen,
            onDropDownVisibilityChanged = {
                onAction(
                    AggregateAttendanceAnalyticsAction.ChangeSemesterNumberDropDownVisibility(
                        it
                    )
                )
            },
            enabled = !state.isLoadingAttendance,
            modifier = Modifier.fillMaxWidth()
        )

        // Academic Year Fields
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PresencifyTextField(
                value = state.academicStartYear,
                onValueChange = { onAction(AggregateAttendanceAnalyticsAction.UpdateAcademicStartYear(it)) },
                label = "Academic Start Year *",
                enabled = !state.isLoadingAttendance,
                modifier = Modifier.weight(1f)
            )
            PresencifyTextField(
                value = state.academicEndYear,
                onValueChange = { onAction(AggregateAttendanceAnalyticsAction.UpdateAcademicEndYear(it)) },
                label = "Academic End Year *",
                enabled = !state.isLoadingAttendance,
                modifier = Modifier.weight(1f)
            )
        }

        // Branch Dropdown
        PresencifyDropDownMenuBox<Branch>(
            value = state.selectedBranch?.name ?: "",
            options = state.branchOptions,
            onSelectItem = { onAction(AggregateAttendanceAnalyticsAction.SelectBranch(it)) },
            label = "Branch *",
            itemToString = { it.name },
            expanded = state.isBranchDropdownOpen,
            onDropDownVisibilityChanged = {
                onAction(
                    AggregateAttendanceAnalyticsAction.ChangeBranchDropDownVisibility(
                        it
                    )
                )
            },
            enabled = !state.areBranchesLoading && !state.isLoadingAttendance,
            modifier = Modifier.fillMaxWidth()
        )

        // Division and Batch dropdowns (visible after semester is resolved)
        AnimatedVisibility(
            visible = state.semester != null,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Optional Filters",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Division Dropdown
                PresencifyDropDownMenuBox<Division>(
                    value = state.selectedDivision?.divisionCode ?: "All Divisions",
                    options = state.divisionOptions,
                    onSelectItem = { onAction(AggregateAttendanceAnalyticsAction.SelectDivision(it)) },
                    label = "Division (Optional)",
                    itemToString = { it.divisionCode },
                    expanded = state.isDivisionDropdownOpen,
                    onDropDownVisibilityChanged = {
                        onAction(
                            AggregateAttendanceAnalyticsAction.ChangeDivisionDropDownVisibility(
                                it
                            )
                        )
                    },
                    enabled = !state.areDivisionsLoading && !state.isLoadingAttendance,
                    modifier = Modifier.fillMaxWidth()
                )

                // Batch Dropdown
//                PresencifyDropDownMenuBox<Batch>(
//                    value = state.selectedBatch?.batchCode ?: "All Batches",
//                    options = state.batchOptions,
//                    onSelectItem = { onAction(AggregateAttendanceAnalyticsAction.SelectBatch(it)) },
//                    label = "Batch (Optional)",
//                    itemToString = { it.batchCode },
//                    expanded = state.isBatchDropdownOpen,
//                    onDropDownVisibilityChanged = {
//                        onAction(
//                            AggregateAttendanceAnalyticsAction.ChangeBatchDropDownVisibility(
//                                it
//                            )
//                        )
//                    },
//                    enabled = !state.areBatchesLoading && !state.isLoadingAttendance,
//                    modifier = Modifier.fillMaxWidth()
//                )
            }
        }
    }
}

@Composable
private fun AttendanceChartsSection(
    attendanceData: List<AggregatedAttendance>,
    detailedRecords: Map<String, List<AttendanceRecord>>,
    onAction: (AggregateAttendanceAnalyticsAction) -> Unit,
    semester: Semester,
) {
    val selectedCourseIds = remember {
        mutableStateOf(setOf(OVERALL_CHIP_ID))
    }

    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
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

                // Course filter chips (Overall first, then individual courses)
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Overall chip
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
                                    .size(12.dp)
                                    .background(
                                        color = overallColor,
                                        shape = androidx.compose.foundation.shape.CircleShape
                                    )
                            )
                        }
                    )

                    // Individual course chips
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

                // Weekly Attendance Chart
                val filteredAttendanceData = attendanceData.filter {
                    selectedCourseIds.value.contains(it.courseId)
                }
                val showOverall = selectedCourseIds.value.contains(OVERALL_CHIP_ID)

                if (filteredAttendanceData.isNotEmpty() || showOverall) {
                    AggregateWeeklyAttendanceTrendChart(
                        attendanceData = filteredAttendanceData,
                        detailedRecords = detailedRecords,
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
                        modifier = Modifier.padding(vertical = 32.dp, horizontal = 16.dp)
                    )
                }
            }

            // Section 2: Average Attendance per Course (Donut Graphs)
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

                val rows = attendanceData.chunked(itemsPerRow)

                rows.forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        rowItems.forEach { attendance ->
                            val percentage = if (attendance.totalLectures > 0) {
                                (attendance.attendedLectures.toFloat() / attendance.totalLectures.toFloat()) * 100f
                            } else 0f

                            DonutGraph(
                                modifier = Modifier.weight(1f),
                                percentage = percentage,
                                label = attendance.courseName,
                                size = 100.dp,
                                strokeWidth = 8.dp,
                                animate = true,
                                onClick = {
                                    onAction(
                                        AggregateAttendanceAnalyticsAction.DonutCourseClick(
                                            courseId = attendance.courseId
                                        )
                                    )
                                }
                            )
                        }

                        // Spacers for left alignment on incomplete rows
                        if (rowItems.size < itemsPerRow) {
                            repeat(itemsPerRow - rowItems.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }

                    if (rowItems != rows.last()) {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

// ============================
// Chart Composables (adapted for aggregate AttendanceRecord data)
// ============================

@Composable
private fun AggregateWeeklyAttendanceTrendChart(
    attendanceData: List<AggregatedAttendance>,
    detailedRecords: Map<String, List<AttendanceRecord>>,
    semester: Semester,
    originalAttendanceData: List<AggregatedAttendance>,
    showOverall: Boolean = false,
    allAttendanceData: List<AggregatedAttendance> = emptyList(),
    modifier: Modifier = Modifier,
) {
    if (attendanceData.isEmpty() && !showOverall) return
    if (detailedRecords.isEmpty()) return

    val scrollState = rememberScrollState()

    val weekSundays = getWeekSundays(semester.startDate, semester.endDate)
    val numberOfWeeks = weekSundays.size
    if (numberOfWeeks == 0) return

    val firstMonday = getMondayOfWeek(semester.startDate)

    // Build weekly percentage data for each selected course
    val courseWeeklyPercentages = attendanceData.map { course ->
        val records = detailedRecords[course.courseId] ?: emptyList()
        calculateAggregateWeeklyPercentages(records, firstMonday, numberOfWeeks)
    }

    // Build overall average line
    val overallWeeklyPercentages = if (showOverall) {
        val allCoursePercentages = allAttendanceData.map { course ->
            val records = detailedRecords[course.courseId] ?: emptyList()
            calculateAggregateWeeklyPercentages(records, firstMonday, numberOfWeeks)
        }
        if (allCoursePercentages.isNotEmpty()) {
            (0 until numberOfWeeks).map { weekIdx ->
                val values = allCoursePercentages.map { it[weekIdx] }
                val nonZeroValues = values.filter { it > 0f }
                if (nonZeroValues.isNotEmpty()) nonZeroValues.average().toFloat() else 0f
            }
        } else null
    } else null

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
                    // Add invisible series with 0% and 100% to force Y-axis range
                    series(listOf(0.0, 0.0), listOf(0.0, 100.0))

                    allWeeklyPercentages.forEach { yData ->
                        series(xValues, yData.map { it.toDouble() })
                    }
                }
            }
        }

        val overallColor = getOverallChartColor()
        val lineSpecs = buildList {
            // Invisible line for Y-axis range anchor (0-100%)
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

// ============================
// Helper Functions
// ============================

private fun getMondayOfWeek(date: kotlinx.datetime.LocalDate): kotlinx.datetime.LocalDate {
    val dayOfWeek = date.dayOfWeek
    val daysFromMonday = dayOfWeek.ordinal
    return kotlinx.datetime.LocalDate.fromEpochDays(date.toEpochDays() - daysFromMonday)
}

private fun getWeekSundays(
    semesterStart: kotlinx.datetime.LocalDate,
    semesterEnd: kotlinx.datetime.LocalDate,
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

/**
 * Calculate weekly attendance percentages from aggregate AttendanceRecord data.
 * For each week: sum(presentStudents) / sum(totalStudents) * 100.
 * Weeks with no records yield 0%.
 */
private fun calculateAggregateWeeklyPercentages(
    records: List<AttendanceRecord>,
    firstMonday: kotlinx.datetime.LocalDate,
    numberOfWeeks: Int,
): List<Float> {
    if (records.isEmpty()) return List(numberOfWeeks) { 0f }

    val firstMondayEpoch = firstMonday.toEpochDays()

    val recordsByWeek = records.groupBy { record ->
        val daysSinceFirstMonday = record.attendanceDate.toEpochDays() - firstMondayEpoch
        (daysSinceFirstMonday / 7).toInt().coerceIn(0, numberOfWeeks - 1)
    }

    return (0 until numberOfWeeks).map { weekIndex ->
        val weekRecords = recordsByWeek[weekIndex]
        if (weekRecords.isNullOrEmpty()) {
            0f
        } else {
            val totalStudents = weekRecords.sumOf { it.totalStudents }
            val presentStudents = weekRecords.sumOf { it.presentStudents }
            if (totalStudents > 0) (presentStudents.toFloat() / totalStudents.toFloat()) * 100f else 0f
        }
    }
}

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

@Composable
private fun getOverallChartColor(): androidx.compose.ui.graphics.Color {
    return MaterialTheme.colorScheme.onSurface
}
