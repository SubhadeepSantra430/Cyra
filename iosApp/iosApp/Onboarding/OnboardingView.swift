import SwiftUI

/// The swipeable post-splash intro carousel - 3 pages (illustration + title +
/// description), Skip + Next/Get Started buttons, dot page indicator. Shown once,
/// between `CyraSplashView` and the (future) auth/home flow - see `CyraRootView` in
/// iOSApp.swift for where this plugs into the app's overall flow. Mirrors Android's
/// `OnboardingScreen`.
struct OnboardingView: View {
    let onFinished: () -> Void

    @State private var currentPage = 0

    private var isLastPage: Bool { currentPage == onboardingPages.count - 1 }

    var body: some View {
        VStack(spacing: 0) {
            TabView(selection: $currentPage) {
                ForEach(onboardingPages) { page in
                    OnboardingPageContent(page: page)
                        .tag(page.id)
                }
            }
            .tabViewStyle(.page(indexDisplayMode: .never))

            // Native page dots (per "use SwiftUI native where it fits") - not shown via
            // .indexDisplayMode(.always) above because that positions dots edge-to-edge;
            // this custom-positioned copy sits exactly where the reference design has it,
            // between the text block and the buttons.
            HStack(spacing: 8) {
                ForEach(onboardingPages) { page in
                    Circle()
                        .fill(page.id == currentPage ? Color.cyraPrimary : Color.cyraOutline)
                        .frame(width: page.id == currentPage ? 10 : 8, height: page.id == currentPage ? 10 : 8)
                        .animation(.easeInOut(duration: 0.2), value: currentPage)
                }
            }
            .padding(.bottom, 24)

            if isLastPage {
                // No Skip needed on the last page - the primary button takes the full
                // row width instead of sharing space with Skip, for emphasis on the
                // final CTA. Mirrors Android's OnboardingScreen.
                Button(String(localized: "onboarding_get_started")) { onFinished() }
                    .buttonStyle(CyraPrimaryButtonStyle())
                    .frame(maxWidth: .infinity)
                    .padding(.horizontal, 24)
                    .padding(.vertical, 16)
            } else {
                HStack {
                    Button(String(localized: "onboarding_skip")) { onFinished() }
                        .buttonStyle(CyraSkipButtonStyle())

                    Spacer()

                    Button(String(localized: "onboarding_next")) {
                        withAnimation { currentPage += 1 }
                    }
                    .buttonStyle(CyraPrimaryButtonStyle())
                }
                .padding(.horizontal, 24)
                .padding(.vertical, 16)
            }
        }
    }
}

private struct OnboardingPageContent: View {
    let page: OnboardingPage

    var body: some View {
        VStack(spacing: 0) {
            Image(page.imageName)
                .resizable()
                .scaledToFit()
                .padding(.top, 24)
                .padding(.horizontal, 24)

            Text(String(localized: String.LocalizationValue(page.titleKey)))
                .font(CyraFont.headlineMedium())
                .foregroundColor(.cyraOnSurface)
                .multilineTextAlignment(.center)
                .padding(.top, 24)
                .padding(.horizontal, 24)

            Text(String(localized: String.LocalizationValue(page.descriptionKey)))
                .font(CyraFont.bodyLarge())
                .foregroundColor(.cyraOnSurfaceVariant)
                .multilineTextAlignment(.center)
                .padding(.top, 12)
                .padding(.horizontal, 24)
        }
    }
}

#Preview("iPhone SE") {
    OnboardingView(onFinished: {})
}

#Preview("iPhone 17") {
    OnboardingView(onFinished: {})
}

#Preview("iPhone 17 Pro Max") {
    OnboardingView(onFinished: {})
}
