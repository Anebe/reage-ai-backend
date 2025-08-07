package com.gabriel.danael_fernandes_react_manager.entity.rules

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
sealed interface RuleInterface {
    @Transient
    val ruleType: RuleType
    @Throws(RuleException::class)
    fun apply(video: Video, context: PricingContext)
}