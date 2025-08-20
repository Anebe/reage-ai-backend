package com.gabriel.danael_fernandes_react_manager.api.controller

import com.gabriel.danael_fernandes_react_manager.YoutubeClientApi
import com.gabriel.danael_fernandes_react_manager.database.entity.ContentCreator
import com.gabriel.danael_fernandes_react_manager.database.repository.ContentCreatorRepository
import com.gabriel.danael_fernandes_react_manager.service.KeycloakAdminService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class ContentCreatorRegisterDTO(
    val email: String,
    val password: String,
    val username: String,
    val firstName: String,
    val lastName: String,
)
@RestController
@RequestMapping("/content-creator")
class ContentCreaterController(
    private val contentCreatorRepository: ContentCreatorRepository,
    private val youtube: YoutubeClientApi,
    private val keycloak: KeycloakAdminService,
) {

    @PostMapping
    fun register(@RequestBody user: ContentCreatorRegisterDTO): ResponseEntity<Void> {
        keycloak.createUser(user.username, user.email, user.password)
        val id = youtube.createPlaylist(user.username + " playlist")
        val contCreator = ContentCreator(
            username = user.username,
            playlistId = id
        )
        contentCreatorRepository.save(contCreator)
        return ResponseEntity.ok().build()
    }

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