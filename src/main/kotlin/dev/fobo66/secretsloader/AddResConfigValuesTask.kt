package dev.fobo66.secretsloader

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.ResValue
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

abstract class AddResConfigValuesTask @Inject constructor(objectFactory: ObjectFactory) : DefaultTask() {

    @get:Input
    abstract val flavorName: Property<String>

    @get:InputFile
    abstract val resConfigFile: RegularFileProperty

    private val secretsProcessor = objectFactory.newInstance(SecretsProcessor::class)

    @TaskAction
    fun addBuildConfigValues() {
        project.extensions.findByType(AndroidComponentsExtension::class.java)?.let { androidComponents ->
            val variantSelector = androidComponents.selector().withName(flavorName.get())
            androidComponents.onVariants(variantSelector) { variant ->
                val resConfigSecrets =
                    secretsProcessor.loadResConfigValues(resConfigFile.asFile.get().inputStream())
                        .mapKeys { entry -> variant.makeResValueKey(entry.value.type, entry.key) }
                        .mapValues { entry -> ResValue(entry.value.value, entry.value.comment) }

                variant.resValues.putAll(resConfigSecrets)
            }
        }
    }
}
