package com.gabriel.danael_fernandes_react_manager.core.authentication

interface Authentication {
    fun createUser(userAuth: UserAuthRegistration): UserAuth

    fun findUser(username: String): UserAuth?

    fun login(username: String, password: String): String
}