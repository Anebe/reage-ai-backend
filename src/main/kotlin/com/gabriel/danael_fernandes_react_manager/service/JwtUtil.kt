package com.gabriel.danael_fernandes_react_manager.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset


@Service
class JwtUtil {
    @Value("\${jwt.secret}")
    private lateinit var secret: String

    fun generateToken(user: UserDetails): String {
        val algorithm: Algorithm = Algorithm.HMAC256(secret)
        val token: String = JWT.create()
            .withIssuer("auth-api")
            .withSubject(user.username)
            .withExpiresAt(genExpirationDate())
            .sign(algorithm)
        return token
    }

    fun validateToken(token: String): String? {
        val algorithm = Algorithm.HMAC256(secret)
        return JWT.require(algorithm)
            .withIssuer("auth-api")
            .build()
            .verify(token)
            .subject
    }

    private fun genExpirationDate(): Instant {
        return LocalDateTime.now().plusHours(1).toInstant(ZoneOffset.of("-03:00"))
    }
}
