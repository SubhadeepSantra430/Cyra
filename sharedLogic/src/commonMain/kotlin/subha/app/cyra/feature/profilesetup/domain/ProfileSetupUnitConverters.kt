package subha.app.cyra.feature.profilesetup.domain

import kotlin.math.roundToInt

/**
 * Pure cm <-> feet/inches conversion for the height slider/field's unit toggle. The
 * canonical stored value is always centimeters; a ft/in slider represents its position
 * as TOTAL inches (one continuous range), formatted as `5' 6"` for display - a compound
 * feet+inches pair doesn't map onto a single slider track otherwise.
 */
object HeightConverter {
    private const val CM_PER_INCH = 2.54

    fun cmToTotalInches(cm: Int): Int = (cm / CM_PER_INCH).roundToInt()

    fun totalInchesToCm(totalInches: Int): Int = (totalInches * CM_PER_INCH).roundToInt()

    /** e.g. `5' 6"` for 66 total inches. */
    fun formatFeetInches(totalInches: Int): String {
        val feet = totalInches / 12
        val inches = totalInches % 12
        return "$feet' $inches\""
    }
}

/** Pure kg <-> lb conversion for the weight slider/field's unit toggle. Canonical stored value is always kilograms. */
object WeightConverter {
    private const val KG_PER_LB = 0.45359237

    fun kgToLb(kg: Int): Int = (kg / KG_PER_LB).roundToInt()

    fun lbToKg(lb: Int): Int = (lb * KG_PER_LB).roundToInt()
}
