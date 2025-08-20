package com.gabriel.danael_fernandes_react_manager.api

import com.gabriel.danael_fernandes_react_manager.YoutubeClientApi
import com.gabriel.danael_fernandes_react_manager.database.entity.OAuthCredential
import com.gabriel.danael_fernandes_react_manager.database.entity.Provider
import com.gabriel.danael_fernandes_react_manager.database.repository.OAuthCredentialRepository
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import java.util.*


@Component
class GoogleOAuthInitializer(
    private val oAuthCredentialRepository: OAuthCredentialRepository,
    private val youtube: YoutubeClientApi
) : ApplicationRunner {

    override fun run(args: ApplicationArguments) {
        var state = UUID.randomUUID()
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

            // Construir a URL de autorização
            val authUrl = youtube.linkAuthorization(state.toString())
            println("1. Copie e cole este link no seu navegador:")
            println(authUrl)
        }
    }
}