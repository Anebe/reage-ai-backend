package com.gabriel.danael_fernandes_react_manager.core.video

import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.PlaylistItem
import com.google.api.services.youtube.model.PlaylistItemSnippet
import com.google.api.services.youtube.model.ResourceId
import java.net.URL
import java.time.Duration

interface ManagerVideoPlataform {

    fun searchVideo(id: String): VideoInfo
    fun searchVideo(url: URL): VideoInfo
    fun createPlaylist(name: String): Playlist
    fun searchPlaylist(id: String): Playlist?
    fun deletePlaylist(id: String)
}

interface Playlist {
    val playlistId: String
    fun addVideo(videoId: String)

    fun removeVideo(videoId: String)

    fun changeName(name: String)

    fun changeIndex(videoId: String, newIndex: Int)

    fun getVideos(): List<VideoInfo>
}

class PlaylistImp(
    private val youtube: YouTube,
    override val playlistId: String,
    private val youtubeClient: YoutubeClient
) : Playlist {

    override fun addVideo(videoId: String) {

        val resourceId = ResourceId().apply {
            kind = "youtube#video"
            this.videoId = videoId
        }
        val snippet = PlaylistItemSnippet()
        snippet.playlistId = playlistId
        snippet.resourceId = resourceId

        val playlistItem = PlaylistItem().apply {
            this.snippet = snippet
        }

        youtube.playlistItems()
            .insert("snippet", playlistItem)
            .execute()
    }

    override fun removeVideo(videoId: String) {
        TODO("Not yet implemented")
    }

    override fun changeName(name: String) {
        TODO("Not yet implemented")
    }

    override fun changeIndex(videoId: String, newIndex: Int) {
        TODO("Not yet implemented")
    }

    override fun getVideos(): List<VideoInfo> {
        val playlist = youtube.playlistItems().list("contentDetails")

        playlist.playlistId = playlistId
        val response = playlist.execute()

        var videosId = ""
        response.items.forEach {
            videosId += it.contentDetails.videoId + ","
        }
        videosId = videosId.dropLast(1)

        val videos = youtube.videos().list("contentDetails")
        videos.id = videosId
        val responseVideos = videos.execute()

        val result: MutableList<VideoInfo> = mutableListOf()
        responseVideos.items.forEach {

            result.add(youtubeClient.searchVideo(it.id))
        }

        return result
    }
}


