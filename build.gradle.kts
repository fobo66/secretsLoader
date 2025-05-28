import org.gradle.kotlin.dsl.registering
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `java-gradle-plugin`
    `kotlin-dsl`

    kotlin("jvm") version "2.1.21"
    kotlin("plugin.serialization") version "2.1.21"
    id("com.gradle.plugin-publish") version "1.3.1"
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
    implementation("com.android.tools.build:gradle-api:8.10.1")
    implementation("com.charleskorn.kaml:kaml:0.78.0")
    testImplementation(kotlin("test-junit5"))
    testImplementation("io.mockk:mockk:1.14.2")
}

gradlePlugin {
    plugins.register("secretsLoader") {
        id = "dev.fobo66.secretsloader"
        displayName = "SecretsLoader – load secrets from the YAML file"
        description =
            "A plugin that helps you with loading sensitive data like API keys from encrypted YAML files into variant-specific res values or build config fields"
        implementationClass = "dev.fobo66.secretsloader.SecretsLoaderPlugin"
        website.set("https://github.com/fobo66/secretsLoader")
        vcsUrl.set("https://github.com/fobo66/secretsLoader.git")
        tags.set(listOf("android", "secrets", "config", "credentials-management"))
    }
}

val functionalTestSourceSet: SourceSet =
    sourceSets.create("functionalTest") {
    }

tasks.test {
    useJUnitPlatform()
}

gradlePlugin.testSourceSets(functionalTestSourceSet)
configurations.getByName("functionalTestImplementation").extendsFrom(configurations.getByName("testImplementation"))

val functionalTest by tasks.registering(Test::class) {
    testClassesDirs = functionalTestSourceSet.output.classesDirs
    classpath = functionalTestSourceSet.runtimeClasspath
}

val check by tasks.getting(Task::class) {
    dependsOn(functionalTest)
}
