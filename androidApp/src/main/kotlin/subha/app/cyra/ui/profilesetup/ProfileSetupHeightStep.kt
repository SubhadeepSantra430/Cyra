package subha.app.cyra.ui.profilesetup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import subha.app.cyra.R
import subha.app.cyra.feature.profilesetup.domain.HeightConverter
import subha.app.cyra.feature.profilesetup.domain.HeightUnit
import subha.app.cyra.ui.components.CyraLabeledSlider
import subha.app.cyra.ui.components.CyraSegmentedToggle
import subha.app.cyra.ui.components.CyraTextField
import subha.app.cyra.ui.components.RulerIcon

private const val MIN_HEIGHT_CM = 120
private const val MAX_HEIGHT_CM = 220

@Composable
fun ProfileSetupHeightStep(
    heightCm: Int,
    heightUnit: HeightUnit,
    onHeightChange: (Int) -> Unit,
    onHeightUnitChange: (HeightUnit) -> Unit,
    modifier: Modifier = Modifier,
) {
    ProfileSetupStepScaffold(
        title = stringResource(R.string.profile_setup_height_title),
        subtitle = stringResource(R.string.profile_setup_height_subtitle),
        modifier = modifier,
    ) {
        Text(text = stringResource(R.string.profile_setup_height_label), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(8.dp))

        val isMetric = heightUnit == HeightUnit.CM
        val displayValue = if (isMetric) heightCm else HeightConverter.cmToTotalInches(heightCm)
        val displayLabel = if (isMetric) "$heightCm cm" else HeightConverter.formatFeetInches(displayValue)
        val range = if (isMetric) MIN_HEIGHT_CM..MAX_HEIGHT_CM else HeightConverter.cmToTotalInches(MIN_HEIGHT_CM)..HeightConverter.cmToTotalInches(MAX_HEIGHT_CM)
        val ticks = if (isMetric) {
            listOf(120, 140, 160, 180, 200, 220)
        } else {
            listOf(120, 140, 160, 180, 200, 220).map { HeightConverter.cmToTotalInches(it) }
        }

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            CyraTextField(
                value = displayValue.toString(),
                onValueChange = { input ->
                    val parsed = input.toIntOrNull() ?: return@CyraTextField
                    onHeightChange(if (isMetric) parsed else HeightConverter.totalInchesToCm(parsed))
                },
                placeholder = stringResource(R.string.profile_setup_height_placeholder),
                leadingIcon = { RulerIcon() },
                keyboardType = KeyboardType.Number,
                modifier = Modifier.weight(1f),
            )
            CyraSegmentedToggle(
                options = listOf("cm", "ft/in"),
                selectedIndex = if (isMetric) 0 else 1,
                onOptionSelected = { index -> onHeightUnitChange(if (index == 0) HeightUnit.CM else HeightUnit.FT_IN) },
                modifier = Modifier.width(140.dp),
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        CyraLabeledSlider(
            value = displayValue,
            range = range,
            valueLabel = displayLabel,
            ticks = ticks,
            onValueChange = { raw -> onHeightChange(if (isMetric) raw else HeightConverter.totalInchesToCm(raw)) },
        )
    }
}
