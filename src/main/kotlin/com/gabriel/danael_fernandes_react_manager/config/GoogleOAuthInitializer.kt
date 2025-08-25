package com.gabriel.danael_fernandes_react_manager.config

import com.gabriel.danael_fernandes_react_manager.api.controller.AuthController
import com.gabriel.danael_fernandes_react_manager.api.controller.ContentCreaterController
import com.gabriel.danael_fernandes_react_manager.api.controller.RuleController
import com.gabriel.danael_fernandes_react_manager.api.dto.ContentCreatorRegisterDTO
import com.gabriel.danael_fernandes_react_manager.core.authentication.Authentication
import com.gabriel.danael_fernandes_react_manager.core.authentication.UserAuthRegistration
import com.gabriel.danael_fernandes_react_manager.core.rule_processor.RuleType
import com.gabriel.danael_fernandes_react_manager.core.video.YoutubeAuthorization
import com.gabriel.danael_fernandes_react_manager.database.entity.ContentCreator
import com.gabriel.danael_fernandes_react_manager.database.entity.CustomRule
import com.gabriel.danael_fernandes_react_manager.database.entity.OAuthCredential
import com.gabriel.danael_fernandes_react_manager.database.entity.Provider
import com.gabriel.danael_fernandes_react_manager.database.repository.ContentCreatorRepository
import com.gabriel.danael_fernandes_react_manager.database.repository.CustomRuleRepository
import com.gabriel.danael_fernandes_react_manager.database.repository.OAuthCredentialRepository
import com.gabriel.danael_fernandes_react_manager.database.repository.SuggestRepository
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.util.*


@Component
class GoogleOAuthInitializer(
    private val oAuthCredentialRepository: OAuthCredentialRepository,
    private val youtube: YoutubeAuthorization
) : ApplicationRunner {

    override fun run(args: ApplicationArguments) {
        val state = UUID.randomUUID()
        val credentialsOp = oAuthCredentialRepository.findAll()
        if (credentialsOp.isEmpty()) {
            oAuthCredentialRepository.save(
                OAuthCredential(
                    id = state,
                    accessToken = "",
                    refreshToken = "",
                    type = Provider.YOUTUBE,
                    expiresAtInMiliSeconds = 0
                )
            )

            println("--- Credenciais do Google OAuth não encontradas! ---")
            println("Por favor, siga os passos abaixo para autorizar sua aplicação:")

            val authUrl = youtube.linkAuthorization(state.toString())
            println("1. Copie e cole este link no seu navegador:")
            println(authUrl)
        }
    }
}

@Profile("dev")
@Component
class InicialMockInfo(
    private val authentication: Authentication,
    private val suggestRepository: SuggestRepository,
    private val contentCreatorRepository: ContentCreatorRepository,
    private val contentCreatorController: ContentCreaterController,
    private val authController: AuthController,
    private val ruleRepository: CustomRuleRepository

): ApplicationRunner{
    override fun run(args: ApplicationArguments?) {
        val email = "a@email.com"
        val username = "a"
        val password = "123"

        authController.register(ContentCreatorRegisterDTO(email,password,username,username))
        val contentCreator = contentCreatorRepository.findByUsername(username)
        requireNotNull(contentCreator)
        ruleRepository.save(CustomRule(user = contentCreator, ruleType = RuleType.FIXED_PRICE, ruleContent = """{"type":"fixed_price","amount":"10.0"}"""))
    }
}