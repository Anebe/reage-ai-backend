package com.gabriel.danael_fernandes_react_manager.core.rule_processor.rule

import com.gabriel.danael_fernandes_react_manager.core.video.VideoInfo
import com.gabriel.danael_fernandes_react_manager.database.entity.ResultVideoProcess
import com.gabriel.danael_fernandes_react_manager.core.rule_processor.RuleType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("duration_validation")
class DurationValidationRule(
    val minDurationInMinutes: Int,
    val maxDurationInMinutes: Int
) : RuleInterface {

    override val ruleType = RuleType.RANGE_DURATION_ALLOWED
    override val isPriceRule = false

    override fun apply(videoInfo: VideoInfo, context: ResultVideoProcess) {

        if (videoInfo.duration.toMinutes() !in minDurationInMinutes..maxDurationInMinutes) {
            context.errors.add("A duração do vídeo (${videoInfo.duration} min) está fora da faixa permitida [${minDurationInMinutes}-${maxDurationInMinutes}].")
        }
    }
}