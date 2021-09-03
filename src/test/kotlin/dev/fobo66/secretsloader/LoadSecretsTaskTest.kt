package dev.fobo66.secretsloader

import dev.fobo66.secretsloader.util.SECRETS_DIR_NAME
import io.mockk.every
import io.mockk.mockk
import org.gradle.api.Project
import org.gradle.api.file.FileSystemLocation
import org.gradle.api.file.FileType
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.register
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.work.ChangeType
import org.gradle.work.FileChange
import org.gradle.work.InputChanges
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFails
import kotlin.test.assertTrue

class LoadSecretsTaskTest {

    private lateinit var project: Project

    private val fileChange: FileChange = mockk {
        every { changeType } returns ChangeType.ADDED
        every { fileType } returns FileType.FILE
        every { normalizedPath } returns SECRET_FILE
    }
    private val inputChanges: InputChanges = mockk {
        every { isIncremental } returns true
        every { getFileChanges(any<Provider<FileSystemLocation>>()) } returns listOf(fileChange)
    }

    @BeforeTest
    fun setUp() {
        project = ProjectBuilder.builder().build()
        project.mkdir(SECRETS_DIR_NAME)
        project.mkdir("build/$SECRETS_DIR_NAME")
        val secretsFile = project.layout.projectDirectory.dir(SECRETS_DIR_NAME)
            .file(SECRET_FILE).asFile
        val testSecretsBytes =
            this.javaClass.classLoader.getResourceAsStream(SECRET_FILE)!!.readAllBytes()
        secretsFile.writeBytes(testSecretsBytes)
        every { fileChange.file } returns secretsFile
    }

    @Test
    fun `load secrets`() {
        val secretsTask = project.tasks.register<LoadSecretsTask>("loadSecrets") {
            encryptionPassword.set("password")
            encryptionToolExecutable.set("openssl")
            encryptionAlgorithm.set("aes-256-cbc")
            encryptionSuffix.set(".cipher")
            encryptionMessageDigestAlgorithm.set("md5")
            secretInputs.set(project.layout.projectDirectory.dir(SECRETS_DIR_NAME))
            secretOutputs.set(project.layout.buildDirectory.dir(SECRETS_DIR_NAME))
        }

        secretsTask.get().loadSecrets(inputChanges)

        assertTrue {
            project.layout.buildDirectory.file("secrets/secret.properties").isPresent
        }
    }

    @Test
    fun `failed to load secrets into non-existent directory`() {
        val secretsTask = project.tasks.register<LoadSecretsTask>("loadSecrets") {
            encryptionPassword.set("password")
            encryptionToolExecutable.set("openssl")
            encryptionAlgorithm.set("aes-256-cbc")
            encryptionSuffix.set(".cipher")
            encryptionMessageDigestAlgorithm.set("md5")
            secretInputs.set(project.layout.projectDirectory.dir(SECRETS_DIR_NAME))
            secretOutputs.set(project.layout.buildDirectory.dir("nosecrets"))
        }

        assertFails {
            secretsTask.get().loadSecrets(inputChanges)
        }
    }

    companion object {
        private const val SECRET_FILE = "secret.properties.cipher"
    }
}
