import org.gradle.kotlin.dsl.registering
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

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
