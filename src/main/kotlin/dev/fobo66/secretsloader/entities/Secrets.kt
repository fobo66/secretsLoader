package dev.fobo66.secretsloader.entities

import kotlinx.serialization.Serializable

@Serializable
data class Secrets(
    val secrets: Map<String, Secret>,
)
