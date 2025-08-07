package com.gabriel.danael_fernandes_react_manager.entity.rules

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("fixed_price")
class FixedPriceRule(private val amount: Double) : RuleInterface {
    override val ruleType = RuleType.FIXED_PRICE

    override fun apply(video: Video, context: PricingContext) {
        context.price = amount
    }
}