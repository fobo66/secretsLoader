package dev.fobo66.secretsloader.entities

import kotlinx.serialization.Serializable

@Serializable
data class Secret(
    val type: String,
    val value: String,
    val comment: String? = null,
)
