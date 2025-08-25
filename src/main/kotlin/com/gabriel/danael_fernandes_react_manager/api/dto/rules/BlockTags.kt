package com.gabriel.danael_fernandes_react_manager.api.dto.rules

import com.gabriel.danael_fernandes_react_manager.core.rule_processor.rule.RuleInterface

data class BlockTags(
    val tags: List<String>
): RuleInterfaceRequest {
    override fun to(): RuleInterface {
        TODO("Not yet implemented")
    }
}
