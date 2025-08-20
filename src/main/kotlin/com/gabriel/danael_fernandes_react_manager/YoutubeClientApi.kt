package com.gabriel.danael_fernandes_react_manager

import com.gabriel.danael_fernandes_react_manager.database.repository.OAuthCredentialRepository
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.Playlist
import com.google.api.services.youtube.model.PlaylistItem
import com.google.api.services.youtube.model.PlaylistItemSnippet
import com.google.api.services.youtube.model.PlaylistSnippet
import com.google.api.services.youtube.model.ResourceId
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.StringReader
import java.net.URL
import java.time.Duration
import java.time.Instant
import java.util.UUID


data class Video(
    val duration: Duration
)

@Service
class YoutubeClientApi(
    @Value("\${youtube.api-key}")
    private val apiKey: String,
    @Value("\${youtube.client-id}")
    private val clientId: String,
    @Value("\${youtube.client-secret}")
    private val clientSecret: String,
    @Value("\${youtube.scopes}")
    private val scopes: List<String>,
    @Value("\${youtube.redirect-uri}")
    private val redirectUri: String,

    private val oAuthCredentialRepository: OAuthCredentialRepository,
)
{

    private val APPLICATION_NAME = "ReageAi"
    private val JSON_FACTORY = GsonFactory.getDefaultInstance()
    private val HTTP_TRANSPORT = NetHttpTransport()
    private val flow = GoogleAuthorizationCodeFlow.Builder(
        HTTP_TRANSPORT,
        JSON_FACTORY,
        clientId,
        clientSecret,
        scopes
    )
        .setAccessType("offline") // Importante para obter o refresh token
        .setApprovalPrompt("force") // Força o usuário a revisar as permissões toda vez (útil em dev)
        .build()


    private fun youtubeOauth(): YouTube {
        val credentials = oAuthCredentialRepository.findAll()[0]

        if (Instant.ofEpochMilli(credentials.expiresAtInMiliSeconds).isBefore(Instant.now().plusSeconds(60))) {
            refreshTokenForUser()
        }

        val credential = GoogleCredential.Builder()
            .setTransport(HTTP_TRANSPORT)
            .setJsonFactory(JSON_FACTORY)
            .setClientSecrets(clientId, clientSecret)
            .build()
            .setRefreshToken(credentials.refreshToken)
            .setAccessToken(credentials.accessToken)

        credential.refreshToken()

        credentials.accessToken = credential.accessToken
        credentials.expiresAtInMiliSeconds = Instant.now().plusSeconds(credential.expiresInSeconds).toEpochMilli()

        oAuthCredentialRepository.save(credentials)
        return YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
            .setApplicationName(APPLICATION_NAME)
            .build()
    }

    fun saveCredentials(code: String, id: UUID){
        val userOptional = oAuthCredentialRepository.findById(id)

        val tokenResponse = flow.newTokenRequest(code)
            .setRedirectUri(redirectUri)
            .execute()

        userOptional.get().let {
            it.refreshToken = tokenResponse.refreshToken
            it.accessToken = tokenResponse.accessToken
            it.expiresAtInMiliSeconds = Instant.now().plusSeconds(tokenResponse.expiresInSeconds).toEpochMilli()
        }

        oAuthCredentialRepository.save(userOptional.get())
    }
    private fun refreshTokenForUser() {
        val credentials = oAuthCredentialRepository.findAll()[0]

        val credential = GoogleCredential.Builder()
            .setTransport(HTTP_TRANSPORT)
            .setJsonFactory(JSON_FACTORY)
            .setClientSecrets(clientId, clientSecret)
            .build()
            .setRefreshToken(credentials.refreshToken) // Usa apenas o refresh token para atualizar

        try {
            val success = credential.refreshToken()
            if (success) {
                credentials.accessToken = credential.accessToken
                credentials.expiresAtInMiliSeconds = Instant.now().plusSeconds(credential.expiresInSeconds).toEpochMilli()
                oAuthCredentialRepository.save(credentials)
            } else {
                throw RuntimeException("Falha ao obter novo token de acesso com o refresh token para o usuário. Requer re-autorização.")
            }
        } catch (e: Exception) {
            throw RuntimeException("Erro ao atualizar o token do YouTube para o usuário: ${e.message}. Requer re-autorização.", e)
        }
    }

    fun linkAuthorization(state : String): String{
        val authorizationUrl = flow.newAuthorizationUrl()
            .setRedirectUri(redirectUri)
            .setState(state)
            .build()

        return authorizationUrl
    }

    fun addVideoPlaylist(videoId: String, playlistId: String){
        val youtube = youtubeOauth()

        val resourceId = ResourceId().apply {
            kind = "youtube#video"
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
    fun createPlaylist(title: String): String{
        val youtube = youtubeOauth()

        val snippet = PlaylistSnippet().apply {
            this.title = title
        }
        val plalist = Playlist().apply {
            this.snippet = snippet
        }
        val plalistResult = youtube.playlists().insert("snippet", plalist).execute()
        println(plalistResult)
        return plalistResult.id
    }
    fun searchVideo(link: URL): Video {
        val youtube = youtubeApiKey()

        val search = youtube.videos().list("contentDetails")
        search.key = apiKey
        search.id = this.extractVideoId(link)
        search.maxResults = 1
        val response = search.execute()
        val item = response.items[0]
        return Video(Duration.parse(item.contentDetails.duration))
    }


    private fun youtubeApiKey(): YouTube {
        return YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, null)
            .setApplicationName(APPLICATION_NAME)
            .build()
    }


    fun extractVideoId(url: URL): String? {
        val params = url.query.split("&")
            .associate {
            val (key, value) = it.split("=")
            key to value
        }

        return params["v"]
    }
}
