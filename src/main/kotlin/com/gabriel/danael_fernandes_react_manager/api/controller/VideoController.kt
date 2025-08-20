package com.gabriel.danael_fernandes_react_manager.api.controller

import com.gabriel.danael_fernandes_react_manager.api.dto.VideoOrderRequestDTO
import com.gabriel.danael_fernandes_react_manager.pay.PixPagamento
import com.gabriel.danael_fernandes_react_manager.service.RuleService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/video")
class VideoController(
    private val ruleService: RuleService
) {

    @GetMapping
    fun saveVideo(@RequestBody videoRquest: VideoOrderRequestDTO): ResponseEntity<PixPagamento>{

        val result = ruleService.applyRules(videoRquest)
        return ResponseEntity.ok(result)
    }
}