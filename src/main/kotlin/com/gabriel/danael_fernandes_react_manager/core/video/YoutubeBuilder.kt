package com.gabriel.danael_fernandes_react_manager.core.video

import com.gabriel.danael_fernandes_react_manager.database.repository.OAuthCredentialRepository
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.youtube.YouTube
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class YoutubeBuilder(
    private val credentialRepository: OAuthCredentialRepository,
    private val clientId: String,
    private val clientSecret: String,
    private val scopes: List<String>
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
//        .setApprovalPrompt("force") // Força o usuário a revisar as permissões toda vez (útil em dev)
        .build()
    fun youtubeOauth(): YouTube {
        val credentialEntity = credentialRepository.findAll().firstOrNull()

        requireNotNull(credentialEntity)

        if (Instant.ofEpochMilli(credentialEntity.expiresAtInMiliSeconds).isBefore(Instant.now().plusSeconds(60))) {
            refreshTokenForUser()
        }

        val credential = GoogleCredential.Builder()
            .setTransport(HTTP_TRANSPORT)
            .setJsonFactory(JSON_FACTORY)
            .setClientSecrets(clientId, clientSecret)
            .build()
            .setRefreshToken(credentialEntity.refreshToken)
            .setAccessToken(credentialEntity.accessToken)

        credential.refreshToken()

        credentialEntity.accessToken = credential.accessToken
        credentialEntity.expiresAtInMiliSeconds = Instant.now().plusSeconds(credential.expiresInSeconds).toEpochMilli()

        credentialRepository.save(credentialEntity)
        return YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
            .setApplicationName(APPLICATION_NAME)
            .build()
    }

    fun youtubeApiKey(): YouTube {
        return YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, null)
            .setApplicationName(APPLICATION_NAME)
            .build()
    }

    private fun refreshTokenForUser() {
        val credentialEntity = credentialRepository.findAll().firstOrNull()

        requireNotNull(credentialEntity)

        val credential = GoogleCredential.Builder()
            .setTransport(HTTP_TRANSPORT)
            .setJsonFactory(JSON_FACTORY)
            .setClientSecrets(clientId, clientSecret)
            .build()
            .setRefreshToken(credentialEntity.refreshToken) // Usa apenas o refresh token para atualizar

        try {
            val success = credential.refreshToken()
            if (success) {
                credentialEntity.accessToken = credential.accessToken
                credentialEntity.expiresAtInMiliSeconds = Instant.now().plusSeconds(credential.expiresInSeconds).toEpochMilli()
                credentialRepository.save(credentialEntity)
            } else {
                throw RuntimeException("Falha ao obter novo token de acesso com o refresh token para o usuário. Requer re-autorização.")
            }
        } catch (e: Exception) {
            throw RuntimeException("Erro ao atualizar o token do YouTube para o usuário: ${e.message}.", e)
        }
    }
}
