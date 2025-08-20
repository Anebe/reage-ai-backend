package com.gabriel.danael_fernandes_react_manager.api.dto

import com.gabriel.danael_fernandes_react_manager.video.rule.DurationValidationRule

data class RangeDurationRequestDTO(
    val maxDurationInMinutes: Int,
    val minDurationInMinutes: Int
): RuleInterfaceRequest{
    override fun to() = DurationValidationRule(
        maxDuration = maxDurationInMinutes,
        minDuration = minDurationInMinutes
    )
}
