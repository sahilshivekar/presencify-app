package edu.watumull.presencify.feature.attendance.attendance_dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.watumull.presencify.core.design.systems.components.PresencifyActionBar
import edu.watumull.presencify.core.design.systems.components.PresencifyScaffold
import edu.watumull.presencify.core.presentation.UiConstants

@Composable
fun AttendanceDashboardScreen(
    state: AttendanceDashboardState,
    onAction: (AttendanceDashboardAction) -> Unit
) {
    PresencifyScaffold(
        backPress = { onAction(AttendanceDashboardAction.BackButtonClick) },
//        topBarTitle = "Attendance",
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onAction(AttendanceDashboardAction.NavigateToCreateAttendance) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create Attendance"
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = UiConstants.MAX_CONTENT_WIDTH)
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PresencifyActionBar(
                    text = "Student Attendance Analytics",
                    onClick = { onAction(AttendanceDashboardAction.NavigateToStudentAttendanceAnalytics) },
                    leadingImageVector = Icons.Default.Person,
                    modifier = Modifier.fillMaxWidth()
                )

                PresencifyActionBar(
                    text = "Aggregate Attendance Analytics",
                    onClick = { onAction(AttendanceDashboardAction.NavigateToAggregateAttendanceAnalytics) },
                    leadingImageVector = Icons.Default.Analytics,
                    modifier = Modifier.fillMaxWidth()
                )

                PresencifyActionBar(
                    text = "Search Attendance",
                    onClick = { onAction(AttendanceDashboardAction.NavigateToSearchAttendance) },
                    leadingImageVector = Icons.Default.Search,
                    modifier = Modifier.fillMaxWidth()
                )

                PresencifyActionBar(
                    text = "Add Student Biometrics",
                    onClick = { onAction(AttendanceDashboardAction.NavigateToSearchStudentForBiometrics) },
                    leadingImageVector = Icons.Default.Face,
                    modifier = Modifier.fillMaxWidth()
                )

                PresencifyActionBar(
                    text = "Defaulters",
                    onClick = { onAction(AttendanceDashboardAction.NavigateToDefaulters) },
                    leadingImageVector = Icons.Default.Warning,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
