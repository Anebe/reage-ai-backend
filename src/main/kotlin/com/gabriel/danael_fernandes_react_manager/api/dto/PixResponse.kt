package com.gabriel.danael_fernandes_react_manager.api.dto

import com.gabriel.danael_fernandes_react_manager.core.pay.PixPagamento
import java.math.BigDecimal
import java.time.LocalDateTime

data class PixResponse(
    val pixCopiaECola: String,
    val qrCodeImageBase64: String,
    val valor: BigDecimal,
    val expiracao: LocalDateTime,
){
    constructor(pix: PixPagamento): this(
        pixCopiaECola = pix.pixCopiaECola,
        qrCodeImageBase64 = pix.qrCodeImageBase64,
        valor = pix.valor,
        expiracao = pix.expiracao,
    )
}
