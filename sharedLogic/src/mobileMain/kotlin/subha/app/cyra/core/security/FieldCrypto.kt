package subha.app.cyra.core.security

/**
 * Field-level encryption for sensitive free-text columns (journal notes, AI chat
 * transcripts, symptom notes) stored in Room. This is the Tier-1 encryption-at-rest
 * measure from the architecture plan - applied transparently in the repository layer
 * just before insert / right after read, so ViewModels never see ciphertext.
 *
 * The encryption key itself is hardware-backed: Android Keystore (`AndroidKeyStore`
 * provider) on Android, Keychain/Secure Enclave on iOS. Callers only ever see plain
 * strings in and out.
 */
expect class FieldCrypto() {
    fun encrypt(plainText: String): String
    fun decrypt(cipherText: String): String
}
