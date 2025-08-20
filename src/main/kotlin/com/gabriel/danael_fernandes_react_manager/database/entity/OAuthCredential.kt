package com.gabriel.danael_fernandes_react_manager.database.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.util.*

enum class Provider(type: String){
    YOUTUBE("youtube"),
    TWITCH("twitch")
}
@Entity
data class OAuthCredential(
    @Id
    val id: UUID = UUID.randomUUID(),
    var accessToken: String,
    var refreshToken: String,
    var expiresAtInMiliSeconds: Long,
    val type: Provider
)