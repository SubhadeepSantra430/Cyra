package subha.app.cyra.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import subha.app.cyra.ui.theme.CyraTheme

/**
 * A row of dots showing position within a paged flow (onboarding, future multi-step
 * forms, etc.) - Compose Foundation has no built-in equivalent (unlike SwiftUI's
 * `TabView(.page)`, which gets one for free), so this is a real reusable component.
 */
@Composable
fun PageIndicatorDots(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(pageCount) { index ->
            val selected = index == currentPage
            val size by animateDpAsState(targetValue = if (selected) 10.dp else 8.dp, label = "dotSize")
            val color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
            Box(
                modifier = Modifier
                    .size(size)
                    .background(color = color, shape = CircleShape),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PageIndicatorDotsPreview() {
    CyraTheme {
        PageIndicatorDots(pageCount = 3, currentPage = 1, modifier = Modifier)
    }
}
