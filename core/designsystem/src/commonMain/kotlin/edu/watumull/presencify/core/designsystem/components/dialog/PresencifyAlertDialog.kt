package edu.watumull.presencify.core.designsystem.components.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import edu.watumull.presencify.core.designsystem.components.PresencifyTextButton


@Composable
fun PresencifyAlertDialog(
    modifier: Modifier = Modifier,
    title: String? = null,
    message: String,
    dialogType: DialogType,
    onDismiss: () -> Unit,
    onConfirm: (() -> Unit)? = null,
) {
    val containerColor = when (dialogType) {
        DialogType.ERROR -> MaterialTheme.colorScheme.errorContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    val buttonColors = when (dialogType) {
        DialogType.CONFIRM_RISKY_ACTION, DialogType.ERROR -> ButtonDefaults.buttonColors(
            contentColor = MaterialTheme.colorScheme.onErrorContainer,
            containerColor = Color.Transparent
        )

        else -> ButtonDefaults.textButtonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            if (dialogType == DialogType.CONFIRM_RISKY_ACTION || dialogType == DialogType.CONFIRM_NORMAL_ACTION) {
                PresencifyTextButton(
                    content = { Text("Confirm") },
                    onClick = onConfirm ?: {},
                    colors = buttonColors,
                )
            }
        },
        dismissButton = {
            PresencifyTextButton(
                content = {
                    Text(
                        text = when (dialogType) {
                            DialogType.INFO, DialogType.ERROR, DialogType.SUCCESS -> "Ok"
                            DialogType.CONFIRM_RISKY_ACTION, DialogType.CONFIRM_NORMAL_ACTION -> "Cancel"
                        }
                    )
                },
                onClick = onDismiss,
                colors = buttonColors,
            )
        },
        title = title?.let {
            {
                Text(
                    text = it,
                    style = MaterialTheme.typography.headlineSmall,
                )
            }
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
            )
        },
        containerColor = containerColor,
    )
}