package com.gabriel.danael_fernandes_react_manager.service

import com.gabriel.danael_fernandes_react_manager.repository.StreamerRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class AuthorizationService : UserDetailsService {

    @Autowired
    private lateinit var repository: StreamerRepository

    override fun loadUserByUsername(username: String): UserDetails {
        return repository.findByUsername(username)
            ?: throw UsernameNotFoundException("User not found with login: $username")
    }
}