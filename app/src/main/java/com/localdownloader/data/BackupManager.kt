package com.localdownloader.data

import android.content.Context
import com.localdownloader.domain.models.AccentPreset
import com.localdownloader.domain.models.AppSettings
import com.localdownloader.domain.models.ContrastMode
import com.localdownloader.domain.models.LocalPlaylist
import com.localdownloader.domain.models.SitePreset
import com.localdownloader.domain.models.ThemeMode
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Serializable
data class BackupSettingsDto(
    val languageTag: String = "system",
    val themeMode: String = "LIGHT",
    val accentPreset: String = "TEAL",
    val contrastMode: String = "ULTRA",
    val defaultOutputTemplate: String = "%(title)s [%(id)s].%(ext)s",
    val defaultAudioOutputTemplate: String = "%(title)s [%(id)s].%(ext)s",
    val defaultMergeContainer: String = "auto",
    val defaultAudioFormat: String = "mp3",
    val downloadsRootFolderName: String = "DownloaderLinks",
    val autoDownloadSubtitles: Boolean = false,
    val autoEmbedSubtitles: Boolean = false,
    val autoEmbedMetadata: Boolean = true,
    val autoEmbedThumbnail: Boolean = true,
    val notifyCompletedDownloads: Boolean = true,
    val notifyDownloadErrors: Boolean = true,
    val notifyCanceledDownloads: Boolean = true,
    val maxConcurrentDownloads: Int = 2,
    val defaultConcurrentFragments: Int = 4,
    val allowMeteredDownloads: Boolean = false,
)

@Serializable
data class AppBackupData(
    val version: Int = 1,
    val exportedEpochMs: Long = System.currentTimeMillis(),
    val settings: BackupSettingsDto = BackupSettingsDto(),
    val playlists: List<LocalPlaylist> = emptyList(),
    val sitePresets: List<SitePreset> = emptyList(),
)

@Singleton
class BackupManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settingsStore: SettingsStore,
    private val playlistStore: PlaylistStore,
    private val json: Json,
) {
    suspend fun createBackupJson(): String {
        val settings = settingsStore.observeSettings().first()
        val playlists = playlistStore.playlists.first()
        val backup = AppBackupData(
            settings = BackupSettingsDto(
                languageTag = settings.languageTag,
                themeMode = settings.themeMode.name,
                accentPreset = settings.accentPreset.name,
                contrastMode = settings.contrastMode.name,
                defaultOutputTemplate = settings.defaultOutputTemplate,
                defaultAudioOutputTemplate = settings.defaultAudioOutputTemplate,
                defaultMergeContainer = settings.defaultMergeContainer,
                defaultAudioFormat = settings.defaultAudioFormat,
                downloadsRootFolderName = settings.downloadsRootFolderName,
                autoDownloadSubtitles = settings.autoDownloadSubtitles,
                autoEmbedSubtitles = settings.autoEmbedSubtitles,
                autoEmbedMetadata = settings.autoEmbedMetadata,
                autoEmbedThumbnail = settings.autoEmbedThumbnail,
                notifyCompletedDownloads = settings.notifyCompletedDownloads,
                notifyDownloadErrors = settings.notifyDownloadErrors,
                notifyCanceledDownloads = settings.notifyCanceledDownloads,
                maxConcurrentDownloads = settings.maxConcurrentDownloads,
                defaultConcurrentFragments = settings.defaultConcurrentFragments,
                allowMeteredDownloads = settings.allowMeteredDownloads,
            ),
            playlists = playlists,
        )
        return json.encodeToString(backup)
    }

    suspend fun restoreFromJson(backupJson: String): Result<Unit> {
        return runCatching {
            val backup = json.decodeFromString<AppBackupData>(backupJson)
            val current = settingsStore.observeSettings().first()
            val themeMode = runCatching { ThemeMode.valueOf(backup.settings.themeMode) }.getOrDefault(current.themeMode)
            val accentPreset = runCatching { AccentPreset.valueOf(backup.settings.accentPreset) }.getOrDefault(current.accentPreset)
            val contrastMode = runCatching { ContrastMode.valueOf(backup.settings.contrastMode) }.getOrDefault(current.contrastMode)
            val restoredSettings = current.copy(
                languageTag = backup.settings.languageTag,
                themeMode = themeMode,
                accentPreset = accentPreset,
                contrastMode = contrastMode,
                defaultOutputTemplate = backup.settings.defaultOutputTemplate,
                defaultAudioOutputTemplate = backup.settings.defaultAudioOutputTemplate,
                defaultMergeContainer = backup.settings.defaultMergeContainer,
                defaultAudioFormat = backup.settings.defaultAudioFormat,
                downloadsRootFolderName = backup.settings.downloadsRootFolderName,
                autoDownloadSubtitles = backup.settings.autoDownloadSubtitles,
                autoEmbedSubtitles = backup.settings.autoEmbedSubtitles,
                autoEmbedMetadata = backup.settings.autoEmbedMetadata,
                autoEmbedThumbnail = backup.settings.autoEmbedThumbnail,
                notifyCompletedDownloads = backup.settings.notifyCompletedDownloads,
                notifyDownloadErrors = backup.settings.notifyDownloadErrors,
                notifyCanceledDownloads = backup.settings.notifyCanceledDownloads,
                maxConcurrentDownloads = backup.settings.maxConcurrentDownloads,
                defaultConcurrentFragments = backup.settings.defaultConcurrentFragments,
                allowMeteredDownloads = backup.settings.allowMeteredDownloads,
            )
            settingsStore.updateSettings(restoredSettings)
            backup.playlists.forEach { playlistStore.savePlaylist(it) }
        }
    }
}
