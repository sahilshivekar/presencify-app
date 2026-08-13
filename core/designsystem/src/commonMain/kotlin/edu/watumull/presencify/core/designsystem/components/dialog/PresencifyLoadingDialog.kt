package edu.watumull.presencify.core.designsystem.components.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import edu.watumull.presencify.core.designsystem.theme.DesignToken


@Composable
fun PresencifyLoadingDialog(
    modifier: Modifier = Modifier,
    message: String? = null,
    isVisible: Boolean,
) {
    if (isVisible) {
        Dialog(
            onDismissRequest = {},
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false
            )
        ) {
            Card(
                modifier = modifier
                    .semantics { testTag = "PresencifyLoadingDialog" }
                    .testTag("PresencifyLoadingDialog"),
                shape = MaterialTheme.shapes.extraLarge,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = DesignToken.elevation.lg
                )
            ) {
                Column(
                    modifier = Modifier.padding(DesignToken.spacing.xxl),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(DesignToken.components.progressMd),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = DesignToken.strokes.thick
                    )

                    message?.let {
                        Spacer(modifier = Modifier.height(DesignToken.spacing.xl))
                        
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}