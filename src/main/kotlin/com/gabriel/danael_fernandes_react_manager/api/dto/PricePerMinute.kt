package com.gabriel.danael_fernandes_react_manager.api.dto

import com.gabriel.danael_fernandes_react_manager.video.rule.RuleInterface

data class PricePerMinute(
    val minute: Int,
    val price: Double
): RuleInterfaceRequest {
    override fun to(): RuleInterface {
        TODO("Not yet implemented")
    }
}
