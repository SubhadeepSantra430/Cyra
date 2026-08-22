import FirebaseCore
import GoogleSignIn
import UIKit

/// Placeholder value in `GoogleService-Info.plist`'s absent `CLIENT_ID` case - Google
/// Sign-In isn't enabled for this app in Firebase console yet, so `FirebaseApp.app()?
/// .options.clientID` is nil until then. Mirrors Android's `GOOGLE_WEB_CLIENT_ID_PLACEHOLDER`.
enum GoogleSignInError: Error {
    case missingClientID
    case missingIdToken
}

/// iOS's half of Google Sign-In - `GIDSignIn` yields both an ID token and an access
/// token (unlike Android's Credential Manager flow, which only yields an ID token).
/// Guards on a nil client ID instead of force-unwrapping, so a not-yet-configured
/// Firebase project fails gracefully rather than crashing.
enum GoogleSignInHelper {
    static func signIn(presenting viewController: UIViewController) async throws -> (idToken: String, accessToken: String?) {
        guard let clientID = FirebaseApp.app()?.options.clientID else {
            throw GoogleSignInError.missingClientID
        }
        GIDSignIn.sharedInstance.configuration = GIDConfiguration(clientID: clientID)

        let result = try await GIDSignIn.sharedInstance.signIn(withPresenting: viewController)
        guard let idToken = result.user.idToken?.tokenString else {
            throw GoogleSignInError.missingIdToken
        }
        return (idToken, result.user.accessToken.tokenString)
    }
}

extension UIApplication {
    /// The active window's root view controller - needed to present both `GIDSignIn`'s
    /// UI and (via `AppleSignInHelper`'s own `presentationAnchor`) Apple's sign-in sheet.
    var currentViewController: UIViewController? {
        connectedScenes
            .compactMap { ($0 as? UIWindowScene)?.keyWindow }
            .first?.rootViewController
    }
}
