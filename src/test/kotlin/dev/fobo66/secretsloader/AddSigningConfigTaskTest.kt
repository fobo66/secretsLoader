@file:Suppress("UnstableApiUsage")

package dev.fobo66.secretsloader

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.variant.AndroidComponentsExtension
import dev.fobo66.secretsloader.util.SECRETS_DIR_NAME
import org.gradle.api.Project
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getByName
import org.gradle.kotlin.dsl.register
import org.gradle.testfixtures.ProjectBuilder
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull

class AddSigningConfigTaskTest {

    private lateinit var project: Project

    @BeforeTest
    fun setUp() {
        project = ProjectBuilder.builder().build()
        project.mkdir(SECRETS_DIR_NAME)
        project.mkdir("build/$SECRETS_DIR_NAME")
        val secretsFile = project.layout.buildDirectory.dir(SECRETS_DIR_NAME).get()
            .file(SECRET_FILE).asFile
        val testSecretsBytes =
            this.javaClass.classLoader.getResourceAsStream(SECRET_FILE)!!.readAllBytes()
        secretsFile.writeBytes(testSecretsBytes)
        project.pluginManager.withPlugin("com.android.application") {
            project.extensions.getByName<ApplicationExtension>("android").let {
                it.compileSdk = 30
                it.defaultConfig {
                    applicationId = "com.example.test"
                    minSdk = 30
                    targetSdk = 30
                }

                it.buildTypes {
                    create(BUILD_TYPE) {}
                }
            }
        }
    }

    @Test
    fun addSigningConfig() {
        val addSigningConfigTask =
            project.tasks.register<AddSigningConfigTask>("addDebugSigningConfig") {
                signingConfigFile.set(project.layout.buildDirectory.dir(SECRETS_DIR_NAME).get().file(
                    SECRET_FILE
                ))
                keystoreFile.set(project.layout.buildDirectory.dir(SECRETS_DIR_NAME).get().file(
                    SECRET_FILE
                ))
                buildType.set(BUILD_TYPE)
            }

        addSigningConfigTask.get().addSigningConfig()


        project.extensions.findByType(ApplicationExtension::class)?.let { android ->
            assertNotNull(android.signingConfigs.findByName(BUILD_TYPE))
            assertNotNull(android.buildTypes[BUILD_TYPE].signingConfig)
        }
    }

    companion object {
        private const val SECRET_FILE = "signingConfig.yml"
        private const val BUILD_TYPE = "internal"
    }
}
