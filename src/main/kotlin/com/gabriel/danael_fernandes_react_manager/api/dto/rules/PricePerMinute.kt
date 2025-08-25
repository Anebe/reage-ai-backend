package com.gabriel.danael_fernandes_react_manager.api.dto.rules

import com.gabriel.danael_fernandes_react_manager.core.rule_processor.rule.RuleInterface

data class PricePerMinute(
    val minute: Int,
    val price: Double
): RuleInterfaceRequest {
    override fun to(): RuleInterface {
        TODO("Not yet implemented")
    }
}
