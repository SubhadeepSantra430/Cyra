import Foundation

struct OnboardingPage: Identifiable {
    let id: Int
    let imageName: String
    let titleKey: String
    let descriptionKey: String
}

/// The illustrations already include their own page-specific context card (calendar/
/// fertile-window, lock/privacy, progress chart) baked into the artwork - cropped
/// directly from the reference design rather than rebuilt as separate overlay UI,
/// since reconstructing a mini calendar/chart widget in code for a decorative
/// background element isn't worth the effort versus using the real exported art.
let onboardingPages: [OnboardingPage] = [
    OnboardingPage(
        id: 0,
        imageName: "OnboardingIllustration1",
        titleKey: "onboarding_page1_title",
        descriptionKey: "onboarding_page1_description",
    ),
    OnboardingPage(
        id: 1,
        imageName: "OnboardingIllustration2",
        titleKey: "onboarding_page2_title",
        descriptionKey: "onboarding_page2_description",
    ),
    OnboardingPage(
        id: 2,
        imageName: "OnboardingIllustration3",
        titleKey: "onboarding_page3_title",
        descriptionKey: "onboarding_page3_description",
    ),
]
