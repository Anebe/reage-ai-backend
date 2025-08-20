package com.gabriel.danael_fernandes_react_manager.api.dto

import com.gabriel.danael_fernandes_react_manager.video.rule.FixedPriceRule
import java.math.BigDecimal

data class FixedPriceResquestDTO(
    val price: String
): RuleInterfaceRequest{
    override fun to() = FixedPriceRule(
        amount = BigDecimal(price)
    )
}
