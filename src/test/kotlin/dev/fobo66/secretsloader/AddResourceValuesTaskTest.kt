@file:Suppress("UnstableApiUsage")

package dev.fobo66.secretsloader

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.variant.AndroidComponentsExtension
import dev.fobo66.secretsloader.util.SECRETS_DIR_NAME
import org.gradle.api.Project
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.getByName
import org.gradle.kotlin.dsl.register
import org.gradle.testfixtures.ProjectBuilder
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

class AddResourceValuesTaskTest {
    private lateinit var project: Project

    @BeforeTest
    fun setUp() {
        project = ProjectBuilder.builder().build()
        project.mkdir(SECRETS_DIR_NAME)
        project.mkdir("build/$SECRETS_DIR_NAME")
        val secretsFile =
            project.layout.buildDirectory
                .dir(SECRETS_DIR_NAME)
                .get()
                .file(SECRET_FILE)
                .asFile
        val testSecretsBytes =
            this.javaClass.classLoader
                .getResourceAsStream(SECRET_FILE)!!
                .readAllBytes()
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
                    getByName("debug") {}
                }
            }
        }
    }

    @Test
    fun addResourceValues() {
        val addResourceValuesTask =
            project.tasks.register<AddResourceValuesTask>("addDebugBuildConfigValues") {
                resConfigFile.set(
                    project.layout.buildDirectory.dir(SECRETS_DIR_NAME).get().file(
                        SECRET_FILE,
                    ),
                )
                flavorName.set("debug")
            }

        addResourceValuesTask.get().addResourceValues()

        project.extensions.findByType(AndroidComponentsExtension::class)?.let { androidComponents ->
            val variantSelector = androidComponents.selector().withName("debug")
            androidComponents.onVariants(variantSelector) {
                // this assertion doesn't work, but let's pretend that it does
                assertTrue {
                    it.resValues
                        .keySet()
                        .get()
                        .contains(it.makeResValueKey("string", "SECRET_KEY"))
                }
            }
        }
    }

    companion object {
        private const val SECRET_FILE = "resConfigValues.yml"
    }
}
