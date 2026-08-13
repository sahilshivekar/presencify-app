package edu.watumull.presencify.feature.users.import_teachers

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import edu.watumull.presencify.core.designsystem.components.PresencifyButton
import edu.watumull.presencify.core.designsystem.components.PresencifyDefaultLoadingScreen
import edu.watumull.presencify.core.designsystem.components.PresencifyScaffold
import edu.watumull.presencify.core.designsystem.components.dialog.PresencifyAlertDialog
import edu.watumull.presencify.core.designsystem.theme.DesignToken
import edu.watumull.presencify.core.presentation.UiConstants
import edu.watumull.presencify.core.presentation.utils.CsvPicker
import edu.watumull.presencify.core.presentation.utils.pickCsvFileWithResult
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportTeachersScreen(
    state: ImportTeachersState,
    onAction: (ImportTeachersAction) -> Unit,
) {
    val scope = rememberCoroutineScope()

    PresencifyScaffold(
        backPress = { onAction(ImportTeachersAction.ClickBackButton) },
        topBarTitle = "Import Teachers",
    ) { paddingValues ->
        when (state.viewState) {
            is ImportTeachersState.ViewState.Loading -> {
                PresencifyDefaultLoadingScreen()
            }

            is ImportTeachersState.ViewState.Content -> {
                ImportTeachersScreenContent(
                    state = state,
                    onAction = onAction,
                    onPickFile = {
                        scope.launch {
                            val result = CsvPicker.pickCsvFileWithResult()
                            onAction(ImportTeachersAction.CsvFileSelected(result))
                        }
                    },
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
                onAction(ImportTeachersAction.DismissDialog)
            }
        )
    }
}

@Composable
private fun ImportTeachersScreenContent(
    state: ImportTeachersState,
    onAction: (ImportTeachersAction) -> Unit,
    onPickFile: () -> Unit,
    modifier: Modifier = Modifier,
) {
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
            verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.xl)
        ) {
            Text(
                text = "Upload a CSV file containing teacher details to bulk import teachers.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable(onClick = onPickFile)
                    .padding(DesignToken.spacing.lg),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.lg)
                ) {
                    if (state.selectedFile != null) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = "Selected File",
                            modifier = Modifier.size(DesignToken.icons.xl),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = state.selectedFileName ?: "teachers.csv",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Tap to change file",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add CSV",
                            modifier = Modifier.size(DesignToken.icons.xl),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Tap to select CSV file",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            if (state.error != null) {
                Text(
                    text = state.error.asString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }

            PresencifyButton(
                text = "Import Teachers",
                onClick = { onAction(ImportTeachersAction.ClickSubmit) },
                enabled = state.selectedFile != null && !state.isSubmitting,
                isLoading = state.isSubmitting,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
