package subha.app.cyra.ui.profilesetup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.datetime.LocalDate
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import subha.app.cyra.R
import subha.app.cyra.core.presentation.NavigationEvent
import subha.app.cyra.feature.profilesetup.domain.CycleRegularity
import subha.app.cyra.feature.profilesetup.domain.HeightUnit
import subha.app.cyra.feature.profilesetup.domain.MaritalStatus
import subha.app.cyra.feature.profilesetup.domain.ProfileSetupStep
import subha.app.cyra.feature.profilesetup.domain.WeightUnit
import subha.app.cyra.feature.profilesetup.presentation.ProfileSetupState
import subha.app.cyra.feature.profilesetup.presentation.ProfileSetupViewModel
import subha.app.cyra.ui.components.CyraCategoryStepHeader
import subha.app.cyra.ui.components.CyraPrimaryButton
import subha.app.cyra.ui.components.CyraTextButton

/**
 * The post-signup profile-setup flow - one screen, one [ProfileSetupViewModel], the
 * body swapped per [ProfileSetupState.step]. Mirrors Auth's screen shape (state
 * collection, effect handling via [HandleProfileSetupEffects]) but as a single
 * multi-step screen rather than three separate ones, since every step here feeds the
 * same eventual profile write - see [ProfileSetupViewModel]'s doc comment.
 */
@Composable
fun ProfileSetupScreen(
    userId: String,
    onNavigate: (NavigationEvent) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileSetupViewModel = koinViewModel { parametersOf(userId) },
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    HandleProfileSetupEffects(sideEffect = viewModel.sideEffect, onNavigate = onNavigate)

    ProfileSetupScreenContent(
        state = state,
        onBackClick = viewModel::onBackClicked,
        onNameChange = viewModel::onNameChanged,
        onDateOfBirthChange = viewModel::onDateOfBirthChanged,
        onHeightChange = viewModel::onHeightChanged,
        onHeightUnitChange = viewModel::onHeightUnitToggled,
        onWeightChange = viewModel::onWeightChanged,
        onWeightUnitChange = viewModel::onWeightUnitToggled,
        onMaritalStatusSelected = viewModel::onMaritalStatusSelected,
        onLastPeriodDateChange = viewModel::onLastPeriodStartDateChanged,
        onDontRememberConfirmed = viewModel::onLastPeriodUnknownConfirmed,
        onCycleLengthChange = viewModel::onAverageCycleLengthChanged,
        onPeriodDurationChange = viewModel::onAveragePeriodDurationChanged,
        onCycleRegularitySelected = viewModel::onCycleRegularitySelected,
        onNextClick = viewModel::onNextClicked,
        onSkipClick = viewModel::onSkipClicked,
        modifier = modifier,
    )
}

@Composable
private fun ProfileSetupScreenContent(
    state: ProfileSetupState,
    onBackClick: () -> Unit,
    onNameChange: (String) -> Unit,
    onDateOfBirthChange: (LocalDate) -> Unit,
    onHeightChange: (Int) -> Unit,
    onHeightUnitChange: (HeightUnit) -> Unit,
    onWeightChange: (Int) -> Unit,
    onWeightUnitChange: (WeightUnit) -> Unit,
    onMaritalStatusSelected: (MaritalStatus) -> Unit,
    onLastPeriodDateChange: (LocalDate) -> Unit,
    onDontRememberConfirmed: () -> Unit,
    onCycleLengthChange: (String) -> Unit,
    onPeriodDurationChange: (String) -> Unit,
    onCycleRegularitySelected: (CycleRegularity) -> Unit,
    onNextClick: () -> Unit,
    onSkipClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding(),
    ) {
        CyraCategoryStepHeader(
            stepNumber = state.step.stepNumber,
            totalSteps = ProfileSetupStep.TOTAL_STEPS,
            category = state.category?.let { stringResource(profileSetupMessageKeyToStringRes(it.messageKey)) },
            onBackClick = if (state.showBackButton) onBackClick else null,
            modifier = Modifier.padding(horizontal = 24.dp),
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 24.dp),
        ) {
            when (state.step) {
                ProfileSetupStep.Name -> ProfileSetupNameStep(state.name, state.nameError, onNameChange)
                ProfileSetupStep.Birthday -> ProfileSetupBirthdayStep(state.dateOfBirth, state.dateOfBirthError, onDateOfBirthChange)
                ProfileSetupStep.Height -> ProfileSetupHeightStep(state.heightCm, state.heightUnit, onHeightChange, onHeightUnitChange)
                ProfileSetupStep.Weight -> ProfileSetupWeightStep(state.weightKg, state.weightUnit, onWeightChange, onWeightUnitChange)
                ProfileSetupStep.MaritalStatus -> ProfileSetupMaritalStatusStep(state.maritalStatus, onMaritalStatusSelected)
                ProfileSetupStep.LastPeriod -> ProfileSetupLastPeriodStep(state.lastPeriodStartDate, onLastPeriodDateChange, onDontRememberConfirmed)
                ProfileSetupStep.CycleInfo -> ProfileSetupCycleInfoStep(
                    state.averageCycleLengthDays,
                    state.averagePeriodDurationDays,
                    state.cycleRegularity,
                    onCycleLengthChange,
                    onPeriodDurationChange,
                    onCycleRegularitySelected,
                )
                ProfileSetupStep.AllSet -> ProfileSetupAllSetStep()
            }
        }

        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
            if (state.showDualButtons) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    CyraTextButton(text = stringResource(R.string.profile_setup_button_skip), onClick = onSkipClick)
                    CyraPrimaryButton(text = stringResource(R.string.profile_setup_button_next), onClick = onNextClick)
                }
            } else {
                CyraPrimaryButton(
                    text = stringResource(profileSetupMessageKeyToStringRes(state.primaryButtonLabelKey)),
                    onClick = onNextClick,
                    enabled = state.isPrimaryButtonEnabled && !state.isSubmitting,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}
