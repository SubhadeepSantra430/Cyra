import SwiftUI
import SharedLogic

// Mirrors ProfileSetupStep.TOTAL_STEPS on the Kotlin side (8 steps: Name...AllSet) - the
// companion object constant doesn't bridge to a directly callable Swift static member,
// so this is kept in sync by hand rather than fought over an interop path.
private let totalProfileSetupSteps = 8

/// The post-signup profile-setup flow - one screen, one `ProfileSetupViewModel`, the
/// body swapped per `state.step`. Mirrors Android's `ProfileSetupScreen.kt` and the
/// existing Auth screens' shape (state collection, effect handling).
struct ProfileSetupView: View {
    let userId: String
    let onNavigate: (NavigationEvent) -> Void

    @State private var viewModel: ProfileSetupViewModel
    @State private var state: ProfileSetupState
    @EnvironmentObject private var snackbarController: CyraSnackbarController

    init(userId: String, onNavigate: @escaping (NavigationEvent) -> Void) {
        self.userId = userId
        self.onNavigate = onNavigate
        let vm = provideProfileSetupViewModel(userId: userId)
        _viewModel = State(initialValue: vm)
        _state = State(initialValue: vm.uiState.value)
    }

    var body: some View {
        VStack(spacing: 0) {
            CyraCategoryStepHeader(
                stepNumber: Int(state.step.stepNumber),
                totalSteps: totalProfileSetupSteps,
                category: state.category.map { String(localized: String.LocalizationValue($0.messageKey)) },
                onBackClick: state.showBackButton ? viewModel.onBackClicked : nil,
            )
            .padding(.horizontal, 24)
            .padding(.top, 16)

            ScrollView {
                stepContent
                    .padding(.horizontal, 24)
                    .padding(.vertical, 24)
            }

            Button(String(localized: String.LocalizationValue(state.primaryButtonLabelKey)), action: viewModel.onPrimaryButtonClicked)
                .buttonStyle(CyraPrimaryButtonStyle(isEnabled: state.isPrimaryButtonEnabled && !state.isSubmitting))
                .frame(maxWidth: .infinity)
                .disabled(!state.isPrimaryButtonEnabled || state.isSubmitting)
                .padding(.horizontal, 24)
                .padding(.vertical, 16)
        }
        .background(Color.cyraBackground.ignoresSafeArea())
        .task {
            for await newState in viewModel.uiState {
                state = newState
            }
        }
        .task {
            for await effect in viewModel.sideEffect {
                handleProfileSetupEffect(effect, onNavigate: onNavigate, snackbarController: snackbarController)
            }
        }
    }

    @ViewBuilder
    private var stepContent: some View {
        switch state.step {
        case .name:
            ProfileSetupNameStep(name: state.name, nameError: state.nameError, onNameChange: viewModel.onNameChanged)
        case .birthday:
            ProfileSetupBirthdayStep(dateOfBirth: state.dateOfBirth, dateOfBirthError: state.dateOfBirthError, onDateOfBirthChange: viewModel.onDateOfBirthChanged)
        case .height:
            ProfileSetupHeightStep(heightCm: state.heightCm, heightUnit: state.heightUnit, onHeightChange: viewModel.onHeightChanged, onHeightUnitChange: viewModel.onHeightUnitToggled)
        case .weight:
            ProfileSetupWeightStep(weightKg: state.weightKg, weightUnit: state.weightUnit, onWeightChange: viewModel.onWeightChanged, onWeightUnitChange: viewModel.onWeightUnitToggled)
        case .maritalStatus:
            ProfileSetupMaritalStatusStep(selected: state.maritalStatus, onSelected: viewModel.onMaritalStatusSelected)
        case .lastPeriod:
            ProfileSetupLastPeriodStep(
                lastPeriodStartDate: state.lastPeriodStartDate,
                onDateChange: viewModel.onLastPeriodStartDateChanged,
                onDontRememberClick: viewModel.onPrimaryButtonClicked,
            )
        case .cycleInfo:
            ProfileSetupCycleInfoStep(
                averageCycleLengthDays: state.averageCycleLengthDays,
                averagePeriodDurationDays: state.averagePeriodDurationDays,
                cycleRegularity: state.cycleRegularity,
                onCycleLengthChange: viewModel.onAverageCycleLengthChanged,
                onPeriodDurationChange: viewModel.onAveragePeriodDurationChanged,
                onCycleRegularityChange: viewModel.onCycleRegularitySelected,
            )
        case .allSet:
            ProfileSetupAllSetStep()
        default:
            EmptyView()
        }
    }
}
