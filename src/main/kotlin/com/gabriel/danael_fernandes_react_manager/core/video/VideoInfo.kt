package com.gabriel.danael_fernandes_react_manager.core.video

import java.time.Duration
import java.time.LocalTime

data class VideoInfo(
    val duration: Duration,
    val videoUrl: String,
    val videoTitle: String,
    val videoThumbnailUrl: String,
    val hasCaption: Boolean,
    val tags: List<String>,
    val categoria: String,
    val isPortugueseAudio: Boolean,
    val channelId: String

)
