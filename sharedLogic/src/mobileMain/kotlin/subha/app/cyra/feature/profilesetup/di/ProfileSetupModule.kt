package subha.app.cyra.feature.profilesetup.di

import org.koin.core.module.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import org.koin.mp.KoinPlatform
import subha.app.cyra.feature.profilesetup.data.ProfileRepository
import subha.app.cyra.feature.profilesetup.presentation.ProfileSetupViewModel

/**
 * [ProfileSetupViewModel] needs the just-created user's id at construction (see that
 * class's doc comment), so it's registered as a parameterized `viewModel { }` rather
 * than the zero-arg style `authModule` uses - resolved with `parametersOf(userId)`.
 */
val profileSetupModule = module {
    single { ProfileRepository() }
    viewModel { (userId: String) -> ProfileSetupViewModel(userId, get()) }
}

// Swift can't call reified generic `get<T>()` (see KoinHelper.kt) or pass Koin
// parameters directly, so this is the one callable entry point from
// ProfileSetupView.swift - mirrors provideLoginViewModel() etc. in AuthModule.kt.
fun provideProfileSetupViewModel(userId: String): ProfileSetupViewModel =
    KoinPlatform.getKoin().get { parametersOf(userId) }
