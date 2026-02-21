package com.gabriel.danael_fernandes_react_manager.api.controller

import com.fasterxml.jackson.databind.JsonNode
import com.gabriel.danael_fernandes_react_manager.api.dto.CreatorContentResponse
import com.gabriel.danael_fernandes_react_manager.core.BadRequestException
import com.gabriel.danael_fernandes_react_manager.core.ResourceNotFoundException
import com.gabriel.danael_fernandes_react_manager.core.authentication.Authentication
import com.gabriel.danael_fernandes_react_manager.database.repository.ContentCreatorRepository
import com.gabriel.danael_fernandes_react_manager.core.authentication.KeycloakAdminService
import com.gabriel.danael_fernandes_react_manager.core.service.FileStorageService
import com.gabriel.danael_fernandes_react_manager.core.video.YoutubeClient
import com.gabriel.danael_fernandes_react_manager.database.entity.ContentCreator
import jakarta.ws.rs.PathParam
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.security.Principal
import java.util.UUID

data class CreatorContentPublicInfoRequest(
    val displayName: String?,
    val message: String?,
)
data class CreatorContentPublicInfo(
    val username: String,
    val displayName: String,
    val message: String,
    val pictureUrl: String,
){
    constructor(contentCreator: ContentCreator): this(
        username = contentCreator.username,
        message = contentCreator.message,
        displayName = contentCreator.displayName,
        pictureUrl = "http://localhost:8080/pictures/${contentCreator.profilePictureFilename}"

    )
}

@RestController
@RequestMapping("/api/content-creator")
class ContentCreaterController(
    private val contentCreatorRepository: ContentCreatorRepository,
    private val youtube: YoutubeClient,
    private val fileStorageService: FileStorageService,
    private val authentication: Authentication,
) {

    @PatchMapping("/profile/picture")
    fun updatePic(
        @RequestParam("file") file: MultipartFile?,
        @AuthenticationPrincipal jwt: Jwt
    ):ResponseEntity<Void>{


        val username = jwt.claims["preferred_username"] as String
        val contentCreator = contentCreatorRepository.findByUsername(username)

        contentCreator ?: throw ResourceNotFoundException("Criador de conteúdo", username)

        var filename:String? = null
        filename = file?.let { fileStorageService.store(file) }

        val newContentCreator = contentCreator.copy(
            profilePictureFilename = filename ?: contentCreator.profilePictureFilename
        )

        if (contentCreator == newContentCreator)
            throw BadRequestException("Não houve mudança")

        contentCreatorRepository.save(newContentCreator)
        return ResponseEntity.ok().build()
    }

    @PatchMapping("/profile")
    fun updatePublicInfo(
        @RequestBody info: CreatorContentPublicInfoRequest,
        @AuthenticationPrincipal jwt: Jwt
    ):ResponseEntity<Void>{


        val username = jwt.claims["preferred_username"] as String
        val contentCreator = contentCreatorRepository.findByUsername(username)

        contentCreator ?: throw ResourceNotFoundException("Criador de conteúdo", username)

        val newContentCreator = contentCreator.copy(
            displayName = info.displayName ?: contentCreator.displayName,
            message =  info.message ?: contentCreator.message,
        )

        if (contentCreator == newContentCreator)
            throw BadRequestException("Não houve mudança")

        contentCreatorRepository.save(newContentCreator)
        return ResponseEntity.ok().build()
    }
    @GetMapping("/{username}/profile")
    fun getPublicInfo(@PathVariable username: String): ResponseEntity<CreatorContentPublicInfo>{
        val contentCreator = contentCreatorRepository.findByUsername(username)
        contentCreator ?: throw ResourceNotFoundException("Criador de conteúdo", username)

        return ResponseEntity.ok(CreatorContentPublicInfo(contentCreator))
    }

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

}