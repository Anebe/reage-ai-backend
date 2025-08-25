package com.gabriel.danael_fernandes_react_manager.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer.AuthorizationManagerRequestMatcherRegistry
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Configuration
@EnableWebSecurity
@Profile("prod")
class WebSecurityConfig {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests { requests ->
                requests
                    .requestMatchers("/api/auth/**").permitAll()
                    .requestMatchers("/h2-console/**").permitAll() // Permite acesso ao console H2
                    .requestMatchers("/api/webhook/**").permitAll()
                    .anyRequest().authenticated()
            }
            .cors(Customizer.withDefaults())
            .oauth2Login(Customizer.withDefaults())
            .oauth2ResourceServer { it.jwt(Customizer.withDefaults()) }
            .csrf { it.disable() } // Para H2 Console
            .headers { it.frameOptions { frameOp -> frameOp.disable() } } // Para H2 Console

        return http.build()
    }
}

@Configuration
@EnableWebSecurity
@Profile("dev")
class WebSecurityConfigTest {

    @Bean
    fun securityFilterChain(http: HttpSecurity, mockJwtAuthFilter: MockJwtAuthFilter): SecurityFilterChain {
        http
            .authorizeHttpRequests { requests ->
                requests
                    .requestMatchers("/api/auth/**").permitAll()
                    .requestMatchers("/h2-console/**").permitAll() // Permite acesso ao console H2
                    .requestMatchers("/api/webhook/**").permitAll()
                    .requestMatchers("/api/suggest/{username}").permitAll()

                    .anyRequest().authenticated()
            }
            .addFilterBefore(mockJwtAuthFilter, UsernamePasswordAuthenticationFilter::class.java)

            .cors(Customizer.withDefaults()) // TODO TRATAR MELHOR para permitir o front-end e webhook
            .csrf { it.disable() } // Para H2 Console
            .headers { it.frameOptions { frameOp -> frameOp.disable() } } // Para H2 Console

        return http.build()
    }
}

@Configuration
class CorsConfig {
    @Bean
    fun corsConfigurer(): WebMvcConfigurer {
        return object : WebMvcConfigurer {
            override fun addCorsMappings(registry: CorsRegistry) {
                registry.addMapping("/api/**") // Aplica o CORS a todos os endpoints que começam com /api/
                    .allowedOrigins(
                        "http://localhost:3000",
                    ) // URLs do frontend permitidas
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Métodos HTTP permitidos
                    .allowedHeaders("*") // Headers permitidos na requisição
                    .allowCredentials(true) // Permite o envio de credenciais (cookies, tokens de autorização)
            }
        }
    }
}