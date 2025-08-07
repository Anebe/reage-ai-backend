package com.gabriel.danael_fernandes_react_manager.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id


enum class Provider(type: String){
    YOUTUBE("youtube"),
    TWITCH("twitch")
}
@Entity
data class OAuthCredential(
    @Id @GeneratedValue
    val id: Long = 0,
    var accessToken: String,
    var refreshToken: String,
    var expiresAtInMiliSeconds: Long,
    val type: Provider
)
