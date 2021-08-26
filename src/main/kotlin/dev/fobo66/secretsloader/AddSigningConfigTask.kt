package dev.fobo66.secretsloader

import com.android.build.api.dsl.ApplicationExtension
import dev.fobo66.secretsloader.util.SecretsProcessor
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.newInstance
import javax.inject.Inject

abstract class AddSigningConfigTask @Inject constructor(objectFactory: ObjectFactory) : DefaultTask() {

    @get:Input
    abstract val flavor: Property<Pair<String, String>>

    @get:InputFile
    abstract val keystoreFile: RegularFileProperty

    @get:InputFile
    abstract val signingConfigFile: RegularFileProperty

    private val secretsProcessor = objectFactory.newInstance(SecretsProcessor::class)

    @TaskAction
    fun addSigningConfig() {
        project.extensions.findByType(ApplicationExtension::class.java)?.let { android ->
            val signingSecrets = secretsProcessor.loadSigningConfig(signingConfigFile.asFile.get().inputStream())
            val signingConfig = android.signingConfigs.maybeCreate(flavor.get().second).apply {
                keyAlias = signingSecrets.keyAlias
                keyPassword = signingSecrets.keyPassword
                storeFile = keystoreFile.asFile.get()
                storePassword = signingSecrets.storePassword
            }
            android.productFlavors.findByName(flavor.get().second)?.signingConfig = signingConfig
        }
    }
}
