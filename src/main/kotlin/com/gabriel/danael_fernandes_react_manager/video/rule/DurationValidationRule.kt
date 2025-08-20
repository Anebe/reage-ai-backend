package com.gabriel.danael_fernandes_react_manager.video.rule

import com.gabriel.danael_fernandes_react_manager.Video
import com.gabriel.danael_fernandes_react_manager.database.entity.ResultVideoProcess
import com.gabriel.danael_fernandes_react_manager.video.RuleType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("duration_validation")
class DurationValidationRule(
    private val minDuration: Int,
    private val maxDuration: Int
) : RuleInterface {

    override val ruleType = RuleType.RANGE_DURATION_ALLOWED
    override val isPriceRule = false

    override fun apply(video: Video, context: ResultVideoProcess) {

        if (video.duration.toMinutes() !in minDuration..maxDuration) {
            context.errors.add("A duração do vídeo (${video.duration} min) está fora da faixa permitida [${minDuration}-${maxDuration}].")
        }
    }
}