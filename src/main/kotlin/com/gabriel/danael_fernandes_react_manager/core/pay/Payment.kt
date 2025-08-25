package com.gabriel.danael_fernandes_react_manager.core.pay

import java.math.BigDecimal

interface Payment {
    fun charge(price: BigDecimal): PixPagamento
}