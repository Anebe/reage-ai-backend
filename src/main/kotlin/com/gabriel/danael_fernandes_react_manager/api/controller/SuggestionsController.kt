package com.gabriel.danael_fernandes_react_manager.api.controller

import com.gabriel.danael_fernandes_react_manager.api.dto.PixResponse
import com.gabriel.danael_fernandes_react_manager.api.dto.VideoOrderRequestDTO
import com.gabriel.danael_fernandes_react_manager.core.ResourceNotFoundException
import com.gabriel.danael_fernandes_react_manager.core.pay.PixPagamento
import com.gabriel.danael_fernandes_react_manager.core.service.RuleService
import com.gabriel.danael_fernandes_react_manager.core.video.ManagerVideoPlataform
import com.gabriel.danael_fernandes_react_manager.core.video.VideoInfo
import com.gabriel.danael_fernandes_react_manager.database.entity.Suggest
import com.gabriel.danael_fernandes_react_manager.database.entity.SuggestStatus
import com.gabriel.danael_fernandes_react_manager.database.repository.ContentCreatorRepository
import com.gabriel.danael_fernandes_react_manager.database.repository.PagamentoRepository
import com.gabriel.danael_fernandes_react_manager.database.repository.SuggestRepository
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*
import java.net.URL
import java.time.LocalDate


data class VideoSugestResponse(
    val id: Long,
    val followerNick: String,
    val message: String,
    val videoUrl: String,
    val videoTitle: String,
    val videoDuration: String,
    val videoThumbnail: String,
    val price: String,
    val status: SuggestStatus,
    val createdAt: LocalDate,
){
    constructor(suggestion: Suggest, video: VideoInfo): this(
        id = suggestion.id,
        followerNick = suggestion.followerNick,
        message = suggestion.followerMessage,
        status = suggestion.status,
        videoUrl = suggestion.link,
        videoTitle = video.videoTitle,
        videoThumbnail = video.videoThumbnailUrl,
        videoDuration = "${video.duration.toMinutes()}:${video.duration.toSecondsPart()}",
        createdAt = suggestion.createdAt,
        price = suggestion.valor.toPlainString()
    )
}

@RestController
@RequestMapping("/api/suggest")
class SuggestionsController(
    private val ruleService: RuleService,
    private val contentCreatorRepository: ContentCreatorRepository,
    private val suggestRespository: SuggestRepository,
    private val videoPlataform: ManagerVideoPlataform,
    private val pagamentoRepository: PagamentoRepository
) {

    /*
    TODO como lidar com fila "cheia"(quando existe
    espaço na fila, mas ele fica cheia por causa de pagamentos pendentes):
    bloquear novas sugestão mas enviar mensagem de
    "fila cheia por causa de pagamentos, tente novamente em X minutos"
     */
    @PostMapping("/{username}")
    fun saveVideo(
        @PathVariable username: String,
        @RequestBody videoRquest: VideoOrderRequestDTO
    ): ResponseEntity<PixResponse> {

        val contentCreator = contentCreatorRepository.findByUsername(username)

        if (contentCreator == null) return ResponseEntity.notFound().build()

        val result = ruleService.applyRules(videoRquest, contentCreator.id)
        return ResponseEntity.ok(PixResponse(result))
    }

    @GetMapping
    fun get(
        @AuthenticationPrincipal jwt: Jwt
    ): ResponseEntity<List<VideoSugestResponse>> {
        val username = jwt.claims["preferred_username"] as String

        val contentCreator = contentCreatorRepository.findByUsername(username)
        if (contentCreator == null) throw ResourceNotFoundException("Criador de conteudo", username)

        val suggestions = suggestRespository.findAllByContentCreator(contentCreator)

        val  suggestionsResponse = suggestions.map {
            val videoInfo = videoPlataform.searchVideo(URL(it.link))
            VideoSugestResponse(it, videoInfo)
        }

        return ResponseEntity.ok(suggestionsResponse)
    }

    @PutMapping("/{id}/status")
    fun updateStatus(
        @PathVariable id: Long,
        @AuthenticationPrincipal jwt: Jwt,
        @RequestBody status: SuggestStatus
    ): ResponseEntity<Void>{

        val suggestOp = suggestRespository.findById(id)
        if(suggestOp.isEmpty) throw ResourceNotFoundException("Sugestão", id)
        val suggestion = suggestOp.get().copy(status = status)
        suggestRespository.save(suggestion)
        return ResponseEntity.ok().build()
    }
}