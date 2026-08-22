import SwiftUI
import SharedLogic

private let minWeightKg: Int32 = 30
private let maxWeightKg: Int32 = 150

struct ProfileSetupWeightStep: View {
    let weightKg: Int32
    let weightUnit: WeightUnit
    let onWeightChange: (Int32) -> Void
    let onWeightUnitChange: (WeightUnit) -> Void

    var body: some View {
        ProfileSetupStepScaffold(
            title: String(localized: "profile_setup_weight_title"),
            subtitle: String(localized: "profile_setup_weight_subtitle"),
        ) {
            Text(String(localized: "profile_setup_weight_label"))
                .font(CyraFont.titleSmall())
                .foregroundColor(.cyraOnSurface)
            Spacer().frame(height: 8)

            let isMetric = weightUnit == .kg
            let displayValue = isMetric ? weightKg : WeightConverter.shared.kgToLb(kg: weightKg)
            let displayLabel = isMetric ? "\(weightKg) kg" : "\(displayValue) lb"
            let range: ClosedRange<Int> = isMetric
                ? Int(minWeightKg)...Int(maxWeightKg)
                : Int(WeightConverter.shared.kgToLb(kg: minWeightKg))...Int(WeightConverter.shared.kgToLb(kg: maxWeightKg))
            let metricTicks: [Int32] = [30, 60, 90, 120, 150]
            let ticks = isMetric ? metricTicks.map { Int($0) } : metricTicks.map { Int(WeightConverter.shared.kgToLb(kg: $0)) }

            HStack(spacing: 10) {
                CyraTextField(
                    placeholder: String(localized: "profile_setup_weight_placeholder"),
                    text: Binding(
                        get: { "\(displayValue)" },
                        set: { input in
                            guard let parsed = Int32(input) else { return }
                            onWeightChange(isMetric ? parsed : WeightConverter.shared.lbToKg(lb: parsed))
                        },
                    ),
                    systemImage: "scalemass",
                    keyboardType: .numberPad,
                )
                CyraSegmentedToggle(
                    options: ["kg", "lb"],
                    selectedIndex: isMetric ? 0 : 1,
                    onOptionSelected: { index in onWeightUnitChange(index == 0 ? .kg : .lb) },
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
                    onWeightChange(isMetric ? Int32(raw) : WeightConverter.shared.lbToKg(lb: Int32(raw)))
                },
            )
        }
    }
}
