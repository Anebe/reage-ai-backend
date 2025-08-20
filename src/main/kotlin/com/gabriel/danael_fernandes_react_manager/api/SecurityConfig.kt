package com.gabriel.danael_fernandes_react_manager.api

import com.gabriel.danael_fernandes_react_manager.pay.EfiBank
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
@Profile("dev")
class WebSecurityConfig {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests { requests ->
                requests
                    .requestMatchers("/rule/**").permitAll()
                    .requestMatchers(HttpMethod.POST, "/content-creator").permitAll()
                    .requestMatchers("/teste").permitAll()
                    .requestMatchers("/h2-console/**").permitAll() // Permite acesso ao console H2
                    .anyRequest().authenticated()
            }
            .oauth2Login(Customizer.withDefaults())
            .oauth2ResourceServer { it.jwt(Customizer.withDefaults()) }
            .csrf { it.disable() } // Para H2 Console
            .headers { it.frameOptions { frameOp -> frameOp.disable() } } // Para H2 Console

        return http.build()
    }
}

@Configuration
@EnableWebSecurity
@Profile("deaaav")
class WebSecurityConfigTest {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests { requests ->
                requests
                    .anyRequest().permitAll()
            }
            .csrf { it.disable() } // Para H2 Console
            .headers { it.frameOptions { frameOp -> frameOp.disable() } } // Para H2 Console

        return http.build()
    }
}