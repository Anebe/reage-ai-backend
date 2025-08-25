package com.gabriel.danael_fernandes_react_manager.core.service

import com.gabriel.danael_fernandes_react_manager.api.dto.VideoOrderRequestDTO
import com.gabriel.danael_fernandes_react_manager.core.BusinessRuleException
import com.gabriel.danael_fernandes_react_manager.core.ResourceNotFoundException
import com.gabriel.danael_fernandes_react_manager.core.pay.Payment
import com.gabriel.danael_fernandes_react_manager.core.pay.PixPagamento
import com.gabriel.danael_fernandes_react_manager.database.repository.CustomRuleRepository
import com.gabriel.danael_fernandes_react_manager.database.repository.ContentCreatorRepository
import com.gabriel.danael_fernandes_react_manager.database.repository.SuggestRepository
import com.gabriel.danael_fernandes_react_manager.core.rule_processor.RuleException
import com.gabriel.danael_fernandes_react_manager.core.rule_processor.rule.RuleInterface
import com.gabriel.danael_fernandes_react_manager.core.rule_processor.VideoProcessPipeline
import com.gabriel.danael_fernandes_react_manager.core.video.YoutubeClient
import com.gabriel.danael_fernandes_react_manager.core.video.YoutubeUtil
import com.gabriel.danael_fernandes_react_manager.database.entity.*
import com.gabriel.danael_fernandes_react_manager.database.repository.PagamentoRepository
import jakarta.transaction.Transactional
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class RuleService(
    private val customRuleRepository: CustomRuleRepository,
    private val contentCreatorRepository: ContentCreatorRepository,
    private val suggestRepository: SuggestRepository,
    private val pagamentoRepository: PagamentoRepository,
    private val json: Json,
    private val youtubeClientApi: YoutubeClient,
    private val payment: Payment
) {

    fun update(ruleId: Long, newRule: RuleInterface): CustomRule{
        val oldRule = customRuleRepository.findById(ruleId)

        if(oldRule.isEmpty) throw ResourceNotFoundException("regra", ruleId)

        val newCustomRule = oldRule.get().copy(
            ruleContent = json.encodeToString<RuleInterface>(newRule),
            ruleType = newRule.ruleType
        )
        return customRuleRepository.save(newCustomRule)
    }
    fun save(contentCreator: ContentCreator, rules: List<RuleInterface>): List<CustomRule> {
        val existingRules = customRuleRepository.findAllByUser(contentCreator)

        existingRules.forEach { actualRule ->
            val isRepeatedRule = rules.any { it.ruleType == actualRule.ruleType}
            if (isRepeatedRule) throw BusinessRuleException("Regras repetidas")
        }
        val rulesConverted = rules.map {
            CustomRule(
                ruleType = it.ruleType,
                ruleContent = json.encodeToString<RuleInterface>(it),
                user = contentCreator
            )
        }.toList()
        return customRuleRepository.saveAll(rulesConverted)
    }

    @Transactional
    fun applyRules(videoOrderRequest: VideoOrderRequestDTO, contentCreatorId: Long): PixPagamento {
        val contentCreator = contentCreatorRepository.findById(contentCreatorId)
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

        val videoId = YoutubeUtil.extractVideoId(videoOrderRequest.link)

        val suggest = suggestRepository.save(
            Suggest(
                link = videoOrderRequest.link.toString(),
                contentCreator = contentCreator.get(),
                status = SuggestStatus.AGUARDANDO_PAGAMENTO,
                followerNick = videoOrderRequest.apelido,
                followerMessage = videoOrderRequest.mensagem,
                valor = charge.valor,
                expireAt = charge.expiracao
            )
        )
        pagamentoRepository.save(Pagamento(
            txid = charge.txId,
            status = StatusPagamento.PENDENTE,
            valor = charge.valor,
            suggest = suggest
        ))
        return charge
    }
}