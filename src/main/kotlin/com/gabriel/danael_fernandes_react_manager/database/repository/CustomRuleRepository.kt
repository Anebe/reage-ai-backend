package com.gabriel.danael_fernandes_react_manager.database.repository

import com.gabriel.danael_fernandes_react_manager.database.entity.CustomRule
import com.gabriel.danael_fernandes_react_manager.database.entity.ContentCreator
import org.springframework.data.jpa.repository.JpaRepository

interface CustomRuleRepository: JpaRepository<CustomRule, Long> {

    fun findAllByUser(user: ContentCreator): List<CustomRule>
}