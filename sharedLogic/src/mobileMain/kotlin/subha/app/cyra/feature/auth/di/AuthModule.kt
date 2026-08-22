package subha.app.cyra.feature.auth.di

import org.koin.core.module.dsl.viewModel
import org.koin.mp.KoinPlatform
import org.koin.dsl.module
import subha.app.cyra.feature.auth.data.AuthRepository
import subha.app.cyra.feature.auth.presentation.ForgotPasswordViewModel
import subha.app.cyra.feature.auth.presentation.LoginViewModel
import subha.app.cyra.feature.auth.presentation.SignupViewModel

/**
 * Once a feature actually calls the shared Ktor client, replace `coreModule`'s
 * `single { createHttpClient() }` with
 * `single { createHttpClient(tokenProvider = { FirebaseClients.currentIdToken() }) }` -
 * Auth is what makes that tokenProvider meaningful, but nothing calls the client yet, so
 * that override is left as a follow-up rather than made here unverified.
 */
val authModule = module {
    single { AuthRepository() }
    viewModel { LoginViewModel(get()) }
    viewModel { SignupViewModel(get()) }
    viewModel { ForgotPasswordViewModel(get()) }
}

// Swift can't call reified generic `get<T>()` (see KoinHelper.kt) - each ViewModel gets
// its own concrete, non-generic accessor so `provideLoginViewModel()` etc. are directly
// callable from LoginView.swift/SignupView.swift/ForgotPasswordView.swift.
fun provideLoginViewModel(): LoginViewModel = KoinPlatform.getKoin().get()
fun provideSignupViewModel(): SignupViewModel = KoinPlatform.getKoin().get()
fun provideForgotPasswordViewModel(): ForgotPasswordViewModel = KoinPlatform.getKoin().get()
