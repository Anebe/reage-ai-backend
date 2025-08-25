package com.gabriel.danael_fernandes_react_manager.core.rule_processor.rule

import com.gabriel.danael_fernandes_react_manager.core.video.VideoInfo
import com.gabriel.danael_fernandes_react_manager.database.entity.ResultVideoProcess
import com.gabriel.danael_fernandes_react_manager.core.rule_processor.RuleType
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
sealed interface RuleInterface {
    @Transient
    val ruleType: RuleType

    val isPriceRule: Boolean

    fun apply(videoInfo: VideoInfo, context: ResultVideoProcess)
}