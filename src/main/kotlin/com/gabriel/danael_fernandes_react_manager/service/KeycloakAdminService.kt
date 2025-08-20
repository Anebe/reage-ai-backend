package com.gabriel.danael_fernandes_react_manager.service

import jakarta.ws.rs.core.Response
import org.keycloak.admin.client.Keycloak
import org.keycloak.representations.idm.CredentialRepresentation
import org.keycloak.representations.idm.UserRepresentation
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.InputStream
import java.nio.charset.StandardCharsets

@Service
class KeycloakAdminService(private val keycloak: Keycloak) {

    //@Value("\${keycloak.realm}")
    private var realmName: String = "reage-ai"

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
        val userResource = usersResource.search(username).firstOrNull() ?: println("Usuário não encontrado após a criação")

    }
}