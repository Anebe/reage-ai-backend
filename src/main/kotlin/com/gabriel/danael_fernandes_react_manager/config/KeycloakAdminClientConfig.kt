package com.gabriel.danael_fernandes_react_manager.config

import com.gabriel.danael_fernandes_react_manager.core.authentication.Authentication
import com.gabriel.danael_fernandes_react_manager.core.authentication.KeycloakAdminService
import com.gabriel.danael_fernandes_react_manager.core.authentication.MockAuthentication
import org.keycloak.OAuth2Constants
import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.KeycloakBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile


@Configuration
class KeycloakAdminClientConfig(
    @Value("\${keycloak.server-url}")
    private val serverUrl: String,

    @Value("\${keycloak.realm}")
    private val realm: String,

    @Value("\${keycloak.clients.admin-cli.client-id}")
    private val clientId: String,

    @Value("\${keycloak.clients.admin-cli.client-secret}")
    private val clientSecret: String,
) {
    @Bean
    fun keycloak(): Keycloak {
        return KeycloakBuilder.builder()
            .serverUrl(serverUrl)
            .realm(realm)
//            .username(username)
//            .password(password)
//            .grantType("password")
            .clientId(clientId)
            .clientSecret(clientSecret)
            .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
            .build()
    }

    @Bean
    @Profile("dev")
    fun mockAuth(): Authentication{
        return MockAuthentication()
    }

    @Bean
    @Profile("prod")
    fun auth(keycloak: Keycloak): Authentication{
        return KeycloakAdminService(keycloak)
    }
}