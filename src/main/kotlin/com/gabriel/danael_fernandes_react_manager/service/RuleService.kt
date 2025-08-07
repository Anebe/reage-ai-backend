package com.gabriel.danael_fernandes_react_manager.service

import com.gabriel.danael_fernandes_react_manager.entity.*
import com.gabriel.danael_fernandes_react_manager.entity.rules.*
import com.gabriel.danael_fernandes_react_manager.repository.CustomRuleRepository
import com.gabriel.danael_fernandes_react_manager.repository.StreamerRepository
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.stereotype.Service

@Service
class RuleService(
    private val customRuleRepository: CustomRuleRepository,
    private val streamerRepository: StreamerRepository,
    private val json: Json
) {

    fun save(streamerUsername: String, ruleInterface: RuleInterface): CustomRule {
        val streamer = streamerRepository.findByUsername(streamerUsername)
        requireNotNull(streamer)

        return customRuleRepository.save(
            CustomRule(
                ruleType = ruleInterface.ruleType,
                ruleContent = json.encodeToString<RuleInterface>(ruleInterface),
                user = streamer
            )
        )
    }
    fun applyRules(video: Video, user: Streamer): Double {
        val customRules = customRuleRepository.findAllByUser(user)
        val pipeline = VideoProcessPipeline()

        customRules.forEach { customRuleEntity ->
            val ruleInterface: RuleInterface = json.decodeFromString(customRuleEntity.ruleContent)
            pipeline.addRule(ruleInterface)
        }
        val price = pipeline.process(video)
        return price ?: 0.0
    }
}