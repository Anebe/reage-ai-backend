package com.gabriel.danael_fernandes_react_manager.core.rule_processor.rule

import com.gabriel.danael_fernandes_react_manager.core.rule_processor.RuleType
import com.gabriel.danael_fernandes_react_manager.core.video.VideoInfo
import com.gabriel.danael_fernandes_react_manager.database.entity.ResultVideoProcess
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.time.LocalTime


enum class TimeType {
    SECOND,
    MINUTE,
    HOUR,
}
@Serializable
@SerialName("time_price")
class PriceTimeRule(@Contextual val price: BigDecimal, @Contextual val time: TimeType,

) : RuleInterface {
    override val ruleType = RuleType.TIME_PRICE
    override val isPriceRule = true
    override fun apply(videoInfo: VideoInfo, context: ResultVideoProcess) {
        var totalTime = when(time) {

            TimeType.SECOND -> videoInfo.duration.toSeconds()
            TimeType.MINUTE -> videoInfo.duration.toMinutes()
            TimeType.HOUR -> videoInfo.duration.toHours()
        }

        context.price = price.multiply(BigDecimal.valueOf(totalTime))
    }
}