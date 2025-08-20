package com.gabriel.danael_fernandes_react_manager.video.rule

import com.gabriel.danael_fernandes_react_manager.Video
import com.gabriel.danael_fernandes_react_manager.database.entity.ResultVideoProcess
import com.gabriel.danael_fernandes_react_manager.video.RuleType
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
sealed interface RuleInterface {
    @Transient
    val ruleType: RuleType

    val isPriceRule: Boolean

    fun apply(video: Video, context: ResultVideoProcess)
}