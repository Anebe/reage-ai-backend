package com.gabriel.danael_fernandes_react_manager.entity.rules

class VideoProcessPipeline {
    private val rules = mutableSetOf<RuleInterface>()

    fun addRule(rule: RuleInterface) {
        val duplicateRule = rules.find { it.ruleType == rule.ruleType }
        if (duplicateRule != null) {
            rules.remove(duplicateRule)
        }
        rules.add(rule)
    }

    fun process(video: Video): Double? {
        val context = PricingContext()

        return try {
            for (ruleType in rules) {
                ruleType.apply(video, context)
            }
            context.price
        } catch (e: RuleException) {
            null
        }
    }
}