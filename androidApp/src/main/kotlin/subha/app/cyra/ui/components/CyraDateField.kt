package subha.app.cyra.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import subha.app.cyra.R
import subha.app.cyra.ui.theme.CyraTheme

/**
 * A [CyraTextField]-styled row that opens a native Material3 [DatePickerDialog] instead
 * of taking keyboard input - used for date of birth and last-period-start-date. Dates
 * cross the Compose `DatePicker`'s UTC-epoch-millis boundary via UTC (not the device's
 * local zone) on both sides, which is the standard fix for the well-known
 * off-by-one-day bug that native date pickers have in negative-UTC-offset zones.
 */
@Composable
fun CyraDateField(
    date: LocalDate?,
    placeholder: String,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    errorText: String? = null,
    enabled: Boolean = true,
) {
    var showPicker by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        OutlinedTextField(
            value = date?.toString().orEmpty(),
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    enabled = enabled,
                    onClick = { showPicker = true },
                ),
            enabled = false,
            readOnly = true,
            placeholder = { Text(text = placeholder, style = MaterialTheme.typography.bodyLarge) },
            leadingIcon = { CalendarIcon() },
            isError = errorText != null,
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            textStyle = MaterialTheme.typography.bodyLarge,
            colors = OutlinedTextFieldDefaults.colors(
                disabledContainerColor = MaterialTheme.colorScheme.surface,
                disabledBorderColor = if (errorText != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
        )
        if (errorText != null) {
            Text(
                text = errorText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp),
            )
        }
    }

    if (showPicker) {
        val initialMillis = date?.atStartOfDayIn(TimeZone.UTC)?.toEpochMilliseconds()
        val pickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let { millis ->
                        val localDate = Instant.fromEpochMilliseconds(millis).toLocalDateTime(TimeZone.UTC).date
                        onDateSelected(localDate)
                    }
                    showPicker = false
                }) {
                    Text(stringResource(R.string.profile_setup_date_picker_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) {
                    Text(stringResource(R.string.profile_setup_date_picker_cancel))
                }
            },
        ) {
            DatePicker(state = pickerState)
        }
    }
}

@CyraPreviews
@Composable
private fun CyraDateFieldPreview() {
    CyraTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            CyraDateField(date = LocalDate(2000, 7, 23), placeholder = "Select your date of birth", onDateSelected = {})
        }
    }
}
