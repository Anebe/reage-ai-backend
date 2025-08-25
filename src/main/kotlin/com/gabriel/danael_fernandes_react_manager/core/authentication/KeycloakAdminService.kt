package com.gabriel.danael_fernandes_react_manager.core.authentication

import jakarta.ws.rs.core.Response
import org.keycloak.admin.client.Keycloak
import org.keycloak.representations.idm.CredentialRepresentation
import org.keycloak.representations.idm.UserRepresentation
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.InputStream
import java.nio.charset.StandardCharsets


class KeycloakAdminService(private val keycloak: Keycloak) : Authentication {

    @Value("\${keycloak.realm}")
    private lateinit var realmName: String

    @Value("\${keycloak.server-url}")
    private lateinit var serverUrl: String

    @Value("\${keycloak.clients.admin-cli.client-id}")
    private lateinit var clientId: String

    @Value("\${keycloak.clients.admin-cli.client-secret}")
    private lateinit var clientSecret: String

    override fun login(username: String, password: String): String {
        val tokenResponse = Keycloak.getInstance(
            serverUrl,
            realmName,
            username,
            password,
            clientId,
            clientSecret,
        ).tokenManager().accessToken //TODO arrumar pra retornar um token que já foi gerado anteriormente e ainda é valido

        return tokenResponse.token
    }

    override fun findUser(username: String): UserAuth? {
        val realmResource = keycloak.realm(realmName)
        val usersResource = realmResource.users()
        val userResource = usersResource.searchByUsername(username, true).firstOrNull()
        if (userResource == null) return null

        return UserAuth(userResource)
    }

    fun createUser(username: String, email: String, password: String) {
        val user = UserRepresentation().apply {
            this.username = username
            this.email = email
            this.isEnabled = true
        }
        val credential = CredentialRepresentation().apply {
            this.type = CredentialRepresentation.PASSWORD
            this.value = password
            this.isTemporary = false
        }
        user.credentials = mutableListOf(credential)

        val realmResource = keycloak.realm(realmName)
        val usersResource = realmResource.users()

        val response = usersResource.create(user)
        if (response.statusInfo.family != Response.Status.Family.SUCCESSFUL) {
            println("Erro na requisição. Status: ${response.status}")

            val responseBody = response.readEntity(InputStream::class.java).use {
                String(it.readBytes(), StandardCharsets.UTF_8)
            }

            println("Corpo da resposta de erro: $responseBody")
        }
        val userResource =
            usersResource.search(username).firstOrNull() ?: println("Usuário não encontrado após a criação")

    }

    override fun createUser(userAuth: UserAuthRegistration): UserAuth {
        val user = UserRepresentation().apply {
            this.username = userAuth.username
            this.email = userAuth.email
            this.isEnabled = true
        }
        val credential = CredentialRepresentation().apply {
            this.type = CredentialRepresentation.PASSWORD
            this.value = userAuth.password
            this.isTemporary = false
        }
        user.credentials = mutableListOf(credential)

        val realmResource = keycloak.realm(realmName)
        val usersResource = realmResource.users()

        val response = usersResource.create(user)
        if (response.statusInfo.family != Response.Status.Family.SUCCESSFUL) {
            println("Erro na requisição. Status: ${response.status}")

            val responseBody = response.readEntity(InputStream::class.java).use {
                String(it.readBytes(), StandardCharsets.UTF_8)
            }

            println("Corpo da resposta de erro: $responseBody")
            throw Exception("Usuário não cadastrado, problema interno!")
        }
        val userResource = usersResource.searchByEmail(userAuth.email, true).firstOrNull()
        userResource ?: throw Exception("Usuário não encontrado após a criação")

        return UserAuth(userResource)
    }
}

