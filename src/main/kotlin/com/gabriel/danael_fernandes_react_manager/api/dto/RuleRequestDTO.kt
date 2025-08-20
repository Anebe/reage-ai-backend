package com.gabriel.danael_fernandes_react_manager.api.dto

import com.gabriel.danael_fernandes_react_manager.video.rule.RuleInterface
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable

@Serializable
data class RuleRequestDTO (
    val contentCreatorId: Long,
    val rules: List<RuleInterfaceRequest>
)