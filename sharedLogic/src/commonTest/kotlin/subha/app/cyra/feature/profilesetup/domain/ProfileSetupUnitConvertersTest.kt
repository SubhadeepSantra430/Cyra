package subha.app.cyra.feature.profilesetup.domain

import kotlin.test.Test
import kotlin.test.assertEquals

class ProfileSetupUnitConvertersTest {

    @Test
    fun cmToTotalInches_roundsToNearestInch() {
        // 165cm is ~64.96in - rounds to 65.
        assertEquals(65, HeightConverter.cmToTotalInches(165))
    }

    @Test
    fun totalInchesToCm_roundTripsCloseToOriginal() {
        val cm = 178
        val inches = HeightConverter.cmToTotalInches(cm)
        val backToCm = HeightConverter.totalInchesToCm(inches)
        // Rounding to whole inches loses at most ~1.27cm either way.
        assertEquals(true, kotlin.math.abs(backToCm - cm) <= 2)
    }

    @Test
    fun formatFeetInches_splitsTotalInchesCorrectly() {
        assertEquals("5' 6\"", HeightConverter.formatFeetInches(66))
        assertEquals("6' 0\"", HeightConverter.formatFeetInches(72))
    }

    @Test
    fun kgToLb_matchesKnownConversion() {
        // 60kg is ~132.3lb.
        assertEquals(132, WeightConverter.kgToLb(60))
    }

    @Test
    fun lbToKg_matchesKnownConversion() {
        // 150lb is ~68.0kg.
        assertEquals(68, WeightConverter.lbToKg(150))
    }
}
