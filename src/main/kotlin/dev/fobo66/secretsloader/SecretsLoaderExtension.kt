package dev.fobo66.secretsloader

import dev.fobo66.secretsloader.util.SECRETS_DIR_NAME
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.ProjectLayout
import org.gradle.api.provider.Property

abstract class SecretsLoaderExtension(val projectLayout: ProjectLayout) {

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
     * Message digest algorithm for the secrets file
     */
    abstract val encryptionMessageDigestAlgorithm: Property<String>

    /**
     * Whether secrets will be added to the BuildConfig. Default value is true
     */
    abstract val useBuildConfig: Property<Boolean>

    /**
     * Suffix for the secrets file that will be removed after decryption. Used commonly to distinguish encrypted files
     */
    abstract val encryptionSuffix: Property<String>

    /**
     * Folder to store secrets
     */
    abstract val secretsFolder: DirectoryProperty

    init {
        encryptionToolExecutable.convention("openssl")
        encryptionAlgorithm.convention("aes-256-cdc")
        encryptionMessageDigestAlgorithm.convention("md5")
        encryptionSuffix.convention(".cipher")
        useBuildConfig.convention(true)
        secretsFolder.convention(projectLayout.buildDirectory.dir(SECRETS_DIR_NAME))
    }
}
