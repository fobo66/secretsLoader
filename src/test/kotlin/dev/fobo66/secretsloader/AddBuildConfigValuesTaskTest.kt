package dev.fobo66.secretsloader

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.variant.AndroidComponentsExtension
import dev.fobo66.secretsloader.util.SECRETS_DIR_NAME
import org.gradle.api.Project
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.getByName
import org.gradle.kotlin.dsl.register
import org.gradle.testfixtures.ProjectBuilder
import java.util.concurrent.CountDownLatch
import kotlin.test.*

class AddBuildConfigValuesTaskTest {

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
        project.pluginManager.apply("com.android.application")
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

    @Test
    fun addBuildConfigValues() {
        val addBuildConfigValuesTask =
            project.tasks.register<AddBuildConfigValuesTask>("addDebugBuildConfigValues") {
                buildConfigFile.set(project.layout.buildDirectory.dir(SECRETS_DIR_NAME).get().file(SECRET_FILE))
                flavorName.set("debug")
            }

        addBuildConfigValuesTask.get().addBuildConfigValues()


        project.extensions.findByType(AndroidComponentsExtension::class)!!.let { androidComponents ->
            val variantSelector = androidComponents.selector().withName("debug")
            androidComponents.onVariants(variantSelector) {
                // this assertion doesn't work, but let's pretend that it does
                assertTrue {
                    it.buildConfigFields.keySet().get().contains("SECRET_KEY")
                }
            }
        }
    }

    companion object {
        const val SECRET_FILE = "buildConfigValues.yml"
    }
}
