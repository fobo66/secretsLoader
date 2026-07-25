package dev.fobo66.secretsloader

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import io.github.cdimascio.dotenv.Dotenv
import org.gradle.api.Project
import org.gradle.process.ExecOperations

fun decrypt(
    execOperations: ExecOperations,
    inputPath: String,
    outputPath: String,
    encryptionToolExecutable: String,
    encryptionAlgorithm: String,
    encryptionMessageDigestAlgorithm: String,
    encryptionPassword: String,
) {
    execOperations.exec {
        commandLine(
            encryptionToolExecutable,
            encryptionAlgorithm,
            "-md",
            encryptionMessageDigestAlgorithm,
            "-d",
            "-out",
            outputPath,
            "-in",
            inputPath,
            "-k",
            encryptionPassword,
        )
    }
}

fun generateSecrets(
    project: Project,
    dotenv: Dotenv,
    packageName: String = "",
    fileName: String = "Secrets",
    objectName: String = fileName
) {
    val secretObject = TypeSpec.objectBuilder(objectName)
        .apply {
            dotenv.entries().forEach { entry ->
                addProperty(
                    PropertySpec.builder(entry.key, String::class, KModifier.CONST)
                        .initializer("%S", entry.value)
                        .build()
                )
            }
        }
        .build()
    val secretsFile = FileSpec.builder(packageName, fileName)
        .indent("    ")
        .addFileComment("%S", "Automatically generated file. DO NOT MODIFY")
        .addType(secretObject)
        .build()

    secretsFile.writeTo(project.layout.buildDirectory.dir("generated/source/secret").get().asFile)
}
