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
        check(GradleVersion.current() >= minGradleVersion) {
            "secretsLoader requires Gradle 7.0 or later."
        }

        val secretsParams = target.extensions.create("secretsLoader", SecretsLoaderExtension::class, target.layout)

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
                target.tasks.register("add${variant.name}BuildConfigValues", AddBuildConfigValuesTask::class) {
                    buildConfigFile.set(
                        target.layout.buildDirectory.dir(SECRETS_DIR_NAME).get().file("${variant.name}BuildConfig.yml")
                    )
                    flavorName.set(variant.name)
                    dependsOn(loadSecretsTask)
                }
            }

            if (secretsParams.useResourceValues.get()) {
                target.tasks.register("add${variant.name.capitalize()}ResourceValues", AddResourceValuesTask::class) {
                    resConfigFile.set(
                        target.layout.buildDirectory.dir(SECRETS_DIR_NAME).get().file("${variant.name}Resources.yml")
                    )
                    flavorName.set(variant.name)
                    dependsOn(loadSecretsTask)
                }
            }

            if (secretsParams.useSigningConfig.get()) {
                val signingConfigTaskName = "add${variant.buildType?.capitalize()}SigningConfig"

                if (target.tasks.findByName(signingConfigTaskName) == null) {
                    target.tasks.register(
                        signingConfigTaskName,
                        AddSigningConfigTask::class
                    ) {
                        signingConfigFile.set(
                            target.layout.buildDirectory.dir(SECRETS_DIR_NAME).get()
                                .file("${variant.buildType}.yml")
                        )
                        keystoreFile.set(
                            target.layout.buildDirectory.dir(SECRETS_DIR_NAME).get()
                                .file("${variant.buildType}.${secretsParams.keyStoreFileExtension}")
                        )
                        buildType.set(variant.buildType)
                        dependsOn(loadSecretsTask)
                    }
                }
            }

            target.tasks.findByName("pre${variant.name.capitalize()}Build")?.dependsOn(loadSecretsTask)
        }
    }
}
