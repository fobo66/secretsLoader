import org.gradle.configurationcache.extensions.serviceOf

plugins {
    `java-gradle-plugin`
    `kotlin-dsl`

    embeddedKotlin("plugin.serialization")
    id("com.gradle.plugin-publish") version "1.2.0"
}

group = "dev.fobo66.secretsloader"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation("com.android.tools.build:gradle-api:8.0.2")
    implementation("com.charleskorn.kaml:kaml:0.54.0")
    testImplementation(kotlin("test-junit5"))
    testImplementation("io.mockk:mockk:1.13.5")
    testRuntimeOnly(
        files(
            serviceOf<org.gradle.api.internal.classpath.ModuleRegistry>().getModule("gradle-tooling-api-builders")
                .classpath.asFiles.first()
        )
    )
}

gradlePlugin {
    plugins.register("secretsLoader") {
        id = "dev.fobo66.secretsloader"
        displayName = "SecretsLoader – load secrets from the"
        description =
            "A plugin that helps you with loading sensitive data like API keys from encrypted YAML files into variant-specific res values or build config fields"
        implementationClass = "dev.fobo66.secretsloader.SecretsLoaderPlugin"
        website.set("https://github.com/fobo66/secretsLoader")
        vcsUrl.set("https://github.com/fobo66/secretsLoader.git")
        tags.set(listOf("android", "secrets", "config", "credentials-management"))
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
