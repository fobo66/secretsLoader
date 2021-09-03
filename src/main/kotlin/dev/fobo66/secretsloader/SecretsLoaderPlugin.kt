package dev.fobo66.secretsloader

import com.android.build.api.variant.AndroidComponentsExtension
import dev.fobo66.secretsloader.util.SECRETS_DIR_NAME
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.register
import org.gradle.util.GradleVersion

class SecretsLoaderPlugin : Plugin<Project> {
    private val minGradleVersion = GradleVersion.version("7.0")

    override fun apply(target: Project) {
        val gradleVersion = GradleVersion.version(target.gradle.gradleVersion)
        check(gradleVersion >= minGradleVersion) {
            "secretsLoader requires Gradle 7.0 or later."
        }

        val secretsParams = target.extensions.create("secretsLoader", SecretsLoaderExtension::class)

        target.extensions.findByType(AndroidComponentsExtension::class)?.beforeVariants { variant ->
            val loadSecretsTask = target.tasks.register("load${variant.name}Secrets", LoadSecretsTask::class) {
                encryptionAlgorithm.set(secretsParams.encryptionAlgorithm)
                encryptionMessageDigestAlgorithm.set(secretsParams.encryptionMessageDigestAlgorithm)
                encryptionPassword.set(secretsParams.encryptionPassword)
                encryptionSuffix.set(secretsParams.encryptionSuffix)
                secretInputs.set(secretsParams.secretsFolder)
                secretOutputs.set(target.layout.buildDirectory.dir(SECRETS_DIR_NAME))
            }

            if (secretsParams.useBuildConfig.get()) {
                val addBuildConfigValuesTask =
                    target.tasks.register("add${variant.name}BuildConfigValues", AddBuildConfigValuesTask::class) {
                        buildConfigFile.set(
                            target.layout.buildDirectory.dir(SECRETS_DIR_NAME).get().file("${variant.name}BuildConfig.yml")
                        )
                        flavorName.set(variant.name)
                        dependsOn(loadSecretsTask)
                    }
            }

            target.tasks.findByName("pre${variant.name}Build")?.dependsOn(loadSecretsTask)
        }
    }
}
