import org.gradle.configurationcache.extensions.serviceOf
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-gradle-plugin`
    `kotlin-dsl`

    kotlin("jvm") version "1.7.10"
    kotlin("plugin.serialization") version "1.7.10"
    id("com.gradle.plugin-publish") version "1.0.0"
}

group = "dev.fobo66.secretsloader"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

dependencies {
    implementation("com.android.tools.build:gradle:7.2.1")
    implementation("com.charleskorn.kaml:kaml:0.46.0")
    testImplementation(kotlin("test-junit5"))
    testImplementation("io.mockk:mockk:1.12.4")
    testRuntimeOnly(
        files(
            serviceOf<org.gradle.api.internal.classpath.ModuleRegistry>().getModule("gradle-tooling-api-builders")
                .classpath.asFiles.first()
        )
    )
}

pluginBundle {
    website = "https://github.com/fobo66/secretsLoader"
    vcsUrl = "https://github.com/fobo66/secretsLoader.git"
    tags = listOf("android", "secrets", "config", "credentials-management")
}

gradlePlugin {
    plugins.register("secretsLoader") {
        id = "dev.fobo66.secretsloader"
        displayName = "SecretsLoader – load secrets from the"
        description =
            "A plugin that helps you with loading sensitive data like API keys from encrypted YAML files into variant-specific res values or build config fields"
        implementationClass = "dev.fobo66.secretsloader.SecretsLoaderPlugin"
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
    }
}

val functionalTestSourceSet = sourceSets.create("functionalTest") {
}

tasks.withType<Test> {
    useJUnitPlatform()
}

gradlePlugin.testSourceSets(functionalTestSourceSet)
configurations.getByName("functionalTestImplementation").extendsFrom(configurations.getByName("testImplementation"))

val functionalTest by tasks.creating(Test::class) {
    testClassesDirs = functionalTestSourceSet.output.classesDirs
    classpath = functionalTestSourceSet.runtimeClasspath
}

val check by tasks.getting(Task::class) {
    dependsOn(functionalTest)
}
