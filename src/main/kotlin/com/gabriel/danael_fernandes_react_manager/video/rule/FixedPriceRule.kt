package com.gabriel.danael_fernandes_react_manager.video.rule

import com.gabriel.danael_fernandes_react_manager.Video
import com.gabriel.danael_fernandes_react_manager.database.entity.ResultVideoProcess
import com.gabriel.danael_fernandes_react_manager.video.BigDecimalSerializer
import com.gabriel.danael_fernandes_react_manager.video.RuleType
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.math.BigDecimal

@Serializable
@SerialName("fixed_price")
class FixedPriceRule(@Contextual private val amount: BigDecimal) : RuleInterface {
    override val ruleType = RuleType.FIXED_PRICE
    override val isPriceRule = true

    override fun apply(video: Video, context: ResultVideoProcess) {
        context.price = amount
    }
}