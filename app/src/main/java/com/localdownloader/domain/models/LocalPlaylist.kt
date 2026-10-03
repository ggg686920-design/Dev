package com.localdownloader.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class LocalPlaylist(
    val id: String,
    val name: String,
    val createdAtEpochMs: Long = System.currentTimeMillis(),
    val itemPaths: List<String> = emptyList(),
)
