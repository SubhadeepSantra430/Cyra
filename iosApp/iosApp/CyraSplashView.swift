import SwiftUI

/// The full-screen branded splash, shown immediately after iOS's own Launch Screen
/// (see `UILaunchScreen` in Info.plist) hands off to the app's first SwiftUI frame.
/// The Launch Screen mechanism only supports a centered image on a background color,
/// not a full-bleed custom layout - this is what actually delivers the full-screen look
/// (mirrors `CyraSplashScreen`/`CyraRoot` on Android).
struct CyraSplashView: View {
    var body: some View {
        ZStack {
            Color.white.ignoresSafeArea()
            Image("SplashLogo")
                .resizable()
                .scaledToFit()
                .frame(width: 180, height: 180)
        }
    }
}

#Preview {
    CyraSplashView()
}
