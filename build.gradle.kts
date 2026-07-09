import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.utils.extendsFrom

plugins {
    `java-gradle-plugin`
    `kotlin-dsl`

    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.publisher)
    alias(libs.plugins.kotlinter)
}

group = "dev.fobo66.secretsloader"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation(libs.android.gradle.api)
    implementation(libs.dotenv.kotlin)
    implementation(libs.kotlinpoet)
    implementation(libs.kaml)
    testImplementation(kotlin("test-junit5"))
    testImplementation(libs.mockk)
}

gradlePlugin {
    plugins.register("secretsLoader") {
        id = "dev.fobo66.secretsloader"
        displayName = "SecretsLoader – load secrets from the YAML file"
        description =
            "A plugin that helps you with loading sensitive data like API keys from encrypted YAML files into variant-specific res values or build config fields"
        implementationClass = "dev.fobo66.secretsloader.SecretsLoaderPlugin"
        website = "https://github.com/fobo66/secretsLoader"
        vcsUrl = "https://github.com/fobo66/secretsLoader.git"
        tags = listOf("android", "secrets", "config", "credentials-management")
    }
}

val functionalTestSourceSet: SourceSet =
    sourceSets.create("functionalTest") {
    }

tasks.test {
    useJUnitPlatform()
}

gradlePlugin.testSourceSets(functionalTestSourceSet)
configurations.named("functionalTestImplementation").extendsFrom(configurations.named("testImplementation"))

val functionalTest = tasks.register<Test>("functionalTest") {
    description = "Test tasks integration"
    testClassesDirs = functionalTestSourceSet.output.classesDirs
    classpath = functionalTestSourceSet.runtimeClasspath
}

tasks.named("check") {
    dependsOn(functionalTest)
}
