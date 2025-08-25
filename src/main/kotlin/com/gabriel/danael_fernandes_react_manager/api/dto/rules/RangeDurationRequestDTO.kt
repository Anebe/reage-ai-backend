package com.gabriel.danael_fernandes_react_manager.api.dto.rules

import com.gabriel.danael_fernandes_react_manager.core.rule_processor.rule.DurationValidationRule

data class RangeDurationRequestDTO(
    val maxDurationInMinutes: Int,
    val minDurationInMinutes: Int
): RuleInterfaceRequest {
    override fun to() = DurationValidationRule(
        maxDurationInMinutes = maxDurationInMinutes,
        minDurationInMinutes = minDurationInMinutes
    )
}
