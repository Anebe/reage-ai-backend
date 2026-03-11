package com.gabriel.danael_fernandes_react_manager.api.controller

import com.gabriel.danael_fernandes_react_manager.api.dto.ContentCreatorRegisterDTO
import com.gabriel.danael_fernandes_react_manager.core.authentication.Authentication
import com.gabriel.danael_fernandes_react_manager.core.authentication.KeycloakAdminService
import com.gabriel.danael_fernandes_react_manager.core.service.FileStorageService
import com.gabriel.danael_fernandes_react_manager.core.video.ManagerVideoPlataform
import com.gabriel.danael_fernandes_react_manager.core.video.YoutubeClient
import com.gabriel.danael_fernandes_react_manager.database.entity.ContentCreator
import com.gabriel.danael_fernandes_react_manager.database.repository.ContentCreatorRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import kotlin.math.log


data class Login(
    val username: String,
    val password: String
)
data class ContentCreatorResponse(
    val id: Long,
    val name: String
)
data class TokenResponse(
    val token: String,
    val user: ContentCreatorResponse
)
@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authentication: Authentication,
    private val youtube: ManagerVideoPlataform,
    private val contentCreatorRepository: ContentCreatorRepository,
    private val file: FileStorageService
) {

    @PostMapping("/register")
    fun register(@RequestBody user: ContentCreatorRegisterDTO): ResponseEntity<Void>{
        //TODO ver os problemas usuarios repetido e tals
        authentication.createUser(user.to())

        var id: String? = null
        kotlin.runCatching {
            youtube.createPlaylist(user.username + " playlist")
        }.onSuccess {
            id = it.playlistId
        }

        val contCreator = ContentCreator(
            username = user.username,
            playlistId = id,
            fullname = user.fullName,
            profilePictureFilename = file.store()
        )
        contentCreatorRepository.save(contCreator)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/login")
    fun login(@RequestBody login: Login): ResponseEntity<TokenResponse>{
        val token = authentication.login(login.username, login.password)
        val contentCreator = contentCreatorRepository.findByUsername(login.username)

        if(contentCreator == null){
            return ResponseEntity.notFound().build()
        }
        val result = TokenResponse(token, ContentCreatorResponse(contentCreator.id, contentCreator.username))
        return ResponseEntity.ok(result)
    }
}