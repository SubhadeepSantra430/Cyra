import SwiftUI
import SharedLogic

private let minHeightCm: Int32 = 120
private let maxHeightCm: Int32 = 220

struct ProfileSetupHeightStep: View {
    let heightCm: Int32
    let heightUnit: HeightUnit
    let onHeightChange: (Int32) -> Void
    let onHeightUnitChange: (HeightUnit) -> Void

    var body: some View {
        ProfileSetupStepScaffold(
            title: String(localized: "profile_setup_height_title"),
            subtitle: String(localized: "profile_setup_height_subtitle"),
        ) {
            Text(String(localized: "profile_setup_height_label"))
                .font(CyraFont.titleSmall())
                .foregroundColor(.cyraOnSurface)
            Spacer().frame(height: 8)

            let isMetric = heightUnit == .cm
            let displayValue = isMetric ? heightCm : Int32(HeightConverter.shared.cmToTotalInches(cm: heightCm))
            let displayLabel = isMetric ? "\(heightCm) cm" : HeightConverter.shared.formatFeetInches(totalInches: displayValue)
            let range: ClosedRange<Int> = isMetric
                ? Int(minHeightCm)...Int(maxHeightCm)
                : Int(HeightConverter.shared.cmToTotalInches(cm: minHeightCm))...Int(HeightConverter.shared.cmToTotalInches(cm: maxHeightCm))
            let metricTicks = [120, 140, 160, 180, 200, 220]
            let ticks = isMetric ? metricTicks : metricTicks.map { Int(HeightConverter.shared.cmToTotalInches(cm: Int32($0))) }

            HStack(spacing: 10) {
                CyraTextField(
                    placeholder: String(localized: "profile_setup_height_placeholder"),
                    text: Binding(
                        get: { "\(displayValue)" },
                        set: { input in
                            guard let parsed = Int32(input) else { return }
                            onHeightChange(isMetric ? parsed : HeightConverter.shared.totalInchesToCm(totalInches: parsed))
                        },
                    ),
                    systemImage: "ruler",
                    keyboardType: .numberPad,
                )
                CyraSegmentedToggle(
                    options: ["cm", "ft/in"],
                    selectedIndex: isMetric ? 0 : 1,
                    onOptionSelected: { index in onHeightUnitChange(index == 0 ? .cm : .ftIn) },
                )
                .frame(width: 140)
            }

            Spacer().frame(height: 20)

            CyraLabeledSlider(
                value: Int(displayValue),
                range: range,
                valueLabel: displayLabel,
                ticks: ticks,
                onValueChange: { raw in
                    onHeightChange(isMetric ? Int32(raw) : HeightConverter.shared.totalInchesToCm(totalInches: Int32(raw)))
                },
            )
        }
    }
}
