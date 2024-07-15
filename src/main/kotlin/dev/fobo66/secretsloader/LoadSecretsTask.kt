@file:Suppress("UnstableApiUsage")

package dev.fobo66.secretsloader

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileType
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import org.gradle.process.ExecOperations
import org.gradle.work.ChangeType
import org.gradle.work.FileChange
import org.gradle.work.Incremental
import org.gradle.work.InputChanges
import java.io.File
import javax.inject.Inject

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

    @get:Inject
    abstract val execOperations: ExecOperations

    @TaskAction
    fun loadSecrets(inputChanges: InputChanges) {
        if (inputChanges.isIncremental) {
            logger.debug("Incrementally loading secrets")
        } else {
            logger.debug("Loading secrets not incrementally")
        }

        inputChanges.getFileChanges(secretInputs).forEach { change ->
            if (change.fileType == FileType.DIRECTORY) return@forEach

            logger.debug("{}: {}", change.changeType, change.normalizedPath)
            val targetFile = secretOutputs.file(
                change.normalizedPath.removeSuffix(encryptionSuffix.get())
            ).get().asFile

            if (change.changeType == ChangeType.REMOVED) {
                logger.debug("Deleting ${targetFile.name}")
                targetFile.delete()
            } else {
                logger.debug("Decrypting ${targetFile.name}")
                decrypt(targetFile, change)
            }
        }
    }

    private fun decrypt(targetFile: File, change: FileChange) {
        execOperations.exec {
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
