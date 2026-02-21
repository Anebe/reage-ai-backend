package com.gabriel.danael_fernandes_react_manager.database.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.util.*

@Entity
data class OAuthCredential(
    @Id
    val id: Long = 1,
    var accessToken: String,
    var refreshToken: String,
    var expiresAtInMiliSeconds: Long,
)