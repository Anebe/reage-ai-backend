package com.gabriel.danael_fernandes_react_manager.config

import com.gabriel.danael_fernandes_react_manager.core.video.ManagerVideoPlataform
import com.gabriel.danael_fernandes_react_manager.database.repository.ContentCreatorRepository
import com.gabriel.danael_fernandes_react_manager.database.repository.OAuthCredentialRepository
import com.google.api.client.http.GenericUrl
import com.google.api.client.http.HttpRequestFactory
import com.google.api.client.http.UrlEncodedContent
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.util.GenericData
import jakarta.annotation.PreDestroy
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Profile("dev")
@Component
class OnSpringBootExiting(
    private val youtube: ManagerVideoPlataform,
    private val contentCreatorRepository: ContentCreatorRepository,
    private val oAuthCredentialRepository: OAuthCredentialRepository,
) {

    @PreDestroy
    fun deleteAllPlaylist(){
        for (contentCreator in contentCreatorRepository.findAll()){
            contentCreator.playlistId?.let { youtube.deletePlaylist(it) }
        }
    }

    @PreDestroy
    fun revokeToken() {
        val token: String = oAuthCredentialRepository.getYoutubeOauth().refreshToken
        val transport = NetHttpTransport()
        val requestFactory: HttpRequestFactory = transport.createRequestFactory()

        val revokeUrl = GenericUrl("https://oauth2.googleapis.com/revoke")

        val data = GenericData()
        data["token"] = token

        val content = UrlEncodedContent(data)

        val request = requestFactory.buildPostRequest(revokeUrl, content)
        val response = request.execute()

        println("Status: ${response.statusCode}")
    }
}