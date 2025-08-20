package com.gabriel.danael_fernandes_react_manager.api.dto

import java.net.URL

data class VideoOrderRequestDTO(
    val apelido: String,
    val link: URL,
    val mensagem: String,
    val contentCreatorId: Long
)