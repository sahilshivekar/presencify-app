package edu.watumull.presencify.core.designsystem.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import edu.watumull.presencify.core.designsystem.theme.DesignToken
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import kotlin.Boolean
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * A Presencify-styled, read-only text field that opens a Material3 [DatePicker] dialog.
 *
 * Mirrors the UX used in `SearchClassScreen`:
 * - Leading icon opens picker
 * - Trailing clear icon appears only when [value] != null
 * - Uses OK/Cancel dialog buttons
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun PresencifyDatePickerTextField(
    value: LocalDate?,
    onValueChange: (LocalDate?) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    supportingText: String? = null,
    isError: Boolean = false,
    pickerIconContentDescription: String = "Select date",
    clearIconContentDescription: String = "Clear date",
    isDateAllowed: (LocalDate) -> Boolean = { true },
) {

    val selectableDates = remember(isDateAllowed) {
        object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                // Convert UTC millis to LocalDate using kotlinx.datetime
                val localDate = Instant.fromEpochMilliseconds(utcTimeMillis)
                    .toLocalDateTime(TimeZone.UTC) // DatePicker API operates in UTC
                    .date

                return isDateAllowed(localDate)
            }

            // Optional: Block users from skipping to future/past years if needed
            override fun isSelectableYear(year: Int): Boolean = true
        }
    }
    val datePickerState = rememberDatePickerState(
        selectableDates = selectableDates
    )


    var showPicker by remember { mutableStateOf(false) }

    val formatter = LocalDate.Format {
        day(padding = Padding.ZERO)
        char('/')
        monthNumber(padding = Padding.ZERO)
        char('/')
        year()
    }

    val valueText = value?.format(formatter) ?: ""
    Box(modifier = modifier) {
        PresencifyTextField(
            value = valueText,
            onValueChange = { },
            label = label,
            readOnly = true,
            enabled = enabled,
            supportingText = supportingText,
            isError = isError,
            leadingIcon = {
                IconButton(onClick = { showPicker = true }, enabled = enabled) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = pickerIconContentDescription,
                    )
                }
            },
            trailingIcon = if (value != null) {
                {
                    IconButton(
                        onClick = {
                            onValueChange(null)
                            datePickerState.selectedDateMillis = null
                        },
                        enabled = enabled,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = clearIconContentDescription,
                        )
                    }
                }
            } else {
                null
            },
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                // Leave the standard Material 48dp touch target uncovered so the Clear icon still works
                .padding(end = if (value != null) 48.dp else 0.dp)
                .clickable(
                    enabled = enabled,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { showPicker = true }
                )
        )
    }
    if (showPicker) {
        AlertDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val selectedDate = Instant
                                .fromEpochMilliseconds(millis)
                                .toLocalDateTime(TimeZone.currentSystemDefault())
                                .date
                            onValueChange(selectedDate)
                        }
                        showPicker = false
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) { Text("Cancel") }
            },
            text = { DatePicker(state = datePickerState) },
            modifier = Modifier.padding(horizontal = DesignToken.spacing.sm),
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
            )
        )
    }
}
