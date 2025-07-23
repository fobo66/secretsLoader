package dev.fobo66.secretsloader.util

import com.android.build.api.variant.BuildConfigField
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.decodeFromStream
import dev.fobo66.secretsloader.entities.Secret
import dev.fobo66.secretsloader.entities.Secrets
import dev.fobo66.secretsloader.entities.SigningConfigSecret
import java.io.InputStream

open class SecretsProcessor {
    fun loadBuildConfigValues(inputStream: InputStream): Map<String, BuildConfigField<String>> {
        val secrets =
            Yaml.default.decodeFromStream(
                Secrets.serializer(),
                inputStream,
            )

        return secrets.secrets.mapValues {
            BuildConfigField(it.value.type, it.value.value, it.value.comment)
        }
    }

    fun loadResourceValues(inputStream: InputStream): Map<String, Secret> {
        val secrets =
            Yaml.default.decodeFromStream(
                Secrets.serializer(),
                inputStream,
            )

        return secrets.secrets
    }

    fun loadSigningConfig(inputStream: InputStream): SigningConfigSecret =
        Yaml.default.decodeFromStream(
            SigningConfigSecret.serializer(),
            inputStream,
        )
}
