package com.gabriel.danael_fernandes_react_manager.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.gabriel.danael_fernandes_react_manager.YoutubeClientApi
import com.gabriel.danael_fernandes_react_manager.api.dto.VideoOrderRequestDTO
import com.gabriel.danael_fernandes_react_manager.database.repository.CustomRuleRepository
import com.gabriel.danael_fernandes_react_manager.database.repository.ContentCreatorRepository
import com.gabriel.danael_fernandes_react_manager.database.entity.ContentCreator
import com.gabriel.danael_fernandes_react_manager.database.entity.CustomRule
import com.gabriel.danael_fernandes_react_manager.database.entity.VideoOrder
import com.gabriel.danael_fernandes_react_manager.database.repository.VideoOrderRepository
import com.gabriel.danael_fernandes_react_manager.pay.*
import com.gabriel.danael_fernandes_react_manager.video.RuleException
import com.gabriel.danael_fernandes_react_manager.video.rule.RuleInterface
import com.gabriel.danael_fernandes_react_manager.video.VideoProcessPipeline
import jakarta.transaction.Transactional
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDateTime

@Service
class RuleService(
    private val customRuleRepository: CustomRuleRepository,
    private val contentCreatorRepository: ContentCreatorRepository,
    private val videoOrderRepository: VideoOrderRepository,
    private val json: Json,
    private val youtubeClientApi: YoutubeClientApi,
    private val payment: Payment
) {

    fun save(contentCreatorId: Long, rules: List<RuleInterface>): List<CustomRule> {
        val streamerOp = contentCreatorRepository.findById(contentCreatorId)
        requireNotNull(streamerOp.isPresent)

        return this.save(streamerOp.get(), rules)
    }

    fun save(streamerUsername: String, ruleInterface: RuleInterface): CustomRule {
        val streamer = contentCreatorRepository.findByUsername(streamerUsername)
        requireNotNull(streamer)

        return this.save(streamer, listOf(ruleInterface))[0]
    }

    @Transactional
    fun save(contentCreator: ContentCreator, rules: List<RuleInterface>): List<CustomRule> {
        val rulesConverted = rules.map {
            CustomRule(
                ruleType = it.ruleType,
                ruleContent = json.encodeToString<RuleInterface>(it),
                user = contentCreator
            )
        }.toList()
        return customRuleRepository.saveAll(rulesConverted)
    }

//    fun applyRules(video: Video, user: Streamer): Double {
//        val customRules = customRuleRepository.findAllByUser(user)
//        val pipeline = VideoProcessPipeline()
//
//        customRules.forEach { customRuleEntity ->
//            val ruleInterface: RuleInterface = json.decodeFromString(customRuleEntity.ruleContent)
//            pipeline.addRule(ruleInterface)
//        }
//        val price = pipeline.process(video)
//
//        requireNotNull(price)
//
//        return price
//    }

    fun applyRules(videoOrderRequest: VideoOrderRequestDTO): PixPagamento {
        val contentCreator = contentCreatorRepository.findById(videoOrderRequest.contentCreatorId)
        require(contentCreator.isPresent)

        val customRules = customRuleRepository.findAllByUser(contentCreator.get())

        val pipeline = VideoProcessPipeline()
        customRules.forEach { customRuleEntity ->
            val ruleInterface: RuleInterface = json.decodeFromString(customRuleEntity.ruleContent)
            pipeline.addRule(ruleInterface)
        }

        val video = youtubeClientApi.searchVideo(videoOrderRequest.link)
        val resultVideoProcess = pipeline.process(video)

        if(resultVideoProcess.errors.isNotEmpty()){
            var msg = ""
            resultVideoProcess.errors.forEach { msg = "$it\n" }
            throw RuleException(msg)
        }

        val price = resultVideoProcess.price
        val charge = payment.charge(price)

        val videoId = youtubeClientApi.extractVideoId(videoOrderRequest.link)
        videoOrderRepository.save(
            VideoOrder(
                link = videoId ?: "",
                contentCreator = contentCreator.get(),
                txId = charge.txId
            )
        )
        return charge
    }
}