package com.gabriel.danael_fernandes_react_manager.repository

import com.gabriel.danael_fernandes_react_manager.entity.rules.CustomRule
import com.gabriel.danael_fernandes_react_manager.entity.rules.RuleType
import com.gabriel.danael_fernandes_react_manager.entity.Streamer
import org.springframework.data.jpa.repository.JpaRepository

interface CustomRuleRepository: JpaRepository<CustomRule, Long> {

    fun findAllByUser(user: Streamer): List<CustomRule>
}