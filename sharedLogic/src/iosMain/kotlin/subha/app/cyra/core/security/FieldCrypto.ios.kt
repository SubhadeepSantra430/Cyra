package subha.app.cyra.core.security

/**
 * TODO(security-spike): implement using the iOS Keychain + Secure Enclave.
 *
 * Kotlin/Native cannot call CryptoKit directly (it's a Swift-only API with no C entry
 * points), and hand-rolling AES-GCM against the C `Security`/`CommonCrypto` frameworks
 * via cinterop is easy to get subtly wrong for something this sensitive (encrypting
 * journal/health data) without a dedicated verification pass. Rather than ship
 * unverified crypto, this is intentionally left unimplemented - do NOT wire this into
 * any repository until it's been implemented and reviewed.
 *
 * Recommended path: a small Swift-side `KeychainFieldCrypto` class (Secure
 * Enclave-backed key via `kSecAttrTokenIDSecureEnclave`, AES-GCM via CryptoKit) exposed
 * back to Kotlin through an `expect`/`actual` boundary that iOS satisfies by calling
 * into Swift (e.g. via a protocol implemented in `iosApp` and injected into Koin at
 * `doInitKoin()` time), OR a cinterop wrapper around `CCCrypt`/`SecKeyCreateEncryptedData`
 * once verified against real Keychain access-control flags.
 */
actual class FieldCrypto {
    actual fun encrypt(plainText: String): String {
        throw NotImplementedError(
            "FieldCrypto.encrypt is not yet implemented on iOS - see TODO(security-spike) above"
        )
    }

    actual fun decrypt(cipherText: String): String {
        throw NotImplementedError(
            "FieldCrypto.decrypt is not yet implemented on iOS - see TODO(security-spike) above"
        )
    }
}
