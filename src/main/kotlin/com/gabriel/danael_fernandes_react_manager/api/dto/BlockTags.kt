package com.gabriel.danael_fernandes_react_manager.api.dto

import com.gabriel.danael_fernandes_react_manager.video.rule.RuleInterface

data class BlockTags(
    val tags: List<String>
): RuleInterfaceRequest {
    override fun to(): RuleInterface {
        TODO("Not yet implemented")
    }
}
