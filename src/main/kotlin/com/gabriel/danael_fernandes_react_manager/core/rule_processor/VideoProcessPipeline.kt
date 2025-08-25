package com.gabriel.danael_fernandes_react_manager.core.rule_processor

import com.gabriel.danael_fernandes_react_manager.core.video.VideoInfo
import com.gabriel.danael_fernandes_react_manager.database.entity.ResultVideoProcess
import com.gabriel.danael_fernandes_react_manager.core.rule_processor.rule.RuleInterface

class VideoProcessPipeline(
    rules: List<RuleInterface> = emptyList()
) {
    private val rules = mutableSetOf<RuleInterface>()

    init {
        rules.forEach {
            this.addRule(it)
        }
    }

    fun addRule(rule: RuleInterface) {
        val duplicateRule = rules.find { it.ruleType == rule.ruleType }
        if (duplicateRule != null) {
            rules.remove(duplicateRule)
        }
        rules.add(rule)
    }

    fun process(videoInfo: VideoInfo): ResultVideoProcess {
        val context = ResultVideoProcess()

        for (ruleType in rules) {
            ruleType.apply(videoInfo, context)
        }

        return context
    }
}