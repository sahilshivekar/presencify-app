package edu.watumull.presencify.feature.schedule.class_details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import edu.watumull.presencify.core.designsystem.components.PresencifyDefaultLoadingScreen
import edu.watumull.presencify.core.designsystem.components.PresencifyNoResultsIndicator
import edu.watumull.presencify.core.designsystem.components.PresencifyScaffold
import edu.watumull.presencify.core.designsystem.components.PresencifyTextButton
import edu.watumull.presencify.core.designsystem.components.dialog.PresencifyAlertDialog
import edu.watumull.presencify.core.designsystem.theme.DesignToken
import edu.watumull.presencify.core.domain.model.auth.UserRole
import edu.watumull.presencify.core.presentation.UiConstants
import edu.watumull.presencify.core.presentation.components.ClassListItem
import edu.watumull.presencify.core.presentation.composition_locals.LocalUserRole
import edu.watumull.presencify.core.presentation.utils.toReadableString

@Composable
fun ClassDetailsScreen(
    state: ClassDetailsState,
    onAction: (ClassDetailsAction) -> Unit,
) {
    PresencifyScaffold(
        backPress = { onAction(ClassDetailsAction.NavigateBack) },
        topBarTitle = "Class Details",
    ) { paddingValues ->
        when (state.viewState) {
            is ClassDetailsState.ViewState.Loading -> {
                PresencifyDefaultLoadingScreen()
            }

            is ClassDetailsState.ViewState.Error -> {
                PresencifyNoResultsIndicator(text = state.viewState.message.asString())
            }

            is ClassDetailsState.ViewState.Content -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(paddingValues)
                        .padding(DesignToken.spacing.lg),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(
                        modifier = Modifier
                            .widthIn(max = UiConstants.MAX_CONTENT_WIDTH)
                    ) {
                        state.classSession?.let { classSession ->
                            val division = classSession.timetable?.division
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
                                teacherName = classSession.teacher?.let { "${it.firstName} ${it.lastName}" } ?: "N/A",
                                startTime = classSession.startTime.toReadableString(),
                                endTime = classSession.endTime.toReadableString(),
                                dayOfWeek = classSession.dayOfWeek.toDisplayLabel(),
                                activeFrom = classSession.activeFrom.toReadableString(),
                                activeTill = classSession.activeTill.toReadableString(),
                                classType = classSession.classType.toDisplayLabel(),
                                isExtraClass = classSession.isExtraClass,
                                roomNumber = classSession.room?.roomNumber,
                                divisionOrBatchText = divisionOrBatchText,
                                branchAbbreviation = branch?.abbreviation,
                                semesterText = semesterText,
                                onClick = null,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        if (LocalUserRole.current == UserRole.ADMIN) {
                            Spacer(modifier = Modifier.height(DesignToken.spacing.lg))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                PresencifyTextButton(
                                    onClick = { onAction(ClassDetailsAction.EditClassClick) },
                                    enabled = !state.isRemovingClass
                                ) {
                                    Text(
                                        text = "Edit class",
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                PresencifyTextButton(
                                    onClick = { onAction(ClassDetailsAction.RemoveClassClick) },
                                    enabled = !state.isRemovingClass
                                ) {
                                    if (state.isRemovingClass) {
                                        CircularProgressIndicator(
                                            color = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(DesignToken.components.progressMd),
                                            strokeWidth = DesignToken.strokes.md,
                                        )
                                    } else {
                                        Text(
                                            text = "Remove class",
                                            color = MaterialTheme.colorScheme.error
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

    state.dialogState?.let { dialogState ->
        PresencifyAlertDialog(
            dialogType = dialogState.dialogType,
            title = dialogState.title?.asString(),
            message = dialogState.message.asString(),
            onConfirm = { onAction(ClassDetailsAction.ConfirmRemoveClass) },
            onDismiss = { onAction(ClassDetailsAction.DismissDialog) }
        )
    }
}
