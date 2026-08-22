import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.kotlinSerialization)
    // Bundled with the kotlin-multiplatform plugin itself (same artifact) - applied
    // with no version (an explicit one via a catalog alias fails: "plugin is already
    // on the classpath with an unknown version"). Needs to go through `plugins {}`
    // (not `apply(plugin = ...)`) so the typed `cocoapods {}` DSL is available below.
    id("org.jetbrains.kotlin.native.cocoapods")
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    alias(libs.plugins.skie)
}

kotlin {
    iosArm64()
    iosSimulatorArm64()

    // GitLive's Firebase KMP SDK (2.6.0, pre-3.0/SwiftPM) is a Kotlin wrapper around the
    // native Firebase iOS SDK - it does NOT bundle those Objective-C frameworks. Without
    // this block, the app links but the linker can't find FIRAuth/FIRFirestore/etc.
    // CocoaPods is GitLive 2.6.0's documented, supported way to pull those frameworks in
    // (their own build declares these exact pod names/versions - see the comment on the
    // `firebase-cocoapods` version in libs.versions.toml). `framework {}` here REPLACES
    // the plain `.binaries.framework {}` config the JetBrains wizard generated - the
    // cocoapods plugin owns framework config for every ios target once applied.
    cocoapods {
        version = "1.0"
        summary = "Cyra shared business logic"
        homepage = "https://github.com/subha/Cyra"
        ios.deploymentTarget = libs.versions.firebase.iosDeploymentTarget.get()
        // Points at the real iosApp Xcode project's own Podfile (not a synthetic one) -
        // `pod install` there pulls in this module (as a generated local podspec) plus
        // its transitive Firebase pod dependencies declared below.
        podfile = project.file("../iosApp/Podfile")

        framework {
            baseName = "SharedLogic"
            isStatic = true
        }

        pod("FirebaseCore") {
            version = libs.versions.firebase.cocoapods.get()
            extraOpts += listOf("-compiler-option", "-fmodules")
        }
        pod("FirebaseAuth") {
            version = libs.versions.firebase.cocoapods.get()
            extraOpts += listOf("-compiler-option", "-fmodules")
        }
        // As of Firebase 10.17+, Firestore moved its ObjC headers to
        // FirebaseFirestoreInternal; the Kotlin cocoapods plugin needs it added
        // explicitly and bound as the interop source for FirebaseFirestore itself
        // (mirrors GitLive's own build setup for this exact reason).
        pod("FirebaseFirestoreInternal") {
            version = libs.versions.firebase.cocoapods.get()
            extraOpts += listOf("-compiler-option", "-fmodules")
        }
        pod("FirebaseFirestore") {
            version = libs.versions.firebase.cocoapods.get()
            extraOpts += listOf("-compiler-option", "-fmodules")
            useInteropBindingFrom("FirebaseFirestoreInternal")
        }
        pod("FirebaseStorage") {
            version = libs.versions.firebase.cocoapods.get()
            extraOpts += listOf("-compiler-option", "-fmodules")
        }
    }

    js {
        browser()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }

    android {
       namespace = "subha.app.cyra.sharedLogic"
       compileSdk = libs.versions.android.compileSdk.get().toInt()
       minSdk = libs.versions.android.minSdk.get().toInt()

       compilerOptions {
           // Bumped from 11 - GitLive Firestore's `DocumentReference.set()` (used by
           // ProfileRepository) is an inline function compiled at JVM target 17;
           // inlining it into JVM-11 bytecode fails the build ("Cannot inline bytecode
           // built with JVM target 17 into bytecode that is being built with JVM target
           // 11"). Kept in sync with androidApp/build.gradle.kts's own jvmTarget.
           jvmTarget = JvmTarget.JVM_17
       }
       androidResources {
           enable = true
       }
       withHostTest {
           isIncludeAndroidResources = true
       }
    }

    // Extends (rather than replaces) the default hierarchy template with one extra
    // grouping node: "mobile" = android + all ios targets, sitting between commonMain
    // and androidMain/iosMain. Room/Koin/Ktor/GitLive Firebase/multiplatform-settings
    // don't publish js/wasmJs variants, and androidApp+iosApp are the only real
    // consumers of "full shared layer" logic (js/wasmJs via :webApp/:sharedUI are
    // parked, per the architecture plan) - so those dependencies and the code that
    // uses them live in `mobileMain`, not `commonMain`. Using a raw `.dependsOn()`
    // instead of this API conflicts with the template Kotlin already applies for the
    // iosArm64/iosSimulatorArm64 -> iosMain grouping and silently breaks it. Declared
    // after all targets so the template has them available to group.
    applyDefaultHierarchyTemplate {
        common {
            group("mobile") {
                withAndroidTarget()
                withIos()
            }
        }
    }

    sourceSets {
        all {
            // FlowSettings (multiplatform-settings coroutines) is still marked
            // experimental upstream; we've accepted that surface deliberately.
            languageSettings.optIn("com.russhwolf.settings.ExperimentalSettingsApi")
        }

        val mobileMain by getting
        // The "mobile" hierarchy-template group above wires mobileMain directly into
        // the iosArm64Main/iosSimulatorArm64Main LEAF compilations (confirmed via a
        // one-off `kotlin.sourceSets.forEach { println(it.dependsOn) }` probe) - but NOT
        // into the intermediate `iosMain` source set (still only appleMain->commonMain),
        // and `withAndroidTarget()` doesn't match the target produced by the
        // `com.android.kotlin.multiplatform.library` plugin at all, so `androidMain`
        // never joined the group either. Both gaps are safe to close with an explicit
        // edge here since they're additive, not redefinitions of anything the template
        // itself manages.
        androidMain.get().dependsOn(mobileMain)
        iosMain.get().dependsOn(mobileMain)

        commonMain.dependencies {
            // MVVM base contracts (BaseViewModel/NavigationEvent) - the multiplatform
            // androidx.lifecycle.viewmodel artifact does publish js/wasmJs, so this can
            // stay common.
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.coroutines.core)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        mobileMain.dependencies {
            // DI - shared on both platforms so the same ViewModels/repositories
            // resolve identically on Android and iOS (Hilt cannot do this)
            implementation(libs.koin.core)
            implementation(libs.koin.core.viewmodel)

            // Local database - offline-first source of truth
            implementation(libs.room.runtime)
            implementation(libs.sqlite.bundled)

            // Networking - Cloud Functions only (AI chat proxy, report generation),
            // never calls Gemini or any third-party API directly
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.contentnegotiation)
            implementation(libs.ktor.serialization.json)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.auth)

            // Firebase - GitLive KMP SDK, one shared implementation for both platforms
            implementation(libs.firebase.auth)
            implementation(libs.firebase.firestore)
            implementation(libs.firebase.storage)

            // Lightweight prefs/session flags (not auth tokens - GitLive owns those)
            implementation(libs.multiplatform.settings)
            implementation(libs.multiplatform.settings.coroutines)
        }
        androidMain.dependencies {
            implementation(libs.ktor.client.android)
            // koin-core-viewmodel (in mobileMain) resolves ViewModels the same way on
            // both platforms, but androidContext()/androidLogger() - used to seed the
            // Room builder and Settings with a real Context - only exist in koin-android.
            implementation(libs.koin.android)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        jsMain.dependencies {
            implementation(libs.wrappers.browser)
        }
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

// SKIE 0.10.4 didn't support Kotlin 2.4.10 (verified: the plugin's own compatibility
// check failed the build); 0.10.14 does. If a future Kotlin bump ever outpaces SKIE
// again, flip this to `false` and rely on `core/presentation/FlowWatcher.kt`'s manual
// Swift-bridging fallback in the meantime.
skie {
    isEnabled = true
}

dependencies {
    add("kspAndroid", libs.room.compiler)
    add("kspIosArm64", libs.room.compiler)
    add("kspIosSimulatorArm64", libs.room.compiler)

    // GitLive's Android artifacts declare their com.google.firebase:* transitive deps
    // with no pinned version - they expect the consumer to apply the official Firebase
    // Android BOM to resolve them. `platform()` needs the plain top-level DependencyHandler
    // (KotlinDependencyHandler inside `sourceSets { androidMain.dependencies {} }` doesn't
    // expose it), hence declaring it here against the generated Android configuration
    // instead. (Full runtime Firebase init also needs the google-services Gradle plugin +
    // a google-services.json, neither of which exist in this repo yet - add both before
    // wiring the first real Firebase call in the Auth feature.)
    add("androidMainImplementation", platform(libs.firebase.bom))
}
