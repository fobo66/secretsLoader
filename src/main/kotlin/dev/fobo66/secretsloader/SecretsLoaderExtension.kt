package dev.fobo66.secretsloader

import org.gradle.api.provider.Property

abstract class SecretsLoaderExtension {

    /**
     * Executable for the encryption tool to encrypt or decrypt secrets file
     */
    abstract val encryptionToolExecutable: Property<String>

    /**
     * Encryption algorithm for the secrets file
     */
    abstract val encryptionAlgorithm: Property<String>

    /**
     * Encryption algorithm for the secrets file
     */
    abstract val encryptionPassword: Property<String>

    /**
     * Suffix for the secrets file that will be removed after decryption. Used commonly to distinguish encrypted files
     */
    abstract val encryptionSuffix: Property<String>

    init {
        encryptionToolExecutable.convention("openssl")
        encryptionAlgorithm.convention("aes-256-cdc")
        encryptionSuffix.convention(".cipher")
    }
}
