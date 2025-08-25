package com.gabriel.danael_fernandes_react_manager.api.dto.rules

import com.gabriel.danael_fernandes_react_manager.core.rule_processor.rule.FixedPriceRule
import java.math.BigDecimal

data class FixedPriceResquestDTO(
    val price: String
): RuleInterfaceRequest {
    override fun to() = FixedPriceRule(
        amount = BigDecimal(price)
    )
}
