package com.gabriel.danael_fernandes_react_manager.api.controller

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.gabriel.danael_fernandes_react_manager.api.dto.rules.RuleInterfaceRequest
import com.gabriel.danael_fernandes_react_manager.api.dto.rules.RuleRequestDTO
import com.gabriel.danael_fernandes_react_manager.database.repository.ContentCreatorRepository
import com.gabriel.danael_fernandes_react_manager.core.service.RuleService
import com.gabriel.danael_fernandes_react_manager.database.repository.CustomRuleRepository
import kotlinx.serialization.json.Json
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,      // Usa um nome simples como identificador
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"       // Campo no JSON que indica o tipo
)
@JsonSubTypes(
    JsonSubTypes.Type(value = RangeDurationResponse::class, name = "range_duration"),
    JsonSubTypes.Type(value = FixedPriceResponse::class, name = "fixed_price")
)
interface RuleResponse {
    val id: Long
}

data class RangeDurationResponse(
    override val id: Long,
    val maxDurationInMinutes: Int,
    val minDurationInMinutes: Int
) : RuleResponse {
}

data class FixedPriceResponse(
    override val id: Long,
    val price: String,
) : RuleResponse {
}

@RestController
@RequestMapping("/api/rule")
class RuleController(
    private val ruleService: RuleService,
    private val contentCreatorRepository: ContentCreatorRepository,
    private val ruleRepository: CustomRuleRepository,
    private val json: Json
) {

    @PostMapping
    fun addRule(
        @RequestBody ruleRequest: RuleRequestDTO,
        @AuthenticationPrincipal jwt: Jwt
    ): ResponseEntity<Void> {

        val username = jwt.claims["preferred_username"] as String
        val creator = contentCreatorRepository.findByUsername(username)

        if (creator == null)
            return ResponseEntity.notFound().build()

        val result = ruleService.save(creator, ruleRequest.rules.map { it.to() })
        return ResponseEntity.ok().build()
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @RequestBody ruleRequest: RuleInterfaceRequest,
        @AuthenticationPrincipal jwt: Jwt
    ): ResponseEntity<Void> {

        val username = jwt.claims["preferred_username"] as String
        val creator = contentCreatorRepository.findByUsername(username)

        if (creator == null)
            return ResponseEntity.notFound().build()

        val result = ruleService.update(id, ruleRequest.to())
        return ResponseEntity.ok().build()
    }

    @GetMapping
    fun getRules(
        @AuthenticationPrincipal jwt: Jwt
    ): ResponseEntity<List<JsonNode>> {
        val username = jwt.claims["preferred_username"] as String

        val contentCreator = contentCreatorRepository.findByUsername(username)

        if (contentCreator == null)
            return ResponseEntity.notFound().build()

        val rules = ruleRepository.findAllByUser(contentCreator)

        val result = mutableListOf<JsonNode>()

        val objectMapper = ObjectMapper().registerModule(KotlinModule.Builder().build())

        rules.forEach {
            val jsonNode = objectMapper.readTree(it.ruleContent)
            (jsonNode as ObjectNode).put("id", it.id)
            val novoJsonString = objectMapper.writeValueAsString(jsonNode)

            result.add(jsonNode)
        }

        return ResponseEntity.ok(result)
    }


}


