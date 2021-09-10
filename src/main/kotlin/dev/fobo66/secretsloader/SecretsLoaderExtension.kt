package dev.fobo66.secretsloader

import dev.fobo66.secretsloader.util.SECRETS_DIR_NAME
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.ProjectLayout
import org.gradle.api.provider.Property

abstract class SecretsLoaderExtension(projectLayout: ProjectLayout) {

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
     * Whether secrets will be added to the resources. Default value is false
     */
    abstract val useResourceValues: Property<Boolean>

    /**
     * Whether signing config will be added to the build types from the secrets. Default value is false
     */
    abstract val useSigningConfig: Property<Boolean>

    /**
     * Suffix for the secrets file that will be removed after decryption. Used commonly to distinguish encrypted files
     */
    abstract val encryptionSuffix: Property<String>

    /**
     * File extension for the Java key store file. Use it without the leading dot. Default value is `jks`
     */
    abstract val keyStoreFileExtension: Property<String>

    /**
     * Folder to store secrets
     */
    abstract val secretsFolder: DirectoryProperty

    init {
        encryptionToolExecutable.convention("openssl")
        encryptionAlgorithm.convention("aes-256-cdc")
        encryptionMessageDigestAlgorithm.convention("sha256")
        encryptionSuffix.convention(".cipher")
        keyStoreFileExtension.convention("jks")
        useBuildConfig.convention(true)
        useResourceValues.convention(false)
        useSigningConfig.convention(false)
        secretsFolder.convention(projectLayout.buildDirectory.dir(SECRETS_DIR_NAME))
    }
}
