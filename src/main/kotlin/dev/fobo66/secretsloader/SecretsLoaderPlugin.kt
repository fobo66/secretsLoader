package dev.fobo66.secretsloader

import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.register

class SecretsLoaderPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val secretsParams = target.extensions.create("secretsLoader", SecretsLoaderExtension::class.java)

        target.extensions.findByType(AndroidComponentsExtension::class.java)?.beforeVariants {
            val loadSecretsTask = target.tasks.register("load${it.name}Secrets", LoadSecretsTask::class) {
                encryptionAlgorithm.set(secretsParams.encryptionAlgorithm)
                encryptionMessageDigestAlgorithm.set(secretsParams.encryptionMessageDigestAlgorithm)
                encryptionPassword.set(secretsParams.encryptionPassword)
                encryptionSuffix.set(secretsParams.encryptionSuffix)
                secretInputs.set(secretsParams.secretsFolder)
                secretOutputs.set(target.layout.buildDirectory.dir("secrets"))
            }

            target.tasks.findByName("pre${it.name}Build")?.dependsOn(loadSecretsTask)
        }
    }
}
