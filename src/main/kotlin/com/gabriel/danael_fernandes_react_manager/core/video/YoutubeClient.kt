package com.gabriel.danael_fernandes_react_manager.core.video

import com.gabriel.danael_fernandes_react_manager.core.BusinessRuleException
import com.gabriel.danael_fernandes_react_manager.core.ResourceNotFoundException
import com.google.api.services.youtube.model.PlaylistItem
import com.google.api.services.youtube.model.PlaylistItemSnippet
import com.google.api.services.youtube.model.PlaylistSnippet
import com.google.api.services.youtube.model.ResourceId
import com.google.api.services.youtube.model.Video
import org.springframework.stereotype.Component
import java.net.URL
import java.time.Duration

@Component
class YoutubeClient(
    private val youtubeBuilder: YoutubeBuilder
): ManagerVideoPlataform{
    fun addVideoPlaylist(videoId: String, playlistId: String){
        val youtube = youtubeBuilder.youtubeOauth()

        val resourceId = ResourceId().apply {
//            kind = "youtube#video"
            this.videoId = videoId
        }
        val snippet = PlaylistItemSnippet().apply {
            this.playlistId = playlistId
            this.resourceId = resourceId
        }

        val playlistItem = PlaylistItem().apply {
            this.snippet = snippet
        }

        youtube.playlistItems()
            .insert("snippet", playlistItem)
            .execute()
    }

    override fun searchVideo(id: String): VideoInfo {
        val youtube = youtubeBuilder.youtubeOauth()

        val search = youtube.videos().list("contentDetails,snippet")

        search.id = id
        search.maxResults = 1
        val response = search.execute()
        val item = response.items.firstOrNull() ?: throw ResourceNotFoundException("Video", id)

        val category = youtube.videoCategories().list("snippet").apply {
            this.id = item.snippet.categoryId
        }.execute().items.firstOrNull()
        val categoryName = category?.snippet?.title ?: "uncategorized"
        //return VideoInfo(Duration.parse(item.contentDetails.duration))
        return YoutubeUtil.to(item, categoryName)
    }

    override fun createPlaylist(name: String): Playlist {
        val youtube = youtubeBuilder.youtubeOauth()

        val snippet = PlaylistSnippet().apply {
            this.title = name
        }
        val plalist = com.google.api.services.youtube.model.Playlist().apply {
            this.snippet = snippet
        }
        val plalistResult = youtube.playlists().insert("snippet", plalist).execute()

        return PlaylistImp(youtube, plalistResult.id, this)
    }

    override fun searchPlaylist(id: String): Playlist? {
        val youtube = youtubeBuilder.youtubeOauth()

        val request = youtube.playlists().list("id")
        request.id = id
        request.maxResults = 1
        val response = request.execute()
        val playlist = response.items.firstOrNull()

        return if(playlist == null)
            null
        else
            PlaylistImp(youtube, playlist.id, this)
    }

    override fun searchVideo(url: URL): VideoInfo {
        val id = YoutubeUtil.extractVideoId(url) ?: throw BusinessRuleException("link incorreto")
        return this.searchVideo(id)
    }


}
