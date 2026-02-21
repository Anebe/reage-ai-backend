package com.gabriel.danael_fernandes_react_manager.core.video

import com.gabriel.danael_fernandes_react_manager.database.repository.OAuthCredentialRepository
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class YoutubeAuthorization(
    private val clientId: String,
    private val clientSecret: String,
    private val scopes: List<String>,
    private val redirectUri: String,
    private val credentialRepository: OAuthCredentialRepository,

    ){
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
        //.setApprovalPrompt("force") // Força o usuário a revisar as permissões toda vez (útil em dev)
        .build()

    fun linkAuthorization(): String{
        val authorizationUrl = flow.newAuthorizationUrl()
            .setRedirectUri(redirectUri)
            .setState((1L).toString())
            .build()

        return authorizationUrl
    }

    fun saveCredentials(code: String){
        val credential = credentialRepository.getYoutubeOauth()


        val tokenResponse = flow.newTokenRequest(code)
            .setRedirectUri(redirectUri)
            .execute()

        credential.let {
            it.refreshToken = tokenResponse.refreshToken
            it.accessToken = tokenResponse.accessToken
            it.expiresAtInMiliSeconds = Instant.now().plusSeconds(tokenResponse.expiresInSeconds).toEpochMilli()
        }

        credentialRepository.save(credential)
    }
}
