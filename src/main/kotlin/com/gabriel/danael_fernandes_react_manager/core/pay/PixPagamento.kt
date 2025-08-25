package com.gabriel.danael_fernandes_react_manager.core.pay

import java.math.BigDecimal
import java.time.LocalDateTime

data class PixPagamento(
    val pixCopiaECola: String,
    val qrCodeImageBase64: String,
    val valor: BigDecimal,
    val expiracao: LocalDateTime,
    val txId: String,
)