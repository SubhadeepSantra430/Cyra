# Cyra (Luna) — Implementation Guide

A running, feature-by-feature record of how this app is being built — what was added, *why*, and the exact steps taken on both Android and iOS. This doc exists because the project owner is learning Kotlin Multiplatform Mobile (KMM) alongside building it, so entries favor explaining the reasoning over terse changelog bullets.

**How this doc works:** every time a new feature or meaningful setup step is implemented, a new dated section gets appended at the bottom, following the template in [How to Add a New Entry](#how-to-add-a-new-entry). Nothing here gets rewritten to look tidier in hindsight — it's a log, not marketing copy.

## Table of Contents

1. [Project Overview](#project-overview)
2. [Feature 1 — Foundational Architecture Setup](#feature-1--foundational-architecture-setup)
3. [Feature 2 — Firebase Project Integration](#feature-2--firebase-project-integration)
4. [Feature 3 — App Icon & Splash Screen](#feature-3--app-icon--splash-screen)
5. [Feature 4 — Design System & Onboarding Carousel](#feature-4--design-system--onboarding-carousel)
6. [How to Add a New Entry](#how-to-add-a-new-entry)

---

## Project Overview

**What Cyra/Luna is:** a privacy-first AI women's-health companion, targeting Android and iOS.

**The core architectural decision, and why everything else follows from it:**

We write **native UI** on each platform — Jetpack Compose on Android, SwiftUI on iOS — but share as much business logic as possible through a Kotlin Multiplatform (KMP) module called `sharedLogic`. That module holds not just models and repositories, but the **ViewModels themselves** (MVVM, shared). Only the UI layer is written twice.

That single decision ("native UI, but ViewModels are shared Kotlin code") is what forces a few other choices that might otherwise look odd:

- **Koin instead of Hilt** for dependency injection. Hilt is Android-only — it has no way to construct a Kotlin ViewModel that iOS also needs to use. Koin has a genuinely multiplatform `viewModel { }` API that works identically on both platforms.
- **Room (KMP)** for the local database, not two separate databases per platform.
- **GitLive's Firebase Kotlin SDK**, a community wrapper that lets the *same* Kotlin code call Firebase Auth/Firestore/Storage on both platforms, instead of writing native Firebase code twice.
- **SKIE**, a compiler plugin that makes Swift able to consume Kotlin's `StateFlow`/`Flow` and sealed classes cleanly (without it, Swift interop with Kotlin coroutines is clunky).

**Module map (as it exists today):**

```
Cyra/
├── androidApp/     — Android app module (Jetpack Compose UI only, no business logic)
├── iosApp/         — Xcode project (SwiftUI UI only, no business logic)
├── sharedLogic/    — KMP module: models, repositories, use cases, ViewModels, Koin, Room, Firebase
├── sharedUI/       — Compose Multiplatform UI (PARKED — not used, see below)
├── webApp/         — Kotlin/Wasm web app (PARKED — not used, see below)
└── assets/branding/ — Canonical source images (app icon etc.) both platforms derive from
```

`sharedUI` and `webApp` are leftovers from the JetBrains project wizard. The product only targets Android and iOS, so these are intentionally left untouched and not developed further — `androidApp` depends directly on `sharedLogic`, not on `sharedUI`.

Inside `sharedLogic`, code is organized as **vertical feature slices**, not horizontal layers:

```
sharedLogic/src/
├── commonMain/kotlin/.../core/presentation/   — BaseViewModel, NavigationEvent (works on ALL targets incl. web)
├── mobileMain/kotlin/.../core/                — di, database, network, firebase, security, datastore
│                                                 (Android + iOS only — Room/Koin/Ktor/Firebase don't
│                                                  publish web targets, so this code can't live in commonMain)
├── androidMain/kotlin/.../core/                — Android-only actuals (FieldCrypto, PlatformModule)
└── iosMain/kotlin/.../core/                    — iOS-only actuals (FieldCrypto, PlatformModule, KoinHelper)
```

Later, each real feature (Auth, Onboarding, Cycle Tracking, etc.) gets its own `feature/<name>/` package with `data`, `domain`, `presentation`, and `di` subfolders — one added at a time, in the app's MVP order.

---

## Feature 1 — Foundational Architecture Setup

**Date:** 2026-08-15
**Goal:** lay down the shared-logic skeleton (DI, database, networking, security, MVVM base classes) before any real feature screen exists, so every feature built afterward drops into an already-decided structure.

### Dependencies added

All versions were checked against the real Maven Central / Google Maven metadata at the time (not guessed), declared in `gradle/libs.versions.toml`:

| Library | Version | Why |
|---|---|---|
| Koin (`koin-core`, `koin-core-viewmodel`, `koin-android`) | 4.2.2 | Multiplatform DI — see "why Koin not Hilt" above |
| Room (`room-runtime`, `room-compiler` via KSP) | 2.8.4 | Offline-first local database, shared schema on both platforms |
| `androidx.sqlite:sqlite-bundled` | 2.7.0 | The actual SQLite driver Room uses under the hood on KMP |
| Ktor (`ktor-client-core`, `-android`, `-darwin`, content-negotiation, logging, auth) | 3.5.2 | Networking — but **only** for calling our own backend (Cloud Functions), never third-party APIs directly |
| GitLive Firebase (`firebase-auth`, `-firestore`, `-storage`) | 2.6.0 | One shared Firebase implementation instead of two native ones |
| `com.google.firebase:firebase-bom` (Android only) | 34.17.0 | GitLive's Android artifacts need the official Firebase BOM to resolve their own transitive Firebase dependencies |
| `multiplatform-settings` (+ coroutines variant) | 1.3.0 | Lightweight key-value storage for app flags (theme, onboarded-flag) — **not** for auth tokens, GitLive owns those |
| `kotlinx-serialization-json` | 1.11.0 | JSON (de)serialization, shared |
| `kotlinx-datetime` | 0.8.0 | Date/time types that work identically on both platforms |
| `kotlinx-coroutines-core` | 1.11.0 | Coroutines/Flow, the backbone of the MVVM state pattern |
| SKIE (Gradle plugin) | 0.10.14 | Makes Swift able to consume Kotlin `Flow`/sealed classes cleanly |
| KSP (Kotlin Symbol Processing, for Room's compiler) | 2.3.11 | Room's annotation processor needs this |

### What was built

- **`core/presentation/BaseViewModel.kt`** — every feature ViewModel extends this. It exposes a `StateFlow<UiState>` (what the screen renders) and a `SharedFlow<SideEffect>` (one-shot events like navigation/snackbars). Built on the *multiplatform* `androidx.lifecycle.ViewModel`, so `viewModelScope` works the same way on both platforms.
- **`core/presentation/NavigationEvent.kt`** — a shared vocabulary ViewModels use to *ask* for navigation, without ever holding a `NavController`/`NavigationStack` themselves. Navigation itself stays 100% native per platform by design — only the "what should happen" event is shared.
- **`core/di/KoinInit.kt`** — the single `initKoin(platformModule)` entry point both platforms call at startup. New features get added here as one line each (`authModule`, `cycleModule`, etc.) once they exist.
- **`core/database/AppDatabase.kt`** — the one physical Room database, with `SyncMetadataEntity` as its first table (infrastructure for tracking offline-sync state per feature, ready for whichever feature needs Firestore sync first).
- **`core/network/HttpClientFactory.kt`** — the Ktor client, deliberately built with a bearer-auth hook to attach the Firebase ID token, and nothing else — it's not meant to be a general-purpose HTTP client for arbitrary APIs.
- **`core/firebase/FirebaseClients.kt`** — thin named accessors over GitLive's `Firebase.auth`/`.firestore`/`.storage`, so the rest of the app never imports GitLive directly.
- **`core/security/FieldCrypto.kt`** — an `expect`/`actual` for encrypting sensitive free-text fields (journal notes, chat transcripts) before they hit the database. **Android is implemented** (real AES-256-GCM via Android Keystore). **iOS is deliberately left unimplemented** (throws `NotImplementedError` with a TODO) rather than shipping unverified crypto — implementing it needs a small Keychain/CryptoKit bridge that hasn't been built yet.
- **`core/security/AppLockState.kt`** — shared "is the app locked" state for the future biometric-lock feature; the actual biometric *prompt* will stay native per platform, but both platforms will report success back through the same shared repository.

### Platform wiring

- **Android**: `CyraApplication.kt` (new) calls `initKoin()` in `onCreate()`; registered in `AndroidManifest.xml` via `android:name=".CyraApplication"`.
- **iOS**: `KoinHelper` (in `iosMain`) exposes `doInitKoin()`, called once from `iOSApp.swift`'s `init()`.

### Things learned along the way (worth knowing if you touch this again)

- **KSP's versioning decoupled from Kotlin's version** starting at KSP 2.3.0 — it's no longer `{kotlinVersion}-{kspPatch}`, just a plain version like `2.3.11`.
- **Room needs one extra annotation for non-Android KMP targets**: `@ConstructedBy(AppDatabaseConstructor::class)` on the `@Database` class, plus an `expect object AppDatabaseConstructor`. Room's own KSP compiler generates the `actual` for iOS automatically — don't hand-write it.
- **The Android Gradle plugin used here (`com.android.kotlin.multiplatform.library`) has a quirk**: a custom Kotlin source-set hierarchy group (used to share Koin/Room/Ktor code between Android and iOS without forcing it onto the parked web targets) doesn't automatically wire in for this specific plugin's Android target. It needed an explicit extra `dependsOn()` call after the hierarchy template — documented with a code comment in `sharedLogic/build.gradle.kts` in case it needs revisiting.
- **Ktor 3.2.0 has a real bug** (fixed in later versions) that breaks Android's dex step with a cryptic "Space characters in SimpleName" error — this is why we're pinned to 3.5.2, not something older.

---

## Feature 2 — Firebase Project Integration

**Date:** 2026-08-15 to 2026-08-16
**Goal:** connect the app to a real Firebase project on both platforms.

### Step 1 — Registering the apps in the Firebase Console

In an *existing* Firebase project (console.firebase.google.com → your project → ⚙ Project Settings → Your apps → Add app):

- **Android app**: registered with package name `subha.app.cyra` (must match `applicationId` in `androidApp/build.gradle.kts` exactly). SHA-1 signing certificate was skipped for now — only needed later for Google Sign-In / Phone Auth.
- **iOS app**: registered with bundle ID `subha.app.cyra.Cyra` (must match `PRODUCT_BUNDLE_IDENTIFIER` in `iosApp/Configuration/Config.xcconfig`).

> **Bug fixed along the way:** `Config.xcconfig` originally had `PRODUCT_BUNDLE_IDENTIFIER=subha.app.cyra.Cyra$(TEAM_ID)` — accidentally appending the empty `TEAM_ID` variable straight onto the bundle ID. Harmless while `TEAM_ID` is blank, but would have silently corrupted the bundle ID the moment a real Apple signing team was set. Fixed to just `subha.app.cyra.Cyra`.

### Step 2 — Downloading and placing the config files

- **Android**: downloaded `google-services.json`, placed at exactly:
  ```
  androidApp/google-services.json
  ```
- **iOS**: downloaded `GoogleService-Info.plist`, placed at exactly:
  ```
  iosApp/iosApp/GoogleService-Info.plist
  ```
  (Xcode auto-detects this — the project uses Xcode's folder-sync feature, so no manual "Add Files to Project" step was needed.)

### Step 3 — Android Gradle wiring

Added the `google-services` Gradle plugin (v4.5.0):
- `build.gradle.kts` (root): `alias(libs.plugins.googleServices) apply false`
- `androidApp/build.gradle.kts`: `alias(libs.plugins.googleServices)`

This plugin reads `google-services.json` at build time and generates the resources GitLive's Auth SDK needs. **The Android build fails outright if this file is missing** — that's expected and by design (a loud failure beats a silent misconfiguration).

### Step 4 — iOS: the CocoaPods detour (the hard part)

This took several rounds to get right, so it's worth understanding *why* it's this complicated:

GitLive's Firebase SDK, on iOS, is a **Kotlin wrapper around Apple's own native Firebase iOS SDK** — it doesn't bundle those Objective-C frameworks (`FirebaseAuth`, `FirebaseFirestore`, etc.) itself. Without extra setup, the app compiles fine but **fails to link**, with errors like `Undefined symbol: _OBJC_CLASS_$_FIRAuth`.

The version of GitLive we're on (2.6.0, the latest *stable* release — 3.0.0 exists but is still alpha) expects those native frameworks to be supplied via **CocoaPods**. Steps taken:

1. **Installed CocoaPods**: `brew install cocoapods` (wasn't present on the machine before).
2. **Enabled the Kotlin/Native CocoaPods Gradle plugin** in `sharedLogic/build.gradle.kts`:
   - Applied as `id("org.jetbrains.kotlin.native.cocoapods")` with **no version** — it ships bundled inside the `kotlin-multiplatform` plugin itself; giving it an explicit version in the `plugins {}` block causes a "already on the classpath" error.
   - Added a `cocoapods { }` block declaring the exact native pods GitLive needs: `FirebaseCore`, `FirebaseAuth`, `FirebaseFirestoreInternal`, `FirebaseFirestore`, `FirebaseStorage` — all pinned to **Firebase iOS SDK 11.8.0** (the exact version GitLive 2.6.0 itself was built and tested against — confirmed by checking GitLive's own `gradle/libs.versions.toml` at that release tag on GitHub, not guessed).
3. **Created `iosApp/Podfile`**:
   ```ruby
   target 'iosApp' do
     use_frameworks! :linkage => :static
     platform :ios, '13.0'
     pod 'sharedLogic', :path => '../sharedLogic'
   end

   post_install do |installer|
     installer.pods_project.targets.each do |target|
       target.build_configurations.each do |config|
         config.build_settings['IPHONEOS_DEPLOYMENT_TARGET'] = '13.0'
       end
     end
   end
   ```
   Two details that matter here:
   - **`:linkage => :static` is required, not optional.** Firestore pulls in gRPC (via `gRPC-Core`/`gRPC-C++`), and gRPC's CocoaPods packaging doesn't work with plain dynamic `use_frameworks!` — it fails with a missing-modulemap error. Static linkage is Google's own documented requirement for using Firestore through CocoaPods.
   - **The `post_install` hook** exists because CocoaPods otherwise defaults every pod's own deployment target to iOS 9.0 regardless of our app's actual target, which triggers an Xcode warning (9.0 is below what current SDKs even support).
4. **Removed the old, no-longer-needed "Compile Kotlin Framework" build step** from `iosApp.xcodeproj` (it used to run `./gradlew :sharedLogic:embedAndSignAppleFrameworkForXcode` directly). CocoaPods now builds `sharedLogic` itself, as part of building the `Pods` project — the two mechanisms would otherwise fight over producing the same framework.
5. Ran `pod install` from inside `iosApp/` — this downloaded ~20 pods (Firebase + gRPC + their dependencies) and generated **`iosApp.xcworkspace`**.

### The recurring gotcha: workspace vs. project

Once CocoaPods is involved, **`iosApp.xcworkspace` must be opened instead of `iosApp.xcodeproj`, always, from now on.** This tripped us up repeatedly:

- Opening the plain `.xcodeproj` skips the `Pods` sub-project entirely (which is what actually builds `FirebaseAuth.framework`, `FirebaseAppCheckInterop.framework`, etc.), causing "Framework 'X' not found" linker errors even though everything was configured correctly.
- **Tell**: in Xcode's left sidebar, the workspace shows **two** top-level entries — `iosApp` *and* `Pods`. If you only see `iosApp`, you're in the wrong file.
- A quick fix if this happens again: Product → Clean Build Folder, then explicitly reopen `iosApp.xcworkspace`.

### Other environment gotchas hit along the way

- **`xcode-select` must point at full Xcode**, not just the Command Line Tools (`xcode-select -p` should print `/Applications/Xcode.app/Contents/Developer`). If it doesn't, the CocoaPods build script that compiles `sharedLogic` for real (instead of using an empty placeholder framework) fails silently, and you get confusing "symbol not found" errors for things that are actually implemented in the code. Fix: `sudo xcode-select -s /Applications/Xcode.app/Contents/Developer`.
- **Android Studio's own "Run" button for iOS uses a different build-cache location** than Xcode's own default, and was observed to *not* pick up the CocoaPods-built frameworks correctly. Decision made: build/run iOS directly from Xcode from now on, not from Android Studio's iOS launcher.
- A **name collision** between the AndroidX `SplashScreen` class and a custom composable also named `SplashScreen` caused a confusing "unresolved reference" error later on (see Feature 3) — same category of "two things with the same name, compiler silently picks the wrong one" issue worth watching for generally.

---

## Feature 3 — App Icon & Splash Screen

**Date:** 2026-08-16
**Goal:** replace the placeholder launcher icon with the real app icon (the purple moon/flower mark) on both platforms, and add a proper splash screen.

### Source image

The master icon (1024×1024 PNG, no transparency) is kept at:
```
assets/branding/cyra_app_icon_1024.png
```
This is the single source of truth — every platform-specific icon/splash asset below was generated *from* this file (using macOS's built-in `sips` image tool), so if the logo ever changes, it only needs to be replaced in one place and regenerated.

### Android — app icon

Android needs several things for a proper adaptive icon:
- **Legacy launcher icons** (`ic_launcher.png` / `ic_launcher_round.png`) at 5 densities: mdpi (48px) → xxxhdpi (192px) — direct resizes of the source image.
- **Adaptive icon foreground** (`ic_launcher_foreground.png`, also at 5 densities) — the logo scaled down to ~66% and padded with white, because Android's adaptive-icon system only guarantees the center ~61% of the icon is visible once a launcher applies its own mask shape (circle, squircle, etc.) — anything near the edges can get clipped.
- **Adaptive icon background**: a plain white color (`@color/ic_launcher_background`), matching the source image's own white background so there's no visible seam.
- The old placeholder vector-drawable icon (`ic_launcher_background.xml`, `ic_launcher_foreground.xml`) was deleted, and `mipmap-anydpi-v26/ic_launcher.xml` / `ic_launcher_round.xml` were updated to point at the new files.

### iOS — app icon

Much simpler on iOS: modern Xcode (16+) supports a **single 1024×1024 image** for the whole App Icon set (no more manually generating a dozen sizes). The same source PNG was copied into:
```
iosApp/iosApp/Assets.xcassets/AppIcon.appiconset/AppIcon-1024.png
```
and `Contents.json` updated to reference it for all three appearance variants (`any`, `dark`, `tinted`) — reusing the same image for all three is a reasonable placeholder; a dedicated grayscale variant for "tinted" mode would look nicer later, but isn't required.

### Splash screen — the "two-layer" approach, and why

Neither Android's nor iOS's native splash-screen mechanism supports an arbitrary full-bleed custom image — both are deliberately restricted to "a centered icon on a solid background color," by design (it's meant to be instant and simple, shown before the app has even started). Since the ask was for a genuine **full-screen** splash, the solution on both platforms is two layers stacked back to back:

1. **The system-level splash** (satisfies "use the splash API," shows instantly at cold start, before any app code runs).
2. **A custom full-screen view**, shown immediately after, for ~1.2 seconds, with full control over layout/animation — this is what actually delivers the full-screen look.

**Android:**
- Added `androidx.core:core-splashscreen` (v1.2.0).
- `values/themes.xml` — new `Theme.App.Starting` style extending `Theme.SplashScreen`, referencing the adaptive-icon foreground as the splash icon and the white background color.
- `AndroidManifest.xml` — `<application>` now uses `android:theme="@style/Theme.App.Starting"`.
- `MainActivity.kt` — calls `installSplashScreen()` (imported from `androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen` — it's a "member extension function," so it's called bare as `installSplashScreen()`, *not* as `SplashScreen.installSplashScreen(this)`) before `super.onCreate()`.
- A new composable, **`CyraSplashScreen`** (in `ui/splash/SplashScreen.kt`) — deliberately *not* named `SplashScreen`, because that collided with the AndroidX class of the same name and caused a confusing compile error.
- `CyraRoot()` in `MainActivity.kt` shows `CyraSplashScreen()` for 1.2 seconds (via a coroutine `delay`), then crossfades to the real app content. Once real auth/navigation exists, this timed delay will be replaced by "stay on splash until the auth-state check completes."

**iOS:**
- `Info.plist` — added a `UILaunchScreen` dictionary (the modern, storyboard-free way to declare a launch screen) pointing at a new `SplashLogo` image set.
- `Assets.xcassets/SplashLogo.imageset` — the logo at 1x/2x/3x (200px/400px/600px), generated from the same source PNG.
- **`CyraSplashView.swift`** (new) — a SwiftUI view: white background, logo centered at 180pt.
- `iOSApp.swift` — `CyraRootView` shows `CyraSplashView()` for 1.2 seconds (via `Task.sleep`), then crossfades to `ContentView()`. Mirrors the Android side exactly.

---

## Feature 4 — Design System & Onboarding Carousel

**Date:** 2026-08-16
**Goal:** establish a shared design system (colors + typography, locked against the device's accessibility font-scale setting) and a reusable component library once, before building any more screens — then build the first real screen on top of it: the swipeable post-splash intro carousel.

### The color palette

The user supplied a palette called "Serene Radiance": Primary `#8B5CF6`, Secondary `#C4B5FD`, Tertiary `#1F2937`, Neutral `#F9FAFB`. Worth knowing: these are an **exact match for Tailwind CSS's stock `violet` and `gray` ramps** (`violet-500`, `violet-300`, `gray-800`, `gray-50`) — recognizing that meant the rest of the color system (container colors, muted text, outlines) could be filled in from the same well-known ramp instead of guessing. An `error` color wasn't specified, so a conventional red (`#EF4444`, Tailwind `red-500`) was used as an easy-to-swap placeholder.

Both platforms define the exact same hex values independently (Android in `ui/theme/Color.kt` as Compose `Color` values feeding a `lightColorScheme()`; iOS in `Theme/Color+Cyra.swift` as a `Color` extension) — kept as plain code on both sides (not an Android XML color resource or an iOS Asset Catalog `ColorSet`) so the two are trivially diffable against each other.

### The font, and the "never resize" requirement

Font: **Manrope**, 7 static weights (ExtraLight → ExtraBold), supplied by the user as TTF files. These are checked into the repo at `assets/fonts/manrope/` (matching the existing `assets/branding/` convention of one canonical source both platforms copy from), then copied into:
- Android: `res/font/manrope_*.ttf` (lowercase, Android's resource-naming rule), wired into a Compose `FontFamily` in `ui/theme/Typography.kt`.
- iOS: `Fonts/Manrope-*.ttf`, registered in `Info.plist` under a new `UIAppFonts` array (**required** — without this, `Font.custom()` silently falls back to the system font instead of erroring, which is an easy mistake to miss).

**The critical requirement was that text must never change size when the user changes their device's system font-size/accessibility setting** — a real, correctly-implemented restriction, not just "big text everywhere":
- **Android**: Compose's `sp` unit scales with the system's `fontScale` by default (that's normally *correct* accessibility behavior — this app deliberately opts out of it). The fix lives in exactly one place, `ui/theme/Theme.kt`'s `CyraTheme()`: override `LocalDensity` to force `fontScale = 1f`, while keeping the real screen `density` untouched (so `dp` sizing for layout is unaffected — only text size is pinned). Every screen goes through `CyraTheme`, so no screen can get this wrong.
- **iOS**: every font in `Theme/Font+Cyra.swift` uses `Font.custom(_:fixedSize:)` — specifically the `fixedSize:` variant, not `size:`. Apple documents `fixedSize:` as immune to Dynamic Type at every accessibility level (including the "Larger Accessibility Sizes" range), which is a stronger guarantee than just avoiding `relativeTo:`. `.dynamicTypeSize(.large)` is also applied once at the app root (`cyraThemed()` in `Theme/CyraTheme.swift`) as a second layer of protection.

### Reusable components

Per the requirement to never re-implement the same button/control per screen:
- **Android** (`ui/components/`): `CyraPrimaryButton` (pill-shaped, filled, trailing chevron — the chevron is hand-drawn with a `Canvas`/`Path` rather than pulling in an icon-font dependency for one glyph), `CyraSkipButton` (plain text button), `PageIndicatorDots` (a real custom composable — Compose has no built-in page-dots component, unlike SwiftUI).
- **iOS** (`Components/`): `CyraPrimaryButtonStyle` and `CyraSkipButtonStyle` — `ButtonStyle`s applied to plain SwiftUI `Button`, not fully custom views, since native `Button` already does the job (per the "use SwiftUI native where it fits" preference). The page dots use SwiftUI's own capability too, hand-styled to match the reference's exact position (between the text block and the buttons) rather than `TabView`'s built-in `.indexDisplayMode(.always)`, which places dots differently.

Every new composable/screen is annotated with a custom **`@CyraPreviews`** annotation (`ui/components/CyraPreviews.kt`) instead of a bare `@Preview` — it stacks AndroidX's `@PreviewScreenSizes` (phone/foldable/tablet spread) and `@PreviewFontScale` (multiple accessibility scale variants). The font-scale variants doubling as a built-in visual check: since `CyraTheme` locks `fontScale = 1f`, those preview variants should all render with identical text size — if they ever don't, the lock is broken. On iOS, multiple `#Preview` blocks per file cover a representative device spread (iPhone SE, iPhone 17, iPhone 17 Pro Max).

### The onboarding carousel screen

The user provided an exact reference design (a flattened mockup screenshot, not an editable Figma file) showing 3 swipeable pages. Copy was transcribed directly from it, not invented:

| Page | Title | Description |
|---|---|---|
| 1 | Your Health. Your Way. | Track your cycle, understand your body, and get AI-powered insights for a healthier you. |
| 2 | Personalized. Private. Powerful. | Your data is safe with us. Get personalized wellness guidance and predictions that adapt to you. |
| 3 | Understand. Improve. Thrive. | Discover patterns, track progress, and build better habits with insights that help you feel your best every day. |

**On the illustrations**: there's no image-generation tool available, and the reference was a flat PNG (not an editable Figma source), so new matching artwork couldn't be generated. Instead, the 3 illustrations (woman-with-phone-and-flowers, each with its own page-relevant context card — a calendar for cycle tracking, a lock for privacy, a progress chart for insights) were **cropped directly out of the reference screenshot** using Python/Pillow, saved to `assets/branding/onboarding_illustration_{1,2,3}.png`, then copied to Android (`res/drawable-nodpi/`) and iOS (`Assets.xcassets/OnboardingIllustration{1,2,3}.imageset/`, as single-scale image sets since there's only one source resolution). Worth knowing: because these came from a screenshot rather than a vector/high-res export, they may look slightly soft on the largest, highest-density phones — swap them for real exports once a proper design source exists.

**Mechanism**: Android uses `HorizontalPager` (Compose Foundation, no new dependency needed) with `rememberPagerState`; iOS uses `TabView` with `.tabViewStyle(.page(indexDisplayMode: .never))` (dots are custom-positioned, not the built-in ones — see above). Both: Skip button (hidden on the last page), a primary button that reads "Next" and becomes "Get Started" on the last page, and the dot indicator between the text and the buttons.

### Strings — no hardcoding

Android: all copy lives in `values/strings.xml` (`onboarding_skip`, `onboarding_next`, `onboarding_get_started`, `onboarding_page{1,2,3}_{title,description}`). iOS: a `Localizable.xcstrings` **String Catalog** (the modern Xcode 15+ format — a single JSON file, no per-locale duplication like the legacy `Localizable.strings`) was hand-authored with the same keys; `String(localized:)` picks it up automatically at build time, no extra wiring needed.

### App flow wiring

Both platforms follow the same shape: splash → onboarding → placeholder home (soon to be real auth/home). Android: `MainActivity.kt`'s `CyraRoot()` now wraps everything in `CyraTheme { }`; a new `CyraAppFlow()` holds local onboarding-complete state and shows `OnboardingScreen` until `onFinished()` fires, then falls through to the renamed `PlaceholderHomeScreen()`. iOS: `iOSApp.swift`'s `CyraRootView` gained the identical state machine. Both leave a `// TODO(Auth feature)` comment where real navigation will eventually replace the placeholder — intentionally minimal since there's no real auth/home yet.

### Things learned along the way

- Stacking `@PreviewScreenSizes` + `@PreviewFontScale` on one annotation gives the *union* of each annotation's variants, not every combination of device × font-scale — fine for what's needed here, just worth knowing so the preview count isn't surprising.
- A `Modifier.weight(1f)` call failed with a confusing "internal in file" compiler error when an explicit `import androidx.compose.foundation.layout.weight` was present — `weight` is a `ColumnScope`/`RowScope` member extension that's automatically in scope inside a `Column`/`Row` lambda and should **never** be imported directly; removing the import fixed it.
- `by animateDpAsState(...)` (or any `by remember`-style delegate) needs `import androidx.compose.runtime.getValue` in scope, even though nothing in the code visibly calls `getValue` — easy to forget since the delegate syntax hides it.
- On iOS, `Color(red: 0x8B / 255, ...)` is a real bug trap: `0x8B / 255` is **integer division** in Swift and evaluates to `0` unless one side is explicitly a `Double` first (`Double(0x8B) / 255`).

### Follow-up fixes (same day)

A few things needed correcting after a first look at the running app:

- **Fonts were physically duplicated in 3 places** (`assets/fonts/manrope/` at the repo root, plus a copy inside `androidApp/res/font/`, plus a copy inside `iosApp/Fonts/`) — worth understanding the distinction: the repo-root copy **never shipped in either app** (nothing referenced it at build time), so it wasn't making either app bigger, just repo clutter. The two platform copies genuinely can't be reduced to one physical file, though — Android and iOS are separate compiled binaries that can't share files at runtime, so each needs its own copy inside its own build output; this is true of every cross-platform framework, not something specific to this project. Removed the redundant repo-root copy; the original source files remain in the user's own `font/Manrope/static/` folder outside the repo if ever needed again.
- **Page indicator dots weren't vertically centered** relative to each other — the selected dot is larger (10dp) than the unselected ones (8dp), and the `Row` holding them had no `verticalAlignment`, so it defaulted to top-aligning, making the bigger dot look offset. Fixed with `verticalAlignment = Alignment.CenterVertically`.
- **The primary button's width behavior was backwards between platforms**: the intent is a compact pill button (next to Skip) on normal pages, and a full-width button for the final "Get Started" CTA. Android had it compact on *every* page (never went full-width); iOS's `CyraPrimaryButtonStyle` had `frame(maxWidth: .infinity)` baked in, so it was full-width on *every* page (including next to Skip, where it shouldn't be). Fixed by removing the forced full-width from the iOS button style entirely (a reusable style shouldn't dictate its own width) and instead applying `.fillMaxWidth()` / `.frame(maxWidth: .infinity)` only at the call site, only on the last page, on both platforms — the reusable button component itself stays width-agnostic on both sides now, which is the more correct design for a shared component anyway.

## How to Add a New Entry

When a new feature or setup step is finished, append a new `## Feature N — <Name>` section at the bottom (just above this one, or replacing this template section's position — keep "How to Add a New Entry" as the last section), following this shape:

```markdown
## Feature N — <Short Name>

**Date:** <YYYY-MM-DD>
**Goal:** <one or two sentences on what this feature is and why it's being built now>

### Dependencies added
(table or list: library, version, why — only if new dependencies were added)

### What was built
(the actual files/classes/screens created, explained simply — assume the reader is learning KMM)

### Platform wiring
(anything Android-specific and iOS-specific that had to be done separately)

### Things learned along the way
(any gotchas, bugs, or non-obvious fixes worth remembering — this is the most valuable part for a learner)
```

Keep explanations at the "explain like I'm learning this" level — favor clarity over brevity. Update the Table of Contents at the top when a new section is added.
