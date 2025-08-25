package com.gabriel.danael_fernandes_react_manager.core.rule_processor.rule

import com.gabriel.danael_fernandes_react_manager.core.video.VideoInfo
import com.gabriel.danael_fernandes_react_manager.database.entity.ResultVideoProcess
import com.gabriel.danael_fernandes_react_manager.core.rule_processor.RuleType
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.math.BigDecimal

@Serializable
@SerialName("fixed_price")
class FixedPriceRule(@Contextual val amount: BigDecimal) : RuleInterface {
    override val ruleType = RuleType.FIXED_PRICE
    override val isPriceRule = true

    override fun apply(videoInfo: VideoInfo, context: ResultVideoProcess) {
        context.price = amount
    }
}