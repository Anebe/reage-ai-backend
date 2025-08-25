package com.gabriel.danael_fernandes_react_manager.api.dto.rules

import kotlinx.serialization.Serializable

@Serializable
data class RuleRequestDTO (
    val rules: List<RuleInterfaceRequest>
)