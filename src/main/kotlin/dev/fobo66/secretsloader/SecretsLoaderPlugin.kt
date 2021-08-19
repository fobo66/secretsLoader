package dev.fobo66.secretsloader

import org.gradle.api.Plugin
import org.gradle.api.Project
import com.android.build.AndroidComponentsExtension

class SecretsLoaderPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.extensions.register("secretsLoader", SecretsLoaderExtension::class.java)
    }
}
