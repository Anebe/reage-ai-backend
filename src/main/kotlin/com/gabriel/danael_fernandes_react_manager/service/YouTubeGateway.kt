package com.gabriel.danael_fernandes_react_manager.service

import com.gabriel.danael_fernandes_react_manager.entity.OAuthCredential
import com.gabriel.danael_fernandes_react_manager.repository.OAuthCredentialRepository
import com.gabriel.danael_fernandes_react_manager.repository.StreamerRepository
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.ChannelListResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class YouTubeGateway(
    @Autowired private val streamerRepository: StreamerRepository,
    @Autowired private val oauthCredentialRepository: OAuthCredentialRepository
) {

    @Value("\${youtube.oauth2.client-id}")
    private lateinit var clientId: String

    @Value("\${youtube.oauth2.client-secret}")
    private lateinit var clientSecret: String

    private val APPLICATION_NAME = "ReagiAi"
    private val JSON_FACTORY = GsonFactory.getDefaultInstance()
    private val HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport()


    private fun getYouTubeService(user: OAuthCredential): YouTube {
        if (Instant.ofEpochMilli(user.expiresAtInMiliSeconds).isBefore(Instant.now().plusSeconds(60))) {
            refreshTokenForUser(user)
        }

        val credential = GoogleCredential.Builder()
            .setTransport(HTTP_TRANSPORT)
            .setJsonFactory(JSON_FACTORY)
            .setClientSecrets(clientId, clientSecret)
            .build()
            .setAccessToken(user.accessToken)
            .setRefreshToken(user.refreshToken)

        // Se o refresh token não foi obtido (ex: na primeira autorização, se setAccessType("offline") não foi usado)
        // ou se ele falhar ao atualizar, a próxima linha lançará uma exceção ou retornará um token nulo.
        // É importante que o fluxo de autorização inicial tenha setAccessType("offline") e approvalPrompt("force")
        // para garantir o refresh token na primeira vez.
        credential.refreshToken() // Tenta atualizar o token de acesso se necessário

        // Atualiza o usuário no banco de dados com o novo token de acesso e tempo de expiração
        user.accessToken = credential.accessToken
        user.expiresAtInMiliSeconds = Instant.now().plusSeconds(credential.expiresInSeconds).toEpochMilli()
        oauthCredentialRepository.save(user) // Salva o usuário com o token atualizado

        return YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
            .setApplicationName(APPLICATION_NAME)
            .build()
    }

    // Método auxiliar para forçar a atualização do token
    private fun refreshTokenForUser(user: OAuthCredential) {
        val credential = GoogleCredential.Builder()
            .setTransport(HTTP_TRANSPORT)
            .setJsonFactory(JSON_FACTORY)
            .setClientSecrets(clientId, clientSecret)
            .build()
            .setRefreshToken(user.refreshToken) // Usa apenas o refresh token para atualizar

        try {
            val success = credential.refreshToken()
            if (success) {
                user.accessToken = credential.accessToken
                user.expiresAtInMiliSeconds = Instant.now().plusSeconds(credential.expiresInSeconds).toEpochMilli()
                oauthCredentialRepository.save(user) // Salva o usuário com o token atualizado
            } else {
                throw RuntimeException("Falha ao obter novo token de acesso com o refresh token para o usuário. Requer re-autorização.")
            }
        } catch (e: Exception) {
            throw RuntimeException("Erro ao atualizar o token do YouTube para o usuário: ${e.message}. Requer re-autorização.", e)
        }
    }

    // Exemplo de uso: buscar os canais do usuário logado
    fun listMyYouTubeChannels(user: OAuthCredential): ChannelListResponse {
        val youtubeService = getYouTubeService(user)
        val request = youtubeService.channels().list("snippet,contentDetails,statistics")
            .setMine(true) // Pega os canais do usuário autenticado
            .setMaxResults(10L) // Limita a 10 resultados
        return request.execute()
    }
}