package subha.app.cyra.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.dp
import subha.app.cyra.ui.theme.CyraTheme
import kotlin.math.roundToInt

/**
 * A [Slider] with a floating value bubble that tracks the thumb (e.g. "165 cm") and
 * min/max/intermediate tick labels underneath - used for the height and weight steps.
 * The bubble's horizontal position is computed from the same fraction the thumb sits
 * at, via a small custom [Layout] rather than a second copy of Material3's internal
 * thumb-padding math (not exposed) - close enough for a value hint, not pixel-exact
 * with the thumb's true touch-target center.
 */
@Composable
fun CyraLabeledSlider(
    value: Int,
    range: IntRange,
    valueLabel: String,
    ticks: List<Int>,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val fraction = (value - range.first).toFloat() / (range.last - range.first).toFloat()

    Column(modifier = modifier) {
        Layout(
            content = {
                Box(
                    modifier = Modifier
                        .background(color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(999.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                ) {
                    Text(text = valueLabel, color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.labelMedium)
                }
                Slider(
                    value = value.toFloat(),
                    onValueChange = { onValueChange(it.roundToInt()) },
                    valueRange = range.first.toFloat()..range.last.toFloat(),
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = MaterialTheme.colorScheme.primaryContainer,
                    ),
                )
            },
        ) { measurables, constraints ->
            val sliderPlaceable = measurables[1].measure(constraints)
            val bubblePlaceable = measurables[0].measure(constraints.copy(minWidth = 0))
            val spacing = 8.dp.roundToPx()
            val totalHeight = bubblePlaceable.height + spacing + sliderPlaceable.height

            layout(sliderPlaceable.width, totalHeight) {
                val bubbleX = (fraction * sliderPlaceable.width - bubblePlaceable.width / 2f)
                    .coerceIn(0f, (sliderPlaceable.width - bubblePlaceable.width).toFloat())
                bubblePlaceable.placeRelative(bubbleX.roundToInt(), 0)
                sliderPlaceable.placeRelative(0, bubblePlaceable.height + spacing)
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            ticks.forEach { tick ->
                Text(text = tick.toString(), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@CyraPreviews
@Composable
private fun CyraLabeledSliderPreview() {
    CyraTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            CyraLabeledSlider(
                value = 165,
                range = 140..190,
                valueLabel = "165 cm",
                ticks = listOf(140, 150, 160, 170, 180, 190),
                onValueChange = {},
            )
        }
    }
}
