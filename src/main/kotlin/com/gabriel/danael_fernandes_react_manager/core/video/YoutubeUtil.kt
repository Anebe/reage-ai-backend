package com.gabriel.danael_fernandes_react_manager.core.video

import com.google.api.services.youtube.model.Video
import java.net.URL
import java.time.Duration

object YoutubeUtil{
    fun extractVideoId(url: URL): String? {
        val params = url.query.split("&")
            .associate {
                val (key, value) = it.split("=")
                key to value
            }

        return params["v"]
    }


    fun to(videoGoogle: Video, categoryName: String) = VideoInfo(
        isPortugueseAudio = videoGoogle.snippet.defaultLanguage == "pt-PT" || videoGoogle.snippet.defaultLanguage == "pt-BR",
        videoThumbnailUrl = videoGoogle.snippet.thumbnails.default.url,
        tags = videoGoogle.snippet.tags,
        categoria = categoryName,
        hasCaption = videoGoogle.contentDetails.caption == "true",
        videoTitle = videoGoogle.snippet.title,
        duration = Duration.parse(videoGoogle.contentDetails.duration),
        videoUrl = "https://www.youtube.com/watch?v=" + videoGoogle.id,
        channelId = videoGoogle.snippet.channelId
    )
}