package edu.watumull.presencify.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import edu.watumull.presencify.core.designsystem.theme.DesignToken

// Can be used for showing empty search results, failure in loading data on a details screen
@Composable
fun PresencifyNoResultsIndicator(
    modifier: Modifier = Modifier,
    text: String,
    onRetry: (() -> Unit)? = null
) {
    Column(
        modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(DesignToken.spacing.lg),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = .5f),
            modifier = Modifier.height(DesignToken.icons.xxl).width(DesignToken.icons.xxl)
        )

        Spacer(modifier = Modifier.height(DesignToken.spacing.lg))

        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = .5f),
            textAlign = TextAlign.Center,
        )

        if (onRetry != null) {
            Spacer(modifier = Modifier.height(DesignToken.spacing.lg))
            PresencifyButton(
                onClick = onRetry,
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.wrapContentSize()
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Retry",
                    modifier = Modifier
                        .height(DesignToken.icons.sm)
                        .width(DesignToken.icons.sm)
                )

                Spacer(modifier = Modifier.width(DesignToken.spacing.sm))

                Text(
                    text = "Retry",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

    }
}