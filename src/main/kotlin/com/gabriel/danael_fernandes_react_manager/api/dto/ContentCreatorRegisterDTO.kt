package com.gabriel.danael_fernandes_react_manager.api.dto

import com.gabriel.danael_fernandes_react_manager.core.authentication.UserAuthRegistration

data class ContentCreatorRegisterDTO(
    val email: String,
    val password: String,
    val username: String,
    val fullName: String,
){
    fun to() = UserAuthRegistration(
        email = this.email,
        password = this.password,
        username = this.username,
    )
}