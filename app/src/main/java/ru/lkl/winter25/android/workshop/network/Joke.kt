package ru.lkl.winter25.android.workshop.network

import kotlinx.coroutines.sync.Mutex
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Joke(
    @SerialName("error") val error: Boolean,
    @SerialName("category") val category: String,
    @SerialName("type") val type: String,
    @SerialName("setup") val setup: String,
    @SerialName("delivery") val delivery: String,
    @SerialName("id") val id: Int
)
