package com.localdownloader.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.localdownloader.domain.models.LocalPlaylist
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

private val Context.playlistDataStore: DataStore<Preferences> by preferencesDataStore(name = "local_playlists")

@Singleton
class PlaylistStore @Inject constructor(
    @ApplicationContext private val context: Context,
    private val json: Json,
) {
    private object Keys {
        val playlistsJson = stringPreferencesKey("playlists_json")
    }

    val playlists: Flow<List<LocalPlaylist>> = context.playlistDataStore.data.map { prefs ->
        val raw = prefs[Keys.playlistsJson] ?: return@map emptyList()
        runCatching { json.decodeFromString<List<LocalPlaylist>>(raw) }.getOrDefault(emptyList())
    }

    suspend fun savePlaylist(playlist: LocalPlaylist) {
        context.playlistDataStore.edit { prefs ->
            val current = runCatching {
                json.decodeFromString<List<LocalPlaylist>>(prefs[Keys.playlistsJson] ?: "[]")
            }.getOrDefault(emptyList()).toMutableList()
            val existingIndex = current.indexOfFirst { it.id == playlist.id }
            if (existingIndex >= 0) {
                current[existingIndex] = playlist
            } else {
                current.add(playlist)
            }
            prefs[Keys.playlistsJson] = json.encodeToString(current)
        }
    }

    suspend fun deletePlaylist(playlistId: String) {
        context.playlistDataStore.edit { prefs ->
            val current = runCatching {
                json.decodeFromString<List<LocalPlaylist>>(prefs[Keys.playlistsJson] ?: "[]")
            }.getOrDefault(emptyList()).filter { it.id != playlistId }
            prefs[Keys.playlistsJson] = json.encodeToString(current)
        }
    }
}
