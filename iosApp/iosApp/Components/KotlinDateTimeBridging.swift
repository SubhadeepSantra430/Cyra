import SharedLogic

/// kotlinx-datetime lives in its own Kotlin/Native klib, separate from `sharedLogic`
/// itself - Kotlin's Objective-C export prefixes cross-klib types with the source
/// module's name to avoid collisions, so the type Swift actually sees is
/// `Kotlinx_datetimeLocalDate`, not the plain `LocalDate` its Kotlin declarations use
/// (confirmed by inspecting `SharedLogic.h`'s generated `swift_name` attributes - this
/// isn't guessable from the Kotlin source alone). This typealias keeps the rest of the
/// iOS codebase readable.
typealias LocalDate = Kotlinx_datetimeLocalDate
