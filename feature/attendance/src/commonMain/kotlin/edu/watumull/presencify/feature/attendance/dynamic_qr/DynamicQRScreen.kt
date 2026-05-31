package edu.watumull.presencify.feature.attendance.dynamic_qr

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import edu.watumull.presencify.core.designsystem.Res
import edu.watumull.presencify.core.designsystem.components.PresencifyButton
import edu.watumull.presencify.core.designsystem.components.PresencifyOutlinedButton
import edu.watumull.presencify.core.designsystem.components.PresencifyScaffold
import edu.watumull.presencify.core.designsystem.components.PresencifyTextButton
import edu.watumull.presencify.core.designsystem.presencify_logo_circle_svg
import edu.watumull.presencify.core.designsystem.theme.DesignToken
import edu.watumull.presencify.core.domain.model.schedule.ClassSession
import edu.watumull.presencify.core.presentation.UiConstants
import edu.watumull.presencify.core.presentation.components.ClassListItem
import edu.watumull.presencify.core.presentation.utils.toReadableString
import io.github.alexzhirkevich.qrose.options.QrBallShape
import io.github.alexzhirkevich.qrose.options.QrBrush
import io.github.alexzhirkevich.qrose.options.QrFrameShape
import io.github.alexzhirkevich.qrose.options.QrLogoPadding
import io.github.alexzhirkevich.qrose.options.roundCorners
import io.github.alexzhirkevich.qrose.options.solid
import io.github.alexzhirkevich.qrose.rememberQrCodePainter
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.painterResource

@Composable
fun DynamicQRScreen(
    state: DynamicQRState,
    onAction: (DynamicQRAction) -> Unit
) {
    PresencifyScaffold(
        backPress = { onAction(DynamicQRAction.NavigateBack) },
        topBarTitle = "Dynamic QR Attendance"
    ) { paddingValues ->

        when (state.viewState) {
            DynamicQRState.ViewState.Loading -> {
                CircularProgressIndicator()
            }

            is DynamicQRState.ViewState.Error -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.lg)
                ) {
                    Text(
                        text = state.viewState.message.asString(),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                    Button(onClick = { onAction(DynamicQRAction.NavigateBack) }) {
                        Text("Go Back")
                    }
                }
            }

            DynamicQRState.ViewState.Content -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(MaterialTheme.colorScheme.background)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(
                        modifier = Modifier
                            .widthIn(max = UiConstants.MAX_CONTENT_WIDTH)
                            .padding(DesignToken.spacing.lg),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        state.attendance?.let { attendance ->
                            attendance.klass?.let { classSession ->
                                ClassDetailsSection(
                                    classSession = classSession,
                                    date = attendance.date
                                )
                            }
                        }
                        if (!state.isStopped) {
                            PresencifyTextButton(
                                onClick = { onAction(DynamicQRAction.StopButtonClick) },
                                modifier = Modifier.align(Alignment.Start),
                                colors = ButtonDefaults.textButtonColors(
                                    containerColor = Color.Transparent,
                                    disabledContainerColor = Color.Transparent,
                                    contentColor = MaterialTheme.colorScheme.error,
                                    disabledContentColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                                ),
                            ) {
                                Text("Stop")
                            }
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(DesignToken.spacing.lg)
                            ) {
                                PresencifyOutlinedButton(
                                    onClick = { onAction(DynamicQRAction.ShowQRClick) },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Show QR")
                                }
                                PresencifyButton(
                                    onClick = { onAction(DynamicQRAction.NavigateToDetails) },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("View Attendance")
                                }
                            }
                        }
                        val logo = painterResource(resource = Res.drawable.presencify_logo_circle_svg)
                        if (!state.isStopped && state.qrCodeContent.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .padding(vertical = DesignToken.spacing.lg)
                                    .background(Color.White, MaterialTheme.shapes.extraLarge)
                                    .padding(DesignToken.spacing.lg), // Extra quiet zone for better aesthetics
                                contentAlignment = Alignment.Center
                            ) {
                                val painter = rememberQrCodePainter(state.qrCodeContent) {
                                    logo {
                                        painter = logo
                                        padding = QrLogoPadding.Natural(.1f)
                                        size = 0.2f
                                    }
                                    shapes {
                                        ball = QrBallShape.roundCorners(.25f)
                                        frame = QrFrameShape.roundCorners(.25f)
                                    }
                                    colors {
                                        dark = QrBrush.solid(Color.Black)
                                        light = QrBrush.solid(Color.White)
                                        frame = QrBrush.solid(Color.Black)
                                        ball = QrBrush.solid(Color.Black)
                                    }
                                }
                                Image(
                                    painter = painter,
                                    contentDescription = "Dynamic QR Code",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1f)
                                )
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
        verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.sm),
        modifier = Modifier.padding(bottom = DesignToken.spacing.lg)
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
