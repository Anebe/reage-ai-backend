package com.gabriel.danael_fernandes_react_manager.core.rule_processor.rule

import com.gabriel.danael_fernandes_react_manager.core.rule_processor.RuleType
import com.gabriel.danael_fernandes_react_manager.core.video.VideoInfo
import com.gabriel.danael_fernandes_react_manager.database.entity.ResultVideoProcess
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("max_total_video")
class LimitQtdRule(val qtdVideoLimit: Long) : RuleInterface{
    override val ruleType = RuleType.MAX_TOTAL_VIDEO
    override val isPriceRule = false

    override fun apply(videoInfo: VideoInfo, context: ResultVideoProcess) {
        // TODO elaborar a regra
    }
}