package edu.watumull.presencify.core.designsystem.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.datetime.LocalTime
import kotlinx.datetime.format
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char

/**
 * A Presencify-styled, read-only text field that opens a Material3 [TimePicker] dialog.
 *
 * Mirrors the UX used in `SearchClassScreen`:
 * - Leading icon opens picker
 * - Trailing clear icon appears only when a value is set
 * - Uses OK/Cancel dialog buttons
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PresencifyTimePickerTextField(
    value: LocalTime?,
    onValueChange: (LocalTime?) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    supportingText: String? = null,
    isError: Boolean = false,
    pickerIconContentDescription: String = "Select time",
    clearIconContentDescription: String = "Clear time",
) {

//    val timePickerState = rememberTimePickerState(
//        initialHour = value?.hour ?: 0,
//        initialMinute = value?.minute ?: 0,
//        is24Hour = false,
//    )
    var showPicker by remember { mutableStateOf(false) }

    val displayText = value?.to12HourFormat() ?: ""

    PresencifyTextField(
        value = displayText,
        onValueChange = { },
        label = label,
        readOnly = true,
        enabled = enabled,
        supportingText = supportingText,
        isError = isError,
        leadingIcon = {
            IconButton(
                onClick = { showPicker = true },
                enabled = enabled,
            ) {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = pickerIconContentDescription,
                )
            }
        },
        trailingIcon = if (value != null) {
            {
                IconButton(
                    onClick = {
                        onValueChange(null)
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
        modifier = modifier,
    )

    if (showPicker) {
        val timePickerState = remember(showPicker) {
            TimePickerState(
                initialHour = value?.hour ?: 0,
                initialMinute = value?.minute ?: 0,
                is24Hour = false
            )
        }
        AlertDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selected = LocalTime(timePickerState.hour, timePickerState.minute)
                        onValueChange(selected)
                        showPicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) {
                    Text("Cancel")
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    TimePicker(state = timePickerState)
                }
            },
        )
    }
}


fun LocalTime.to12HourFormat(): String {
    val amPm = if (hour < 12) "AM" else "PM"

    val hour12 = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }

    val timePart = LocalTime(hour12, minute).format(LocalTime.Format {
        hour(Padding.ZERO)
        char(':')
        minute(Padding.ZERO)
    })

    return "$timePart $amPm"
}
