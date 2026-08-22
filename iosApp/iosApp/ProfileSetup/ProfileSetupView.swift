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
    let snackbarController: CyraSnackbarController
    let onNavigate: (NavigationEvent) -> Void

    @State private var viewModel: ProfileSetupViewModel
    @State private var state: ProfileSetupState

    init(userId: String, snackbarController: CyraSnackbarController, onNavigate: @escaping (NavigationEvent) -> Void) {
        self.userId = userId
        self.snackbarController = snackbarController
        self.onNavigate = onNavigate
        let vm = provideProfileSetupViewModel(userId: userId)
        _viewModel = State(initialValue: vm)
        _state = State(initialValue: vm.uiState.value)
    }

    var body: some View {
        Group {
            // Briefly true right after construction, while the ViewModel's init loads a
            // possible offline-first draft - rendering nothing here (rather than the
            // default "Name" step) avoids a one-frame flash before a resumed session
            // jumps to its real step. See ProfileSetupState.isLoadingDraft.
            if state.isLoadingDraft {
                Color.cyraBackground.ignoresSafeArea()
            } else {
                resolvedContent
            }
        }
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

    private var resolvedContent: some View {
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

            bottomButtons
                .padding(.horizontal, 24)
                .padding(.vertical, 16)
        }
        .background(Color.cyraBackground.ignoresSafeArea())
    }

    /// Mandatory steps (and the completion screen) get one full-width button; every
    /// optional step gets a "Skip" (left) + "Next" (right) pair instead, matching the
    /// onboarding carousel's own Skip/Next convention - mirrors Android's
    /// `ProfileSetupScreen.kt`'s bottom-button section.
    @ViewBuilder
    private var bottomButtons: some View {
        if state.showDualButtons {
            HStack {
                Button(String(localized: "profile_setup_button_skip"), action: viewModel.onSkipClicked)
                    .buttonStyle(CyraSkipButtonStyle())
                Spacer()
                Button(String(localized: "profile_setup_button_next"), action: viewModel.onNextClicked)
                    .buttonStyle(CyraPrimaryButtonStyle())
            }
        } else {
            Button(String(localized: String.LocalizationValue(state.primaryButtonLabelKey)), action: viewModel.onNextClicked)
                .buttonStyle(CyraPrimaryButtonStyle(isEnabled: state.isPrimaryButtonEnabled && !state.isSubmitting))
                .frame(maxWidth: .infinity)
                .disabled(!state.isPrimaryButtonEnabled || state.isSubmitting)
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
                onDontRememberConfirmed: viewModel.onLastPeriodUnknownConfirmed,
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
