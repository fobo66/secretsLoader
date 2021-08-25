package dev.fobo66.secretsloader.entities

import kotlinx.serialization.Serializable

@Serializable
data class SigningConfigSecret(
    val keyAlias: String,
    val keyPassword: String,
    val storePassword: String
)
