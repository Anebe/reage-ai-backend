package com.gabriel.danael_fernandes_react_manager.video

import com.gabriel.danael_fernandes_react_manager.Video
import com.gabriel.danael_fernandes_react_manager.database.entity.ResultVideoProcess
import com.gabriel.danael_fernandes_react_manager.video.rule.RuleInterface

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

    fun process(video: Video): ResultVideoProcess {
        val context = ResultVideoProcess()

        for (ruleType in rules) {
            ruleType.apply(video, context)
        }

        return context
    }
}