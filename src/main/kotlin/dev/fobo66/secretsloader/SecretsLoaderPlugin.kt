package dev.fobo66.secretsloader

import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.register

class SecretsLoaderPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val secretsParams = target.extensions.create("secretsLoader", SecretsLoaderExtension::class.java)

        target.extensions.findByType(AndroidComponentsExtension::class.java)?.beforeVariants { variant ->
            val loadSecretsTask = target.tasks.register("load${variant.name}Secrets", LoadSecretsTask::class) {
                encryptionAlgorithm.set(secretsParams.encryptionAlgorithm)
                encryptionMessageDigestAlgorithm.set(secretsParams.encryptionMessageDigestAlgorithm)
                encryptionPassword.set(secretsParams.encryptionPassword)
                encryptionSuffix.set(secretsParams.encryptionSuffix)
                secretInputs.set(secretsParams.secretsFolder)
                secretOutputs.set(target.layout.buildDirectory.dir("secrets"))
            }

            if (secretsParams.useBuildConfig.get()) {
                val addBuildConfigValuesTask =
                    target.tasks.register("add${variant.name}BuildConfigValues", AddBuildConfigValuesTask::class) {
                        buildConfigFile.set(target.layout.buildDirectory.dir("secrets").get().file("${variant.name}BuildConfig.yml"))
                        flavorName.set(variant.name)
                        dependsOn(loadSecretsTask)
                    }
            }

            target.tasks.findByName("pre${variant.name}Build")?.dependsOn(loadSecretsTask)
        }
    }
}
