package com.gabriel.danael_fernandes_react_manager.config

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Profile
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.time.Instant
import java.util.*

@Component
@Profile("dev") // 👈 Este filtro só existirá no perfil "dev"
class MockJwtAuthFilter : OncePerRequestFilter() {

    private val objectMapper = jacksonObjectMapper()

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                val fakeTokenString = authHeader.substring(7)
                val payloadString = String(Base64.getUrlDecoder().decode(fakeTokenString.split('.')[1]))
                val claims: Map<String, Any> = objectMapper.readValue(payloadString)

                // --- INÍCIO DA CORREÇÃO ---

                // 1. Cria uma cópia mutável dos claims para podermos modificar
                val processedClaims = claims.toMutableMap()

                // 2. Converte os campos de timestamp de Número para Instant
                claims["iat"]?.let {
                    val iatSeconds = (it as Number).toLong()
                    processedClaims["iat"] = Instant.ofEpochSecond(iatSeconds)
                }
                claims["exp"]?.let {
                    val expSeconds = (it as Number).toLong()
                    processedClaims["exp"] = Instant.ofEpochSecond(expSeconds)
                }

                // 3. Cria o objeto Jwt "mockado" com os claims processados
                val jwt = Jwt.withTokenValue(fakeTokenString)
                    .header("alg", "none")
                    .claims { it.putAll(processedClaims) } // Usa o mapa corrigido
                    .build()

                // --- FIM DA CORREÇÃO ---
                val authorities = listOf(SimpleGrantedAuthority("ROLE_USER"))
                val authentication = JwtAuthenticationToken(jwt, authorities)
                SecurityContextHolder.getContext().authentication = authentication

                println("✅ MockJwtAuthFilter: Usuário '${claims["preferred_username"]}' autenticado com sucesso.")

            } catch (e: Exception) {
                SecurityContextHolder.clearContext()
                // Imprime a mensagem do erro original para ajudar a depurar
                println("❌ MockJwtAuthFilter: Erro ao processar token falso. ${e.message}")
            }
        }

        // Continua a cadeia de filtros
        filterChain.doFilter(request, response)
    }
}