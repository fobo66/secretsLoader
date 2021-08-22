package dev.fobo66.secretsloader

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileType
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import org.gradle.work.ChangeType
import org.gradle.work.Incremental
import org.gradle.work.InputChanges

abstract class LoadSecretsTask : DefaultTask() {

    @get:Incremental
    @get:PathSensitive(PathSensitivity.NAME_ONLY)
    @get:InputDirectory
    abstract val secretInputs: DirectoryProperty

    @get:OutputDirectory
    abstract val secretOutputs: DirectoryProperty

    /**
     * Executable for the encryption tool to encrypt or decrypt secrets file
     */
    @get:Input
    abstract val encryptionToolExecutable: Property<String>

    /**
     * Encryption algorithm for the secrets file
     */
    @get:Input
    abstract val encryptionAlgorithm: Property<String>

    /**
     * Encryption algorithm for the secrets file
     */
    @get:Input
    abstract val encryptionMessageDigestAlgorithm: Property<String>

    /**
     * Encryption password for the secrets file
     */
    @get:Input
    abstract val encryptionPassword: Property<String>

    /**
     * Suffix for the secrets file that will be removed after decryption. Used commonly to distinguish encrypted files
     */
    @get:Input
    abstract val encryptionSuffix: Property<String>

    @TaskAction
    fun loadSecrets(inputChanges: InputChanges) {
        if (inputChanges.isIncremental) {
            logger.debug("Incrementally loading secrets")
        }

        inputChanges.getFileChanges(secretInputs).forEach { change ->
            if (change.fileType == FileType.DIRECTORY) return@forEach

            logger.debug("${change.changeType}: ${change.normalizedPath}")
            val targetFile = secretOutputs.file(
                change.normalizedPath.removeSuffix(encryptionSuffix.get())
            ).get().asFile

            if (change.changeType == ChangeType.REMOVED) {
                targetFile.delete()
            } else {
                project.exec {
                    commandLine(
                        encryptionToolExecutable.get(),
                        encryptionAlgorithm.get(),
                        "-md",
                        encryptionMessageDigestAlgorithm.get(),
                        "-d",
                        "-out",
                        targetFile.path,
                        "-in",
                        change.file.path,
                        "-k",
                        encryptionPassword.get()
                    )
                }
            }
        }
    }
}
