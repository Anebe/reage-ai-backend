package com.gabriel.danael_fernandes_react_manager.controller

import com.gabriel.danael_fernandes_react_manager.entity.Streamer
import com.gabriel.danael_fernandes_react_manager.repository.StreamerRepository
import com.gabriel.danael_fernandes_react_manager.service.JwtUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


enum class UserRole(val role: String) {
    ADMIN("admin"),
    USER("user");
}

data class AuthenticationDTO(val email: String, val password: String)

data class RegisterDTO(
    val login: String,
    val email: String,
    val password: String,
    val role: UserRole
)

data class Token(val token: String)
@RestController
@RequestMapping("api/auth")
@CrossOrigin //TODO lembrar de tratar o cors corretamente
class AuthenticationController {

    @Autowired
    private lateinit var authenticationManager: AuthenticationManager
    @Autowired
    private lateinit var repository: StreamerRepository
    @Autowired
    private lateinit var tokenService: JwtUtil

    @PostMapping("/login")
    fun login(@RequestBody data: AuthenticationDTO): ResponseEntity<Token> {
        val usernamePassword = UsernamePasswordAuthenticationToken(data.email, data.password)
        val auth = this.authenticationManager.authenticate(usernamePassword) // Tenta autenticar o usuário

        val token = tokenService.generateToken(auth.principal as Streamer) // Gera o token JWT

        return ResponseEntity.ok(Token(token))
    }

    @PostMapping("/register")
    fun register(@RequestBody data: RegisterDTO): ResponseEntity<Void> {
        if(this.repository.findByUsername(data.login) != null) return ResponseEntity.badRequest().build() // Verifica se o login já existe

        val encryptedPassword = BCryptPasswordEncoder().encode(data.password) // Criptografa a senha
        val newUser = Streamer(username =  data.login, password =  encryptedPassword, role =  data.role, email = data.email)

        this.repository.save(newUser)

        return ResponseEntity.ok().build()
    }
}