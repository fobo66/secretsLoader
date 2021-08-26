package dev.fobo66.secretsloader.util

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SecretsProcessorTest {

    @Test
    fun loadBuildConfigValues() {
        val buildConfigSecretsFile = javaClass.classLoader.getResourceAsStream("buildConfigValues.yml")!!
        val buildConfigSecrets = SecretsProcessor().loadBuildConfigValues(buildConfigSecretsFile)

        assertTrue {
            buildConfigSecrets.containsKey("SECRET_KEY")
        }
    }

    @Test
    fun loadResConfigValues() {
        val resConfigSecretsFile = javaClass.classLoader.getResourceAsStream("resConfigValues.yml")!!
        val resConfigSecrets = SecretsProcessor().loadResourceValues(resConfigSecretsFile)

        assertTrue {
            resConfigSecrets.containsKey("SECRET_KEY")
        }
    }

    @Test
    fun loadSigningConfig() {
        val signingConfigSecretsFile = javaClass.classLoader.getResourceAsStream("signingConfig.yml")!!
        val resConfigSecrets = SecretsProcessor().loadSigningConfig(signingConfigSecretsFile)

        assertEquals("test", resConfigSecrets.keyPassword)
    }
}
