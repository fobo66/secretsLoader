package dev.fobo66.secretsloader

import com.android.build.api.variant.AndroidComponentsExtension
import dev.fobo66.secretsloader.util.SecretsProcessor
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.newInstance
import javax.inject.Inject

@CacheableTask
abstract class AddBuildConfigValuesTask
    @Inject
    constructor(
        objectFactory: ObjectFactory,
    ) : DefaultTask() {
        @get:Input
        abstract val flavorName: Property<String>

        @get:InputFile
        @get:PathSensitive(PathSensitivity.NAME_ONLY)
        abstract val buildConfigFile: RegularFileProperty

        private val secretsProcessor = objectFactory.newInstance<SecretsProcessor>()

        @TaskAction
        fun addBuildConfigValues() {
            project.extensions.findByType(AndroidComponentsExtension::class)?.let { androidComponents ->
                val variantSelector = androidComponents.selector().withName(flavorName.get())
                androidComponents.onVariants(variantSelector) {
                    val buildConfigSecrets =
                        secretsProcessor.loadBuildConfigValues(buildConfigFile.asFile.get().inputStream())

                    it.buildConfigFields?.putAll(buildConfigSecrets)
                }
            }
        }
    }
