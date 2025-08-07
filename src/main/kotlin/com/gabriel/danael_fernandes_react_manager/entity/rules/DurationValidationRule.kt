package com.gabriel.danael_fernandes_react_manager.entity.rules

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("duration_validation")
class DurationValidationRule(
    private val minDuration: Int,
    private val maxDuration: Int
) : RuleInterface {
    override val ruleType = RuleType.RANGE_DURATION_ALLOWED
    override fun apply(video: Video, context: PricingContext) {
        if (video.durationInMinutes !in minDuration..maxDuration) {
            throw RuleException("A duração do vídeo (${video.durationInMinutes} min) está fora da faixa permitida [${minDuration}-${maxDuration}].")
        }
    }
}