package edu.watumull.presencify.feature.academics.batch_details

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
import androidx.compose.material3.MaterialTheme
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
import edu.watumull.presencify.core.presentation.components.BatchListItem
import edu.watumull.presencify.core.presentation.composition_locals.LocalUserRole

@Composable
fun BatchDetailsScreen(
    state: BatchDetailsState,
    onAction: (BatchDetailsAction) -> Unit,
) {
    PresencifyScaffold(
        backPress = { onAction(BatchDetailsAction.NavigateBack) },
        topBarTitle = "Batch Details",
    ) { paddingValues ->
        when (state.viewState) {
            is BatchDetailsState.ViewState.Loading -> {
                PresencifyDefaultLoadingScreen()
            }

            is BatchDetailsState.ViewState.Error -> {
                PresencifyNoResultsIndicator(text = state.viewState.message.asString())
            }

            is BatchDetailsState.ViewState.Content -> {
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
                        state.batch?.let { batch ->
                            val div = batch.division
                            val sem = div?.semester
                            BatchListItem(
                                batchCode = batch.batchCode,
                                divisionCode = div?.divisionCode ?: "",
                                semesterNumber = sem?.semesterNumber
                                    ?: edu.watumull.presencify.core.domain.enums.SemesterNumber.SEMESTER_1,
                                semesterAcademicStartYear = sem?.academicStartYear ?: 0,
                                semesterAcademicEndYear = sem?.academicEndYear ?: 0,
                                branchAbbreviation = sem?.branch?.abbreviation ?: div?.semester?.branch?.abbreviation
                                ?: "",
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
                                    onClick = { onAction(BatchDetailsAction.EditBatchClick) },
                                    enabled = !state.isRemovingBatch
                                ) {
                                    androidx.compose.material3.Text(
                                        text = "Edit batch",
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                PresencifyTextButton(
                                    onClick = { onAction(BatchDetailsAction.RemoveBatchClick) },
                                    enabled = !state.isRemovingBatch
                                ) {
                                    if (state.isRemovingBatch) {
                                        androidx.compose.material3.CircularProgressIndicator(
                                            color = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(DesignToken.components.progressMd),
                                            strokeWidth = DesignToken.strokes.md,
                                        )
                                    } else {
                                        androidx.compose.material3.Text(
                                            text = "Remove batch",
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
            onConfirm = { onAction(BatchDetailsAction.ConfirmRemoveBatch) },
            onDismiss = { onAction(BatchDetailsAction.DismissDialog) }
        )
    }
}
