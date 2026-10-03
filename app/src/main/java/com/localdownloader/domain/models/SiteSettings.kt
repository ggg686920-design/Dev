package com.localdownloader.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class SitePreset(
    val domainPattern: String,
    val preferredQuality: String = "BEST",
    val preferredStreamType: String = "VIDEO_AND_AUDIO",
    val preferredAudioFormat: String = "mp3",
    val preferredSubfolder: String = "",
)

object DefaultSitePresets {
    fun defaults(): List<SitePreset> = listOf(
        SitePreset(domainPattern = "youtube.com", preferredQuality = "1080p", preferredStreamType = "VIDEO_AND_AUDIO"),
        SitePreset(domainPattern = "youtu.be", preferredQuality = "1080p", preferredStreamType = "VIDEO_AND_AUDIO"),
        SitePreset(domainPattern = "tiktok.com", preferredQuality = "BEST", preferredStreamType = "VIDEO_AND_AUDIO"),
        SitePreset(domainPattern = "instagram.com", preferredQuality = "BEST", preferredStreamType = "VIDEO_AND_AUDIO"),
        SitePreset(domainPattern = "twitter.com", preferredQuality = "BEST", preferredStreamType = "VIDEO_AND_AUDIO"),
        SitePreset(domainPattern = "x.com", preferredQuality = "BEST", preferredStreamType = "VIDEO_AND_AUDIO"),
    )
}
