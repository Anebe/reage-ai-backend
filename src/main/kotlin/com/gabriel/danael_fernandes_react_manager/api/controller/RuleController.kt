package com.gabriel.danael_fernandes_react_manager.api.controller

import com.gabriel.danael_fernandes_react_manager.YoutubeClientApi
import com.gabriel.danael_fernandes_react_manager.api.dto.RuleRequestDTO
import com.gabriel.danael_fernandes_react_manager.database.repository.ContentCreatorRepository
import com.gabriel.danael_fernandes_react_manager.database.repository.VideoOrderRepository
import com.gabriel.danael_fernandes_react_manager.service.RuleService
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/rule")
class RuleController(
    private val ruleService: RuleService,
    private val contentCreatorRepository: ContentCreatorRepository,
    private val videoOrderRepository: VideoOrderRepository,
    private val json: Json,
    private val youtubeClientApi: YoutubeClientApi
) {

    @PostMapping
    fun addRule(
        @RequestBody ruleRequest: RuleRequestDTO,
    ): ResponseEntity<Void> {
        ruleService.save(ruleRequest.contentCreatorId, ruleRequest.rules.map { it.to() })
        return ResponseEntity.ok().build()
    }

    //TODO ao confirmar pagamento adicionar video a fila
    @PostMapping("/webhook/pix")
    fun pixRecebido(@RequestBody str: String): ResponseEntity<Void>{
        println(str)
        val payReceive = json.parseToJsonElement(str).jsonObject
        val txIds = payReceive["pix"]?.jsonArray?.map {
            it.jsonObject["txid"]?.jsonPrimitive?.content ?: ""
        }?: emptyList()

        if(txIds.isNotEmpty()) {
            txIds.forEach{txId ->
                val videoOrderOP = videoOrderRepository.findByTxId(txId)
                val playlist = videoOrderOP.get().contentCreator.playlistId
                val video = videoOrderOP.get().link
                youtubeClientApi.addVideoPlaylist(video, playlist)

            }
        }
        return ResponseEntity.ok().build()
    }
}