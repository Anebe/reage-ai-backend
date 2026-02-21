package com.gabriel.danael_fernandes_react_manager.api.controller

import com.gabriel.danael_fernandes_react_manager.core.video.YoutubeBuilder
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.net.URI
import java.util.UUID


@RestController
class YouTubeOAuthController(
    private val youtube: YoutubeBuilder
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
        //TODO decidir uma maneira de produzir o state
//        if (state == null) {
//            return ResponseEntity.badRequest().body("Estado (user ID) não recebido. Não é possível associar o token.")
//        }

        youtube.saveCredentials(code)
        return ResponseEntity.ok("Autorização do YouTube concluída com sucesso! Tokens salvos.")
    }

    @GetMapping("/oauth2/admin-permission")
    fun adminPermission(): ResponseEntity<Void>{
        var url = youtube.linkAuthorization()

        val headers = HttpHeaders()
        headers.location = URI.create(url)

        return ResponseEntity
            .status(HttpStatus.FOUND) // 302
            .headers(headers)
            .build()
    }
}