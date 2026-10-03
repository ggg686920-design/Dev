package com.localdownloader.utils

object TemplateSanitizer {
    private val ILLEGAL_CHARS = Regex("""[/\\:*?"<>|]""")

    fun sanitizeFilename(input: String): String {
        return input.replace(ILLEGAL_CHARS, "_")
            .trim()
            .take(180)
    }

    fun convertFriendlyTemplateToYtDlp(friendly: String): String {
        return friendly
            .replace("{title}", "%(title)s")
            .replace("{channel}", "%(uploader)s")
            .replace("{uploader}", "%(uploader)s")
            .replace("{date}", "%(upload_date)s")
            .replace("{id}", "%(id)s")
            .replace("{quality}", "%(resolution)s")
            .replace("{extension}", "%(ext)s")
    }

    fun convertYtDlpTemplateToFriendly(ytdlp: String): String {
        return ytdlp
            .replace("%(title)s", "{title}")
            .replace("%(uploader)s", "{channel}")
            .replace("%(upload_date)s", "{date}")
            .replace("%(id)s", "{id}")
            .replace("%(resolution)s", "{quality}")
            .replace("%(ext)s", "{extension}")
    }

    fun previewTemplate(template: String, title: String = "Amazing Video", channel: String = "Media Creator", ext: String = "mp4"): String {
        val friendly = convertYtDlpTemplateToFriendly(template)
        val preview = friendly
            .replace("{title}", title)
            .replace("{channel}", channel)
            .replace("{date}", "20261002")
            .replace("{id}", "xyz123")
            .replace("{quality}", "1080p")
            .replace("{extension}", ext)
        return sanitizeFilename(preview)
    }
}
