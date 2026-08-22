import SwiftUI
import SharedLogic

/// A `CyraTextField`-styled row that opens a native `DatePicker` in a sheet instead of
/// taking keyboard input - mirrors Android's `CyraDateField.kt`. Used for date of birth
/// and last-period-start-date. Converts through `Calendar.current` `DateComponents` on
/// both sides, since the shared `LocalDate` (kotlinx-datetime) has no direct `Date`
/// bridge.
struct CyraDateField: View {
    let date: LocalDate?
    let placeholder: String
    let onDateSelected: (LocalDate) -> Void
    var errorText: String? = nil

    @State private var showPicker = false
    @State private var pickerDate = Date()

    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            Button(action: {
                pickerDate = date?.toFoundationDate() ?? Date()
                showPicker = true
            }) {
                HStack(spacing: 10) {
                    Image(systemName: "calendar")
                        .foregroundColor(.cyraOnSurfaceVariant)
                        .frame(width: 20)
                    Text(date?.isoString ?? placeholder)
                        .font(CyraFont.bodyLarge())
                        .foregroundColor(date != nil ? .cyraOnSurface : .cyraOnSurfaceVariant)
                    Spacer()
                }
                .padding(.horizontal, 16)
                .padding(.vertical, 14)
                .background(RoundedRectangle(cornerRadius: 16).fill(Color.cyraSurface))
                .overlay(
                    RoundedRectangle(cornerRadius: 16)
                        .stroke(errorText != nil ? Color.cyraError : Color.cyraOutline, lineWidth: 1),
                )
            }
            .buttonStyle(.plain)

            if let errorText {
                Text(errorText)
                    .font(CyraFont.bodySmall())
                    .foregroundColor(.cyraError)
                    .padding(.horizontal, 4)
            }
        }
        .sheet(isPresented: $showPicker) {
            VStack(spacing: 0) {
                DatePicker("", selection: $pickerDate, displayedComponents: .date)
                    .datePickerStyle(.graphical)
                    .labelsHidden()
                    .padding()
                Button(String(localized: "profile_setup_date_picker_confirm")) {
                    onDateSelected(pickerDate.toKotlinLocalDate())
                    showPicker = false
                }
                .buttonStyle(CyraPrimaryButtonStyle())
                .frame(maxWidth: .infinity)
                .padding()
            }
            .presentationDetents([.medium])
        }
    }
}

extension LocalDate {
    /// ISO-8601 (`yyyy-MM-dd`) - Kotlin's `toString()` bridges to Swift as the method
    /// `description()` (not the `description` property NSObject subclasses usually
    /// get), and already returns exactly this format.
    var isoString: String { self.description() }

    func toFoundationDate() -> Date {
        var components = DateComponents()
        components.year = Int(self.year)
        components.month = Int(self.monthNumber)
        components.day = Int(self.day)
        return Calendar.current.date(from: components) ?? Date()
    }
}

extension Date {
    func toKotlinLocalDate() -> LocalDate {
        let components = Calendar.current.dateComponents([.year, .month, .day], from: self)
        return LocalDate(year: Int32(components.year ?? 1970), month: Int32(components.month ?? 1), day: Int32(components.day ?? 1))
    }
}

#Preview {
    CyraDateField(date: LocalDate(year: 2000, month: 7, day: 23), placeholder: "Select your date of birth", onDateSelected: { _ in })
        .padding(24)
        .cyraThemed()
}
