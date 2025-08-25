package com.gabriel.danael_fernandes_react_manager.api.controller

import com.gabriel.danael_fernandes_react_manager.api.dto.CreatorContentResponse
import com.gabriel.danael_fernandes_react_manager.core.authentication.Authentication
import com.gabriel.danael_fernandes_react_manager.database.repository.ContentCreatorRepository
import com.gabriel.danael_fernandes_react_manager.core.authentication.KeycloakAdminService
import com.gabriel.danael_fernandes_react_manager.core.video.YoutubeClient
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal


@RestController
@RequestMapping("/api/content-creator")
class ContentCreaterController(
    private val contentCreatorRepository: ContentCreatorRepository,
    private val youtube: YoutubeClient,
    private val authentication: Authentication,
) {

    @GetMapping("/profile")
    fun getInfo(@AuthenticationPrincipal jwt: Jwt): ResponseEntity<CreatorContentResponse> {
        val username = jwt.claims["preferred_username"] as String

        val contentCreator = contentCreatorRepository.findByUsername(username)
        val userInfo = authentication.findUser(username)

        if (contentCreator == null || userInfo == null)
            return ResponseEntity.notFound().build()

        val result = CreatorContentResponse(userInfo, contentCreator)
        return ResponseEntity.ok(result)
    }

//    @PostMapping
//    fun register(@RequestBody user: ContentCreatorRegisterDTO): ResponseEntity<Void> {
//        keycloak.createUser(user.username, user.email, user.password)
//        val id = youtube.createPlaylist(user.username + " playlist")
//        val contCreator = ContentCreator(
//            username = user.username,
//            playlistId = id.playlistId
//        )
//        contentCreatorRepository.save(contCreator)
//        return ResponseEntity.ok().build()
//    }

//    @GetMapping
//    fun register(@AuthenticationPrincipal principal: OidcUser) {
//        val username = principal.preferredUsername
//        firstAcess(username)
//    }
//    private fun firstAcess(username: String) {
//        val contentCreatorOp = contentCreatorRepository.findByUsername(username)
//
//
//        if(contentCreatorOp == null){
//            val newContentCreator = ContentCreator(
//                username = username,
//                playlistId = "aaaaaaaaaaa"//TODO arrumar o fluxo
//            )
//            contentCreatorRepository.save(newContentCreator)
//        }
//    }
}