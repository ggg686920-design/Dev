package com.localdownloader.media

import com.localdownloader.ffmpeg.FfmpegExecutor
import com.localdownloader.utils.Logger
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

data class MediaMetadata(
    val title: String = "",
    val artist: String = "",
    val album: String = "",
    val genre: String = "",
)

@Singleton
class MetadataEditor @Inject constructor(
    private val ffmpegExecutor: FfmpegExecutor,
    private val logger: Logger,
) {
    suspend fun updateMetadata(
        sourceFile: File,
        metadata: MediaMetadata,
    ): Result<File> {
        val tempOutput = File(sourceFile.parentFile, "meta_${System.currentTimeMillis()}_${sourceFile.name}")
        val args = mutableListOf(
            "-i", sourceFile.absolutePath,
            "-map", "0",
            "-codec", "copy",
        )
        if (metadata.title.isNotBlank()) {
            args += listOf("-metadata", "title=${metadata.title}")
        }
        if (metadata.artist.isNotBlank()) {
            args += listOf("-metadata", "artist=${metadata.artist}")
        }
        if (metadata.album.isNotBlank()) {
            args += listOf("-metadata", "album=${metadata.album}")
        }
        if (metadata.genre.isNotBlank()) {
            args += listOf("-metadata", "genre=${metadata.genre}")
        }
        args += listOf("-y", tempOutput.absolutePath)

        return runCatching {
            val result = ffmpegExecutor.execute(args)
            if (result.exitCode == 0 && tempOutput.exists() && tempOutput.length() > 0) {
                sourceFile.delete()
                tempOutput.renameTo(sourceFile)
                sourceFile
            } else {
                tempOutput.delete()
                error("FFmpeg metadata update failed (code ${result.exitCode}): ${result.stderr}")
            }
        }.onFailure {
            logger.e("MetadataEditor", "Failed to update metadata for ${sourceFile.name}", it)
            tempOutput.delete()
        }
    }
}
