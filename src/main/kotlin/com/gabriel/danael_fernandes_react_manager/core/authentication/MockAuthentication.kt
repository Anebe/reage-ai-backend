package com.gabriel.danael_fernandes_react_manager.core.authentication

import org.springframework.context.annotation.Profile
import java.util.*


@Profile("dev")
class MockAuthentication : Authentication {

    private val users = mutableMapOf<String, Pair<UserAuth, String>>() // username -> (UserAuth, password)


    override fun createUser(userAuth: UserAuthRegistration): UserAuth {
        if (users.containsKey(userAuth.username)) {
            throw IllegalStateException("Usuário '${userAuth.username}' já existe.")
        }
        val newUser = UserAuth(
            username = userAuth.username,
            email = userAuth.email
        )
        users[userAuth.username] = Pair(newUser, userAuth.password)
        println("Mock: Usuário '${userAuth.username}' criado com sucesso.")
        return newUser
    }

    override fun findUser(username: String): UserAuth? {
        return users[username]?.first
    }

    override fun login(username: String, password: String): String {
        val userEntry = users[username]

        if (userEntry == null || userEntry.second != password) {
            throw SecurityException("Credenciais inválidas para o usuário '$username'.")
        }

        // Criamos um payload JSON real para o nosso token falso
        val claims = """
        {
            "sub": "${userEntry.first.email}",
            "preferred_username": "$username",
            "email": "${userEntry.first.email}",
            "iat": ${System.currentTimeMillis() / 1000},
            "exp": ${System.currentTimeMillis() / 1000 + 3600},
            "iss": "mock-issuer"
        }
    """.trimIndent()

        // O token falso agora é "header.payload.signature" com um payload real
        val header = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9".toByteArray().toBase64() // Header fixo
        val payload = claims.toByteArray().toBase64()
        val signature = "fake-signature".toByteArray().toBase64()

        println("Mock: Login bem-sucedido para '$username'. Gerando token falso com payload real.")
        return "$header.$payload.$signature"
    }

    private fun ByteArray.toBase64(): String = Base64.getUrlEncoder().withoutPadding().encodeToString(this)
}