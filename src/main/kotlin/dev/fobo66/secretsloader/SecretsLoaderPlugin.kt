package dev.fobo66.secretsloader

import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class SecretsLoaderPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.extensions.create("secretsLoader", SecretsLoaderExtension::class.java)

        target.extensions.findByType(AndroidComponentsExtension::class.java)?.beforeVariants {

        }
    }
}
