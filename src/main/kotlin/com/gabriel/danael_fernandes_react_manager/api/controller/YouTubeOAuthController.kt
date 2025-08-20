package com.gabriel.danael_fernandes_react_manager.api.controller

import com.gabriel.danael_fernandes_react_manager.YoutubeClientApi
import com.gabriel.danael_fernandes_react_manager.database.entity.OAuthCredential
import com.gabriel.danael_fernandes_react_manager.database.entity.Provider
import com.gabriel.danael_fernandes_react_manager.database.repository.ContentCreatorRepository
import com.gabriel.danael_fernandes_react_manager.database.repository.OAuthCredentialRepository
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.io.StringReader
import java.time.Instant
import java.util.UUID


@RestController
class YouTubeOAuthController(
    private val youtube: YoutubeClientApi
) {

    @GetMapping("/oauth2/callback/youtube")
    fun oauth2Callback(
        @RequestParam code: String?,
        @RequestParam state: String?, // Seu user ID, que passamos no 'state'
        @RequestParam error: String?
    ): ResponseEntity<String> {
        if (error != null) {
            return ResponseEntity.badRequest().body("Erro na autorização do YouTube: $error")
        }
        if (code == null) {
            return ResponseEntity.badRequest().body("Código de autorização não recebido.")
        }
        if (state == null) {
            return ResponseEntity.badRequest().body("Estado (user ID) não recebido. Não é possível associar o token.")
        }

        val id = UUID.fromString(state)
        youtube.saveCredentials(code, id)
        return ResponseEntity.ok("Autorização do YouTube concluída com sucesso! Tokens salvos.")
    }
}