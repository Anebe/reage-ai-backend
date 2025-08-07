package com.gabriel.danael_fernandes_react_manager

import com.gabriel.danael_fernandes_react_manager.repository.OAuthCredentialRepository
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.youtube.YouTube
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.net.URI
import java.time.Duration


data class Playlist(
    val size: Int = 0,
    val totalDuration: Duration = Duration.ZERO
)

data class Video(
    val duration: Duration
)

@Service
class YoutubeGateway(
    @Value("\${youtube.api-key}") private val apiKey: String
) {
    @Autowired
    private lateinit var credentialRepository: OAuthCredentialRepository

//    init {
//        val credentialEntity = credentialRepository.findByIdOrNull()
//        requireNotNull(credentialEntity)
//        //TODO arrumar pra fazer os dois youtube um pelo oauth e outro pela chave api
//        val credential = GoogleCredential().setAccessToken(credentialEntity.accessToken)
//        val youtube = YouTube.Builder(
//            NetHttpTransport(),
//            JacksonFactory.getDefaultInstance(),
//            credential
//        )
//            .setApplicationName("SuaApp")
//            .build()
//    }

    fun searchVideo(link: String): Video {
        val youtube = createYoutubeWithApiKey()

        val search = youtube.videos().list("contentDetails")
        search.key = apiKey
        search.id = this.extractVideoId(link)
        search.maxResults = 1
        val response = search.execute()
        val item = response.items[0]
        return Video(Duration.parse(item.contentDetails.duration))
    }


//    fun createPlaylist(name: String) {
//        val youTube = createYoutubeWithOAuth()
//    }

    private fun createYoutubeWithApiKey(): YouTube {
        return YouTube.Builder(NetHttpTransport(), JacksonFactory.getDefaultInstance(), null)
            .setApplicationName("MinhaApp")
            .build()
    }

//    private fun createYoutubeWithOAuth(): YouTube {
//        val credentialEntity = credentialRepository.findByIdOrNull()
//        requireNotNull(credentialEntity)
//        //TODO arrumar pra fazer os dois youtube um pelo oauth e outro pela chave api
//        val credential = GoogleCredential().setAccessToken(credentialEntity.accessToken)
//        return YouTube.Builder(
//            NetHttpTransport(),
//            JacksonFactory.getDefaultInstance(),
//            credential
//        )
//            .setApplicationName("SuaApp")
//            .build()
//
//    }

    private fun extractVideoId(url: String): String? {
        val uri = URI(url)
        val query = uri.query ?: return null

        return query.split("&")
            .map { it.split("=") }
            .firstOrNull { it.first() == "v" }
            ?.getOrNull(1)
    }


}
