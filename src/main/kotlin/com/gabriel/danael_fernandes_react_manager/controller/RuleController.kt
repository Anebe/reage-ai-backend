package com.gabriel.danael_fernandes_react_manager.controller

import com.gabriel.danael_fernandes_react_manager.entity.rules.RuleInterface
import com.gabriel.danael_fernandes_react_manager.entity.rules.Video
import com.gabriel.danael_fernandes_react_manager.repository.StreamerRepository
import com.gabriel.danael_fernandes_react_manager.service.RuleService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

data class VideoRequestDTO(
    val nome: String,
    val link: String,
    val mensagem: String
)
@RestController
@RequestMapping("/rule")
class RuleController(
    @Autowired val ruleService: RuleService,
    private val streamerRepository: StreamerRepository
) {


    @PostMapping
    fun addRule(
        @RequestBody rule: RuleInterface,
        principal: Principal
    ): ResponseEntity<Void> {

        val result = ruleService.save(principal.name, rule)
        return ResponseEntity.ok().build()
    }

    @GetMapping
    @RequestMapping("/{username}")
    fun executeRule(
        @PathVariable username: String,
        @RequestBody video: Video
    ): ResponseEntity<Double>{
        val streamer = streamerRepository.findByUsername(username)
        requireNotNull(streamer)
        val price = ruleService.applyRules(video, streamer)
        return ResponseEntity.ok(price)
    }
}