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
import subha.app.cyra.feature.profilesetup.domain.WeightConverter
import subha.app.cyra.feature.profilesetup.domain.WeightUnit
import subha.app.cyra.ui.components.CyraLabeledSlider
import subha.app.cyra.ui.components.CyraSegmentedToggle
import subha.app.cyra.ui.components.CyraTextField
import subha.app.cyra.ui.components.ScaleIcon

private const val MIN_WEIGHT_KG = 30
private const val MAX_WEIGHT_KG = 150

@Composable
fun ProfileSetupWeightStep(
    weightKg: Int,
    weightUnit: WeightUnit,
    onWeightChange: (Int) -> Unit,
    onWeightUnitChange: (WeightUnit) -> Unit,
    modifier: Modifier = Modifier,
) {
    ProfileSetupStepScaffold(
        title = stringResource(R.string.profile_setup_weight_title),
        subtitle = stringResource(R.string.profile_setup_weight_subtitle),
        modifier = modifier,
    ) {
        Text(text = stringResource(R.string.profile_setup_weight_label), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(8.dp))

        val isMetric = weightUnit == WeightUnit.KG
        val displayValue = if (isMetric) weightKg else WeightConverter.kgToLb(weightKg)
        val displayLabel = if (isMetric) "$weightKg kg" else "$displayValue lb"
        val range = if (isMetric) MIN_WEIGHT_KG..MAX_WEIGHT_KG else WeightConverter.kgToLb(MIN_WEIGHT_KG)..WeightConverter.kgToLb(MAX_WEIGHT_KG)
        val ticks = if (isMetric) {
            listOf(30, 60, 90, 120, 150)
        } else {
            listOf(30, 60, 90, 120, 150).map { WeightConverter.kgToLb(it) }
        }

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            CyraTextField(
                value = displayValue.toString(),
                onValueChange = { input ->
                    val parsed = input.toIntOrNull() ?: return@CyraTextField
                    onWeightChange(if (isMetric) parsed else WeightConverter.lbToKg(parsed))
                },
                placeholder = stringResource(R.string.profile_setup_weight_placeholder),
                leadingIcon = { ScaleIcon() },
                keyboardType = KeyboardType.Number,
                modifier = Modifier.weight(1f),
            )
            CyraSegmentedToggle(
                options = listOf("kg", "lb"),
                selectedIndex = if (isMetric) 0 else 1,
                onOptionSelected = { index -> onWeightUnitChange(if (index == 0) WeightUnit.KG else WeightUnit.LB) },
                modifier = Modifier.width(140.dp),
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        CyraLabeledSlider(
            value = displayValue,
            range = range,
            valueLabel = displayLabel,
            ticks = ticks,
            onValueChange = { raw -> onWeightChange(if (isMetric) raw else WeightConverter.lbToKg(raw)) },
        )
    }
}
