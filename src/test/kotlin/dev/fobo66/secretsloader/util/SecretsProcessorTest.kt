package dev.fobo66.secretsloader.util

import org.junit.Before
import java.io.InputStream
import kotlin.test.Test
import kotlin.test.assertTrue

class SecretsProcessorTest {

    private lateinit var buildConfigSecretsFile: InputStream
    private lateinit var resConfigSecretsFile: InputStream


    @Before
    fun setUp() {
        buildConfigSecretsFile = javaClass.classLoader.getResourceAsStream("buildConfigValues.yml")!!
        resConfigSecretsFile = javaClass.classLoader.getResourceAsStream("resConfigValues.yml")!!

    }

    @Test
    fun loadBuildConfigValues() {
        val buildConfigSecrets = SecretsProcessor().loadBuildConfigValues(buildConfigSecretsFile)

        assertTrue {
            buildConfigSecrets.containsKey("SECRET_KEY")
        }
    }

    @Test
    fun loadResConfigValues() {
        val resConfigSecrets = SecretsProcessor().loadResConfigValues(resConfigSecretsFile)

        assertTrue {
            resConfigSecrets.containsKey("SECRET_KEY")
        }
    }
}
