package com.gabriel.danael_fernandes_react_manager

import com.gabriel.danael_fernandes_react_manager.controller.UserRole
import com.gabriel.danael_fernandes_react_manager.entity.*
import com.gabriel.danael_fernandes_react_manager.entity.rules.*
import com.gabriel.danael_fernandes_react_manager.repository.CustomRuleRepository
import com.gabriel.danael_fernandes_react_manager.repository.StreamerRepository
import com.gabriel.danael_fernandes_react_manager.service.RuleService
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class DanaelFernandesReactManagerApplicationTests {

	@Autowired
	private lateinit var customRuleRepository: CustomRuleRepository
	@Autowired
	private lateinit var ruleService: RuleService
	@Autowired
	private lateinit var json: Json
	@Autowired
	private lateinit var streamerRepository: StreamerRepository

	@Test
	fun contextLoads() {
		val key = java.util.Base64.getEncoder().encodeToString(ByteArray(64).apply { java.security.SecureRandom().nextBytes(this) })
		println(key)
	}


	@Test
	fun bbbbbb(){
		var streamer = Streamer(
			email = "a", role = UserRole.USER, username = "a", password = "a"
		)
		streamer = streamerRepository.save(streamer)
//		json.encodeToJsonElement(DurationValidationRule(1, 10))
		val rule1 = customRuleRepository.save(
			CustomRule(
				ruleContent = json.encodeToString<RuleInterface>(
					DurationValidationRule(1, 10)
				),
				ruleType = RuleType.RANGE_DURATION_ALLOWED,
				user = streamer
			)
		)

		val rule2 = customRuleRepository.save(
			CustomRule(
				ruleContent = json.encodeToString<RuleInterface>(
					FixedPriceRule(15.0)
				),
				ruleType = RuleType.RANGE_DURATION_ALLOWED,
				user = streamer
			)
		)

		val pipeline = VideoProcessPipeline()
		pipeline.addRule(json.decodeFromString(rule1.ruleContent))
		pipeline.addRule(json.decodeFromString(rule2.ruleContent))

		val videoBom =
			Video(5)      // Deve passar
		val videoRuim =
			Video(20)     // Deve falhar na validação de duração

		val precoFinalSucesso = pipeline.process(videoBom)
		if (precoFinalSucesso != null) {
			println("--> Preço final para o vídeo bom: R$$precoFinalSucesso")
		} else {
			println("--> Não foi possível calcular o preço para o vídeo bom.")
		}

		val precoFinalFalha = pipeline.process(videoRuim)
		if (precoFinalFalha != null) {
			println("--> Preço final para o vídeo ruim: R$$precoFinalFalha")
		} else {
			println("--> Não foi possível calcular o preço para o vídeo ruim.")
		}
	}
	@Test
	fun aaaa(){






		val pipeline = VideoProcessPipeline()
		pipeline.addRule(DurationValidationRule(1, 10)) // Vídeos de 1 a 10 min
		pipeline.addRule(FixedPriceRule(15.0))         // Preço fixo de R$ 15,00

		val videoBom =
			Video(5)      // Deve passar
		val videoRuim =
			Video(20)     // Deve falhar na validação de duração

		val precoFinalSucesso = pipeline.process(videoBom)
		if (precoFinalSucesso != null) {
			println("--> Preço final para o vídeo bom: R$$precoFinalSucesso")
		} else {
			println("--> Não foi possível calcular o preço para o vídeo bom.")
		}

		val precoFinalFalha = pipeline.process(videoRuim)
		if (precoFinalFalha != null) {
			println("--> Preço final para o vídeo ruim: R$$precoFinalFalha")
		} else {
			println("--> Não foi possível calcular o preço para o vídeo ruim.")
		}
	}

}
