package edu.watumull.presencify.feature.users.mark_unmark_student_dropout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import edu.watumull.presencify.core.designsystem.components.PresencifyButton
import edu.watumull.presencify.core.designsystem.components.PresencifyDefaultLoadingScreen
import edu.watumull.presencify.core.designsystem.components.PresencifyNoResultsIndicator
import edu.watumull.presencify.core.designsystem.components.PresencifyScaffold
import edu.watumull.presencify.core.designsystem.components.PresencifyTextField
import edu.watumull.presencify.core.designsystem.components.dialog.PresencifyAlertDialog
import edu.watumull.presencify.core.designsystem.theme.DesignToken
import edu.watumull.presencify.core.presentation.UiConstants

@Composable
fun MarkUnmarkStudentAsDropoutScreen(
    state: MarkUnmarkStudentAsDropoutState,
    onAction: (MarkUnmarkStudentAsDropoutAction) -> Unit,
) {
    PresencifyScaffold(
        backPress = { onAction(MarkUnmarkStudentAsDropoutAction.BackButtonClick) },
        topBarTitle = "Mark/Unmark Student as Dropout",
    ) { paddingValues ->
        when (state.viewState) {
            is MarkUnmarkStudentAsDropoutState.ViewState.Loading -> {
                PresencifyDefaultLoadingScreen()
            }

            is MarkUnmarkStudentAsDropoutState.ViewState.Error -> {
                PresencifyNoResultsIndicator(
                    text = state.viewState.message.asString()
                )
            }

            is MarkUnmarkStudentAsDropoutState.ViewState.Content -> {
                MarkUnmarkStudentAsDropoutScreenContent(
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
            message = dialogState.message.asString(),
            onConfirm = {
                when (dialogState.dialogIntention) {
                    DialogIntention.GENERIC -> {
                        onAction(MarkUnmarkStudentAsDropoutAction.DismissDialog)
                    }
                }
            },
            onDismiss = {
                onAction(MarkUnmarkStudentAsDropoutAction.DismissDialog)
            }
        )
    }
}

@Composable
private fun MarkUnmarkStudentAsDropoutScreenContent(
    state: MarkUnmarkStudentAsDropoutState,
    onAction: (MarkUnmarkStudentAsDropoutAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background)
            .padding(DesignToken.spacing.lg),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = UiConstants.MAX_CONTENT_WIDTH)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.lg),
            horizontalAlignment = Alignment.Start
        ) {
            // Instructions
            Text(
                text = "Select the academic year for which you want to mark students as dropout",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Academic Year Selection
            Column(
                verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.sm)
            ) {
                Text(
                    text = "Academic Year",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(DesignToken.spacing.sm)
                ) {
                    PresencifyTextField(
                        value = state.startYear,
                        onValueChange = { onAction(MarkUnmarkStudentAsDropoutAction.UpdateStartYear(it)) },
                        label = "Start Year",
                        supportingText = state.startYearError,
                        isError = state.startYearError != null,
                        modifier = Modifier.weight(1f)
                    )

                    PresencifyTextField(
                        value = state.endYear,
                        onValueChange = { onAction(MarkUnmarkStudentAsDropoutAction.UpdateEndYear(it)) },
                        label = "End Year",
                        supportingText = state.endYearError,
                        isError = state.endYearError != null,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Continue Button
            PresencifyButton(
                onClick = { onAction(MarkUnmarkStudentAsDropoutAction.ContinueClick) },
                text = "Continue",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = DesignToken.spacing.lg)
            )
        }
    }
}
