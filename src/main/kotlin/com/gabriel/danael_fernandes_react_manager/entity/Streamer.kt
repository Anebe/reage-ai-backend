package com.gabriel.danael_fernandes_react_manager.entity

import com.gabriel.danael_fernandes_react_manager.controller.UserRole
import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

@Entity
data class Streamer(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(unique = true)
    private val username: String,
    private val password: String,
    @Column(unique = true)
    private val email: String,
    var role: UserRole,

    @OneToOne
    var youTubeOAuth: OAuthCredential? = null

) : UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> =
        mutableListOf(GrantedAuthority { "ROLE_USER" })

    override fun getPassword(): String = password

    override fun getUsername(): String = username

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true
}