package com.gabriel.danael_fernandes_react_manager.controller

import com.gabriel.danael_fernandes_react_manager.entity.OAuthCredential
import com.gabriel.danael_fernandes_react_manager.entity.Provider
import com.gabriel.danael_fernandes_react_manager.repository.OAuthCredentialRepository
import com.gabriel.danael_fernandes_react_manager.repository.StreamerRepository
import com.gabriel.danael_fernandes_react_manager.service.JwtUtil
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.io.StringReader
import java.time.Instant

@RestController
class YouTubeOAuthController(
    @Autowired private val streamerRepository: StreamerRepository,
    @Autowired private val ouathCredentialRepository: OAuthCredentialRepository,
    @Autowired private val tokenService: JwtUtil // Para obter o usuário logado
) {

    @Value("\${youtube.oauth2.client-id}")
    private lateinit var clientId: String

    @Value("\${youtube.oauth2.client-secret}")
    private lateinit var clientSecret: String

    @Value("\${youtube.oauth2.redirect-uri}")
    private lateinit var redirectUri: String

    @Value("\${youtube.oauth2.scopes}")
    private lateinit var scopes: List<String>

    private val JSON_FACTORY = GsonFactory.getDefaultInstance()
    private val HTTP_TRANSPORT = NetHttpTransport()

    @GetMapping("/oauth2/authorize/youtube")
    fun authorizeYoutube(authentication: Authentication, response: HttpServletResponse): ResponseEntity<String> {
        val user = authentication.principal
        println(user)
        val clientSecrets = GoogleClientSecrets.load(
            JSON_FACTORY,
            StringReader("{\"web\":{\"client_id\":\"$clientId\",\"client_secret\":\"$clientSecret\"}}")
        )

        val flow = GoogleAuthorizationCodeFlow.Builder(
            HTTP_TRANSPORT,
            JSON_FACTORY,
            clientSecrets,
            scopes
        )
            .setAccessType("offline") // Importante para obter o refresh token
            .setApprovalPrompt("force") // Força o usuário a revisar as permissões toda vez (útil em dev)
            .build()

        val authorizationUrl = flow.newAuthorizationUrl()
            .setRedirectUri(redirectUri)
            .setState(user.toString()) // Passa o ID do seu usuário para o callback (para associar o token a ele)
            .build()

        return ResponseEntity.ok().body(authorizationUrl.toString())
        //response.sendRedirect(authorizationUrl.toString())
    }

    // 2. Endpoint de callback para receber o código de autorização do Google
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

        val username = state.toString()
        val userOptional = streamerRepository.findByUsername(username)

        if (userOptional == null) {
            return ResponseEntity.badRequest().body("Usuário interno não encontrado para o ID: $username")
        }

//        val streamer = streamerRepository.findByLogin(userOptional)
//            ?: return ResponseEntity.badRequest().body("Usuário interno não encontrado para o ID: $username")

//        val authCredential = streamer.youTubeOAuth


        val clientSecrets = GoogleClientSecrets.load(
            JSON_FACTORY,
            StringReader("{\"web\":{\"client_id\":\"$clientId\",\"client_secret\":\"$clientSecret\"}}")
        )

        val flow = GoogleAuthorizationCodeFlow.Builder(
            HTTP_TRANSPORT,
            JSON_FACTORY,
            clientSecrets,
            scopes
        )
            .setAccessType("offline")
            .build()

        try {
            val tokenResponse: GoogleTokenResponse = flow.newTokenRequest(code)
                .setRedirectUri(redirectUri)
                .execute()

            if (userOptional.youTubeOAuth == null) {


                val authCredential = OAuthCredential(
                    refreshToken = tokenResponse.refreshToken,
                    accessToken = tokenResponse.accessToken,
                    type = Provider.YOUTUBE,
                    expiresAtInMiliSeconds = Instant.now().plusSeconds(tokenResponse.expiresInSeconds).toEpochMilli(),
                )
//            authCredential.accessToken = tokenResponse.accessToken
//            authCredential.refreshToken = tokenResponse.refreshToken // O refresh token só vem na primeira vez!
//            authCredential.expiresAtInMiliSeconds = Instant.now().plusSeconds(tokenResponse.expiresInSeconds).toEpochMilli()

                userOptional.youTubeOAuth = authCredential

                ouathCredentialRepository.save(authCredential) // Salva os tokens no seu banco de dados
                streamerRepository.save(userOptional)

                return ResponseEntity.ok("Autorização do YouTube concluída com sucesso! Tokens salvos.")
            }
            return ResponseEntity.badRequest().body("Erro Interno")
        } catch (e: Exception) {
            e.printStackTrace()
            return ResponseEntity.internalServerError().body("Erro ao trocar código de autorização por tokens: ${e.message}")
        }
    }
}