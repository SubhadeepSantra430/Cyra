import Foundation

/// The 3 screens the Auth flow swaps between - mirrors Android's `AuthDestination.kt`.
enum AuthDestination {
    case login
    case signup
    case forgotPassword
}
