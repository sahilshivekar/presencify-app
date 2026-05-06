package edu.watumull.presencify.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.text.style.TextAlign
import edu.watumull.presencify.core.designsystem.theme.DesignToken

// Can be used for showing empty search results, failure in loading data on a details screen
@Composable
fun PresencifyDefaultLoadingScreen(
    modifier: Modifier = Modifier,
    text: String? = null,
) {
    Column(
        modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(DesignToken.spacing.lg),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Card(
            modifier = modifier
                .semantics { testTag = "PresencifyLoadingDialog" }
                .testTag("PresencifyLoadingDialog"),
            shape = MaterialTheme.shapes.extraLarge, // Rounded square borders
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = DesignToken.elevation.sm
            )
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(DesignToken.components.progressMd),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = DesignToken.strokes.md
            )
        }
        text?.let {
            Spacer(modifier = Modifier.height(DesignToken.spacing.xl))

            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
            )
        }

    }
}