package com.gabriel.danael_fernandes_react_manager.config

import com.gabriel.danael_fernandes_react_manager.core.video.PlaylistImp
import com.gabriel.danael_fernandes_react_manager.core.video.YoutubeAuthorization
import com.gabriel.danael_fernandes_react_manager.core.video.YoutubeBuilder
import com.gabriel.danael_fernandes_react_manager.database.repository.OAuthCredentialRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class YoutubeConfig(
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

    private val credential: OAuthCredentialRepository,
) {

    @Bean
    fun youtubeBuilder() = YoutubeBuilder(
        credentialRepository = credential,
        clientSecret = clientSecret,
        scopes = scopes,
        clientId = clientId
    )

    @Bean
    fun youtubeAuthorization() = YoutubeAuthorization(
        scopes = scopes,
        credentialRepository = credential,
        clientSecret = clientSecret,
        clientId = clientId,
        redirectUri = redirectUri
    )
}